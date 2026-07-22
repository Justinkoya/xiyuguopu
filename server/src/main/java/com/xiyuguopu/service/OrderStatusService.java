package com.xiyuguopu.service;

import com.xiyuguopu.common.BusinessException;
import com.xiyuguopu.entity.OrderHead;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
public class OrderStatusService {

    public static final String UNPAID = "UNPAID";
    public static final String PAID = "PAID";
    public static final String SHIPPED = "SHIPPED";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED = "CANCELLED";

    private static final Map<String, Set<String>> ALLOWED = Map.of(
            UNPAID, Set.of(PAID, CANCELLED),
            PAID, Set.of(SHIPPED, CANCELLED),
            SHIPPED, Set.of(COMPLETED)
    );

    public void transition(OrderHead head, String toStatus) {
        String fromStatus = head.getStatus();
        if (!ALLOWED.getOrDefault(fromStatus, Set.of()).contains(toStatus)) {
            throw BusinessException.conflict("订单状态不允许从 " + fromStatus + " 变更为 " + toStatus);
        }
        head.setStatus(toStatus);
        LocalDateTime now = LocalDateTime.now();
        if (PAID.equals(toStatus)) {
            head.setPaidAt(now);
        } else if (SHIPPED.equals(toStatus)) {
            head.setShippedAt(now);
        } else if (COMPLETED.equals(toStatus)) {
            head.setCompletedAt(now);
        } else if (CANCELLED.equals(toStatus)) {
            head.setCancelledAt(now);
        }
    }

    public void requirePayable(OrderHead head) {
        if (!UNPAID.equals(head.getStatus())) {
            throw BusinessException.conflict("订单状态不允许支付");
        }
    }
}
