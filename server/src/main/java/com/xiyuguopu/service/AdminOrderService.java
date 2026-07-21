package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.dto.AdminOrderVO;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.mapper.OrderHeadMapper;
import com.xiyuguopu.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理后台 — 订单管理
 */
@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderHeadMapper orderHeadMapper;
    private final OrderItemMapper orderItemMapper;

    /**
     * 分页列表，按状态筛选，按创建时间倒序
     */
    public Page<OrderHead> list(int page, int size, String status) {
        LambdaQueryWrapper<OrderHead> qw = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            qw.eq(OrderHead::getStatus, status);
        }
        qw.orderByDesc(OrderHead::getCreatedAt);
        return orderHeadMapper.selectPage(new Page<>(page, size), qw);
    }

    /**
     * 订单详情（含明细 + 地址快照）
     */
    public AdminOrderVO detail(Long id) {
        OrderHead head = orderHeadMapper.selectById(id);
        if (head == null) {
            throw new RuntimeException("订单不存在");
        }

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));

        List<AdminOrderVO.OrderItemVO> itemVOs = items.stream()
                .map(i -> AdminOrderVO.OrderItemVO.builder()
                        .itemType(i.getItemType())
                        .productId(i.getProductId())
                        .packageCode(i.getPackageCode())
                        .productName(i.getProductName())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .subtotal(i.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return AdminOrderVO.builder()
                .id(head.getId())
                .orderNo(head.getOrderNo())
                .userId(head.getUserId())
                .status(head.getStatus())
                .totalAmount(head.getTotalAmount())
                .remark(head.getRemark())
                .addressSnapshot(head.getAddressSnapshot())
                .items(itemVOs)
                .paidAt(head.getPaidAt())
                .shippedAt(head.getShippedAt())
                .completedAt(head.getCompletedAt())
                .cancelledAt(head.getCancelledAt())
                .createdAt(head.getCreatedAt())
                .trackingNumber(head.getTrackingNumber())
                .shippingCompany(head.getShippingCompany())
                .build();
    }

    /**
     * 变更状态，自动填对应时间戳；发货时设置物流信息
     */
    public void updateStatus(Long id, String status, String trackingNumber, String shippingCompany) {
        OrderHead head = orderHeadMapper.selectById(id);
        if (head == null) {
            throw new RuntimeException("订单不存在");
        }

        switch (status) {
            case "SHIPPED":
                head.setShippedAt(LocalDateTime.now());
                head.setTrackingNumber(trackingNumber != null ? trackingNumber : "");
                head.setShippingCompany(shippingCompany != null ? shippingCompany : "");
                break;
            case "COMPLETED":
                head.setCompletedAt(LocalDateTime.now());
                break;
            case "CANCELLED":
                head.setCancelledAt(LocalDateTime.now());
                break;
            default:
                throw new RuntimeException("不支持的状态: " + status);
        }

        head.setStatus(status);
        orderHeadMapper.updateById(head);
    }
}
