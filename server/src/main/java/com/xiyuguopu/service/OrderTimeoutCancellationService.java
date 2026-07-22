package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.mapper.OrderHeadMapper;
import com.xiyuguopu.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderTimeoutCancellationService {

    private final OrderHeadMapper orderHeadMapper;
    private final OrderItemMapper orderItemMapper;
    private final InventoryService inventoryService;
    private final OrderStatusService orderStatusService;

    @Transactional
    public void cancelOne(OrderHead head) {
        orderStatusService.transition(head, OrderStatusService.CANCELLED);

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, head.getId()));
        for (OrderItem item : items) {
            inventoryService.restore(item);
        }

        orderHeadMapper.updateById(head);
        log.info("订单 {} 已自动取消", head.getOrderNo());
    }
}
