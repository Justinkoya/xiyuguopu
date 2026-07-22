package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.mapper.OrderHeadMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderTimeoutServiceTest {

    private final OrderHeadMapper orderHeadMapper = mock(OrderHeadMapper.class);
    private final OrderTimeoutCancellationService cancellationService = mock(OrderTimeoutCancellationService.class);
    private final OrderTimeoutService service = new OrderTimeoutService(orderHeadMapper, cancellationService);

    @Test
    void continuesAfterSingleOrderFailure() {
        OrderHead first = order(1L, "XG1");
        OrderHead second = order(2L, "XG2");
        OrderHead third = order(3L, "XG3");
        when(orderHeadMapper.selectList(anyOrderHeadQuery())).thenReturn(List.of(first, second, third));
        doThrow(new RuntimeException("db busy")).when(cancellationService).cancelOne(second);

        service.cancelTimeoutOrders();

        verify(cancellationService).cancelOne(first);
        verify(cancellationService).cancelOne(second);
        verify(cancellationService).cancelOne(third);
    }

    private OrderHead order(Long id, String orderNo) {
        OrderHead head = new OrderHead();
        head.setId(id);
        head.setOrderNo(orderNo);
        head.setStatus(OrderStatusService.UNPAID);
        return head;
    }

    @SuppressWarnings("unchecked")
    private LambdaQueryWrapper<OrderHead> anyOrderHeadQuery() {
        return any(LambdaQueryWrapper.class);
    }
}
