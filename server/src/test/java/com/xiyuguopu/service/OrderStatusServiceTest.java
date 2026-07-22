package com.xiyuguopu.service;

import com.xiyuguopu.common.BusinessException;
import com.xiyuguopu.entity.OrderHead;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderStatusServiceTest {

    private final OrderStatusService service = new OrderStatusService();

    @Test
    void transitionsUnpaidToPaid() {
        OrderHead head = new OrderHead();
        head.setStatus(OrderStatusService.UNPAID);

        service.transition(head, OrderStatusService.PAID);

        assertThat(head.getStatus()).isEqualTo(OrderStatusService.PAID);
        assertThat(head.getPaidAt()).isNotNull();
    }

    @Test
    void rejectsRepeatedPay() {
        OrderHead head = new OrderHead();
        head.setStatus(OrderStatusService.PAID);

        assertThatThrownBy(() -> service.transition(head, OrderStatusService.PAID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("订单状态不允许");
    }

    @Test
    void rejectsCompletedCancel() {
        OrderHead head = new OrderHead();
        head.setStatus(OrderStatusService.COMPLETED);

        assertThatThrownBy(() -> service.transition(head, OrderStatusService.CANCELLED))
                .isInstanceOf(BusinessException.class);
    }
}
