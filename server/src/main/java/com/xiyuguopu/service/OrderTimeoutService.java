package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.mapper.OrderHeadMapper;
import com.xiyuguopu.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderTimeoutService {

    private static final int TIMEOUT_MINUTES = 30;

    private final OrderHeadMapper orderHeadMapper;
    private final OrderItemMapper orderItemMapper;
    private final InventoryService inventoryService;
    private final OrderStatusService orderStatusService;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void cancelTimeoutOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);

        List<OrderHead> timeoutOrders = orderHeadMapper.selectList(
                new LambdaQueryWrapper<OrderHead>()
                        .eq(OrderHead::getStatus, OrderStatusService.UNPAID)
                        .le(OrderHead::getCreatedAt, deadline));

        if (timeoutOrders.isEmpty()) {
            return;
        }

        log.info("自动取消超时订单: {} 笔", timeoutOrders.size());

        for (OrderHead head : timeoutOrders) {
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
}
