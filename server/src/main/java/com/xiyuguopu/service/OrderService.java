package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.common.BusinessException;
import com.xiyuguopu.dto.CreateOrderDTO;
import com.xiyuguopu.dto.UserOrderVO;
import com.xiyuguopu.entity.Address;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.entity.PackageDef;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.mapper.AddressMapper;
import com.xiyuguopu.mapper.OrderHeadMapper;
import com.xiyuguopu.mapper.OrderItemMapper;
import com.xiyuguopu.mapper.PackageDefMapper;
import com.xiyuguopu.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderHeadMapper orderHeadMapper;
    private final OrderItemMapper orderItemMapper;
    private final AddressMapper addressMapper;
    private final ProductMapper productMapper;
    private final PackageDefMapper packageDefMapper;
    private final OrderAssembler orderAssembler;
    private final InventoryService inventoryService;
    private final OrderStatusService orderStatusService;

    @Transactional
    public OrderHead create(Long userId, CreateOrderDTO dto) {
        Address addr = addressMapper.selectById(dto.getAddressId());
        if (addr == null || !addr.getUserId().equals(userId)) {
            throw BusinessException.notFound("收货地址不存在");
        }

        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("name", addr.getName());
        snapshot.put("phone", addr.getPhone());
        snapshot.put("province", addr.getProvince());
        snapshot.put("city", addr.getCity());
        snapshot.put("district", addr.getDistrict());
        snapshot.put("detail", addr.getDetail());

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderDTO.OrderItemDTO itemDTO : dto.getItems()) {
            int quantity = itemDTO.getQuantity() == null ? 0 : itemDTO.getQuantity();
            if (quantity <= 0) {
                throw BusinessException.badRequest("商品数量不合法");
            }

            String itemType = itemDTO.getItemType() == null || itemDTO.getItemType().isBlank()
                    ? "PRODUCT"
                    : itemDTO.getItemType().toUpperCase();

            if ("PACKAGE".equals(itemType)) {
                OrderItem item = buildPackageOrderItem(itemDTO, quantity);
                orderItems.add(item);
                total = total.add(item.getSubtotal());
            } else {
                OrderItem item = buildProductOrderItem(itemDTO, quantity);
                orderItems.add(item);
                total = total.add(item.getSubtotal());
            }
        }

        OrderHead head = new OrderHead();
        head.setOrderNo(newOrderNo());
        head.setUserId(userId);
        head.setAddressId(addr.getId());
        head.setAddressSnapshot(snapshot);
        head.setTotalAmount(total);
        head.setStatus(OrderStatusService.UNPAID);
        head.setRemark(dto.getRemark() != null ? dto.getRemark() : "");
        orderHeadMapper.insert(head);

        for (OrderItem item : orderItems) {
            item.setOrderId(head.getId());
            orderItemMapper.insert(item);
        }

        return head;
    }

    public Page<UserOrderVO> list(Long userId, int page, int size) {
        LambdaQueryWrapper<OrderHead> qw = new LambdaQueryWrapper<OrderHead>()
                .eq(OrderHead::getUserId, userId)
                .orderByDesc(OrderHead::getCreatedAt);
        Page<OrderHead> headPage = orderHeadMapper.selectPage(new Page<>(page, size), qw);
        List<OrderHead> heads = headPage.getRecords();

        List<Long> orderIds = heads.stream().map(OrderHead::getId).collect(Collectors.toList());
        List<OrderItem> allItems = orderIds.isEmpty()
                ? Collections.emptyList()
                : orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().in(OrderItem::getOrderId, orderIds));
        Map<Long, List<OrderItem>> itemMap = allItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        List<UserOrderVO> vos = heads.stream()
                .map(head -> orderAssembler.toUserVO(head, itemMap.getOrDefault(head.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        Page<UserOrderVO> voPage = new Page<>(page, size, headPage.getTotal());
        voPage.setRecords(vos);
        return voPage;
    }

    public UserOrderVO detail(Long userId, Long orderId) {
        OrderHead head = getOwnOrder(userId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        return orderAssembler.toUserVO(head, items);
    }

    @Transactional
    public void cancel(Long userId, Long orderId) {
        OrderHead head = getOwnOrder(userId, orderId);
        orderStatusService.transition(head, OrderStatusService.CANCELLED);

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            inventoryService.restore(item);
        }

        orderHeadMapper.updateById(head);
    }

    private OrderItem buildPackageOrderItem(CreateOrderDTO.OrderItemDTO itemDTO, int quantity) {
        if (itemDTO.getPackageCode() == null || itemDTO.getPackageCode().isBlank()) {
            throw BusinessException.badRequest("套餐编码不能为空");
        }
        PackageDef pkg = packageDefMapper.selectOne(
                new LambdaQueryWrapper<PackageDef>().eq(PackageDef::getCode, itemDTO.getPackageCode()));
        if (pkg == null) {
            throw BusinessException.notFound("套餐不存在: code=" + itemDTO.getPackageCode());
        }

        inventoryService.deductPackage(pkg, quantity);

        OrderItem item = new OrderItem();
        item.setItemType("PACKAGE");
        item.setPackageCode(pkg.getCode());
        item.setProductName(pkg.getName());
        item.setPrice(BigDecimal.valueOf(pkg.getPrice()));
        item.setQuantity(quantity);
        item.setSubtotal(BigDecimal.valueOf(pkg.getPrice()).multiply(BigDecimal.valueOf(quantity)));
        return item;
    }

    private OrderItem buildProductOrderItem(CreateOrderDTO.OrderItemDTO itemDTO, int quantity) {
        if (itemDTO.getProductId() == null) {
            throw BusinessException.badRequest("商品ID不能为空");
        }
        Product product = productMapper.selectById(itemDTO.getProductId());
        if (product == null) {
            throw BusinessException.notFound("商品不存在: id=" + itemDTO.getProductId());
        }

        inventoryService.deductProduct(product, quantity);

        OrderItem item = new OrderItem();
        item.setItemType("PRODUCT");
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setPrice(BigDecimal.valueOf(product.getPrice()));
        item.setQuantity(quantity);
        item.setSubtotal(BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity)));
        return item;
    }

    private OrderHead getOwnOrder(Long userId, Long orderId) {
        OrderHead head = orderHeadMapper.selectById(orderId);
        if (head == null) {
            throw BusinessException.notFound("订单不存在");
        }
        if (!head.getUserId().equals(userId)) {
            throw BusinessException.forbidden("无权查看该订单");
        }
        return head;
    }

    private String newOrderNo() {
        return "XG" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
    }
}
