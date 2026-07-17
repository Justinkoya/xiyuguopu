package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.dto.CreateOrderDTO;
import com.xiyuguopu.dto.UserOrderVO;
import com.xiyuguopu.entity.*;
import com.xiyuguopu.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户端订单
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderHeadMapper orderHeadMapper;
    private final OrderItemMapper orderItemMapper;
    private final AddressMapper addressMapper;
    private final ProductMapper productMapper;

    /**
     * 创建订单
     */
    public OrderHead create(Long userId, CreateOrderDTO dto) {
        // 1. 校验地址
        Address addr = addressMapper.selectById(dto.getAddressId());
        if (addr == null || !addr.getUserId().equals(userId)) {
            throw new RuntimeException("收货地址不存在");
        }

        // 2. 构建地址快照
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("name", addr.getName());
        snapshot.put("phone", addr.getPhone());
        snapshot.put("province", addr.getProvince());
        snapshot.put("city", addr.getCity());
        snapshot.put("district", addr.getDistrict());
        snapshot.put("detail", addr.getDetail());

        // 3. 校验商品并计算金额
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderDTO.OrderItemDTO itemDTO : dto.getItems()) {
            Product p = productMapper.selectById(itemDTO.getProductId());
            if (p == null) {
                throw new RuntimeException("商品不存在: id=" + itemDTO.getProductId());
            }
            if (p.getStock() == null || p.getStock() < itemDTO.getQuantity()) {
                throw new RuntimeException("「" + p.getName() + "」库存不足");
            }

            OrderItem item = new OrderItem();
            item.setProductId(p.getId());
            item.setProductName(p.getName());
            item.setPrice(BigDecimal.valueOf(p.getPrice()));
            item.setQuantity(itemDTO.getQuantity());
            item.setSubtotal(BigDecimal.valueOf(p.getPrice()).multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            orderItems.add(item);

            total = total.add(item.getSubtotal());
        }

        // 4. 生成订单号
        String orderNo = "XG" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));

        // 5. 插入订单主表
        OrderHead head = new OrderHead();
        head.setOrderNo(orderNo);
        head.setUserId(userId);
        head.setAddressId(addr.getId());
        head.setAddressSnapshot(snapshot);
        head.setTotalAmount(total);
        head.setStatus("UNPAID");
        head.setRemark(dto.getRemark() != null ? dto.getRemark() : "");
        orderHeadMapper.insert(head);

        // 6. 插入订单明细
        for (OrderItem item : orderItems) {
            item.setOrderId(head.getId());
            orderItemMapper.insert(item);
        }

        return head;
    }

    /**
     * 我的订单列表（含商品明细）
     */
    public Page<UserOrderVO> list(Long userId, int page, int size) {
        // 1. 分页查订单主表
        LambdaQueryWrapper<OrderHead> qw = new LambdaQueryWrapper<OrderHead>()
                .eq(OrderHead::getUserId, userId)
                .orderByDesc(OrderHead::getCreatedAt);
        Page<OrderHead> headPage = orderHeadMapper.selectPage(new Page<>(page, size), qw);
        List<OrderHead> heads = headPage.getRecords();

        // 2. 批量查所有订单的商品明细
        List<Long> orderIds = heads.stream().map(OrderHead::getId).collect(Collectors.toList());
        List<OrderItem> allItems = orderIds.isEmpty()
                ? Collections.emptyList()
                : orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().in(OrderItem::getOrderId, orderIds));
        Map<Long, List<OrderItem>> itemMap = allItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        // 3. 组装 VO
        List<UserOrderVO> vos = heads.stream().map(head -> {
            List<OrderItem> items = itemMap.getOrDefault(head.getId(), Collections.emptyList());
            List<UserOrderVO.OrderItemVO> itemVOs = items.stream()
                    .map(i -> UserOrderVO.OrderItemVO.builder()
                            .productId(i.getProductId())
                            .productName(i.getProductName())
                            .price(i.getPrice())
                            .quantity(i.getQuantity())
                            .subtotal(i.getSubtotal())
                            .build())
                    .collect(Collectors.toList());

            return UserOrderVO.builder()
                    .id(head.getId())
                    .orderNo(head.getOrderNo())
                    .userId(head.getUserId())
                    .status(head.getStatus())
                    .totalAmount(head.getTotalAmount())
                    .remark(head.getRemark())
                    .addressSnapshot(head.getAddressSnapshot())
                    .wxTransactionId(head.getWxTransactionId())
                    .items(itemVOs)
                    .paidAt(head.getPaidAt())
                    .shippedAt(head.getShippedAt())
                    .completedAt(head.getCompletedAt())
                    .cancelledAt(head.getCancelledAt())
                    .createdAt(head.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());

        // 4. 构造分页结果
        Page<UserOrderVO> voPage = new Page<>(page, size, headPage.getTotal());
        voPage.setRecords(vos);
        return voPage;
    }

    /**
     * 订单详情
     */
    public UserOrderVO detail(Long userId, Long orderId) {
        OrderHead head = getOwnOrder(userId, orderId);

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));

        List<UserOrderVO.OrderItemVO> itemVOs = items.stream()
                .map(i -> UserOrderVO.OrderItemVO.builder()
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .subtotal(i.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return UserOrderVO.builder()
                .id(head.getId())
                .orderNo(head.getOrderNo())
                .userId(head.getUserId())
                .status(head.getStatus())
                .totalAmount(head.getTotalAmount())
                .remark(head.getRemark())
                .addressSnapshot(head.getAddressSnapshot())
                .wxTransactionId(head.getWxTransactionId())
                .items(itemVOs)
                .paidAt(head.getPaidAt())
                .shippedAt(head.getShippedAt())
                .completedAt(head.getCompletedAt())
                .cancelledAt(head.getCancelledAt())
                .createdAt(head.getCreatedAt())
                .build();
    }

    /**
     * 取消订单（仅 UNPAID）
     */
    public void cancel(Long userId, Long orderId) {
        OrderHead head = getOwnOrder(userId, orderId);
        if (!"UNPAID".equals(head.getStatus())) {
            throw new RuntimeException("仅待支付订单可取消");
        }
        head.setStatus("CANCELLED");
        head.setCancelledAt(LocalDateTime.now());
        orderHeadMapper.updateById(head);
    }

    // ---- 私有工具 ----

    private OrderHead getOwnOrder(Long userId, Long orderId) {
        OrderHead head = orderHeadMapper.selectById(orderId);
        if (head == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!head.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看该订单");
        }
        return head;
    }
}
