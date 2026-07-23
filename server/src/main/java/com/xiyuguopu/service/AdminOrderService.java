package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.common.BusinessException;
import com.xiyuguopu.dto.AdminOrderVO;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.mapper.OrderHeadMapper;
import com.xiyuguopu.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderHeadMapper orderHeadMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderAssembler orderAssembler;
    private final OrderStatusService orderStatusService;
    private final InventoryService inventoryService;

    public Page<OrderHead> list(int page, int size, String status) {
        LambdaQueryWrapper<OrderHead> qw = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            qw.eq(OrderHead::getStatus, status);
        }
        qw.orderByDesc(OrderHead::getCreatedAt);
        return orderHeadMapper.selectPage(new Page<>(page, size), qw);
    }

    public AdminOrderVO detail(Long id) {
        OrderHead head = orderHeadMapper.selectById(id);
        if (head == null) {
            throw BusinessException.notFound("订单不存在");
        }

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
        return orderAssembler.toAdminVO(head, items);
    }

    @Transactional
    public void updateStatus(Long id, String status, String trackingNumber, String shippingCompany) {
        OrderHead head = orderHeadMapper.selectById(id);
        if (head == null) {
            throw BusinessException.notFound("订单不存在");
        }

        String normalizedStatus = status == null ? "" : status.trim().toUpperCase();
        switch (normalizedStatus) {
            case OrderStatusService.SHIPPED:
                String normalizedTrackingNumber = trackingNumber == null ? "" : trackingNumber.trim();
                String normalizedShippingCompany = shippingCompany == null ? "" : shippingCompany.trim();
                if (normalizedTrackingNumber.isBlank()) {
                    throw BusinessException.badRequest("物流单号不能为空");
                }
                if (normalizedShippingCompany.isBlank()) {
                    throw BusinessException.badRequest("快递公司不能为空");
                }
                head.setTrackingNumber(normalizedTrackingNumber);
                head.setShippingCompany(normalizedShippingCompany);
                break;
            case OrderStatusService.COMPLETED:
                break;
            case OrderStatusService.CANCELLED:
                restoreInventoryAndSales(id);
                break;
            default:
                throw BusinessException.badRequest("不支持的状态: " + status);
        }

        orderStatusService.transition(head, normalizedStatus);
        orderHeadMapper.updateById(head);
    }

    private void restoreInventoryAndSales(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            inventoryService.restore(item);
            inventoryService.decrementSale(item);
        }
    }
}
