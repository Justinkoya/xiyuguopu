package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.mapper.OrderHeadMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderTimeoutService {

    private static final int TIMEOUT_MINUTES = 30;

    private final OrderHeadMapper orderHeadMapper;
    private final OrderTimeoutCancellationService cancellationService;

    @Scheduled(cron = "0 */5 * * * *")
    public void cancelTimeoutOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);

        List<OrderHead> timeoutOrders = orderHeadMapper.selectList(
                new LambdaQueryWrapper<OrderHead>()
                        .eq(OrderHead::getStatus, OrderStatusService.UNPAID)
                        .le(OrderHead::getCreatedAt, deadline));

        if (timeoutOrders.isEmpty()) {
            return;
        }

        log.info("发现超时订单: {} 笔", timeoutOrders.size());
        int success = 0;
        int fail = 0;

        for (OrderHead head : timeoutOrders) {
            try {
                cancellationService.cancelOne(head);
                success++;
            } catch (Exception e) {
                fail++;
                log.error("自动取消订单 {} 失败，跳过继续", head.getOrderNo(), e);
            }
        }

        log.info("超时订单处理完成: 成功 {} 笔, 失败 {} 笔", success, fail);
    }
}
