package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.mapper.OrderHeadMapper;
import com.xiyuguopu.mapper.OrderItemMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderTimeoutCancellationServiceTest {

    private final OrderHeadMapper orderHeadMapper = mock(OrderHeadMapper.class);
    private final OrderItemMapper orderItemMapper = mock(OrderItemMapper.class);
    private final InventoryService inventoryService = mock(InventoryService.class);
    private final OrderStatusService orderStatusService = new OrderStatusService();
    private final OrderTimeoutCancellationService service = new OrderTimeoutCancellationService(
            orderHeadMapper,
            orderItemMapper,
            inventoryService,
            orderStatusService
    );

    @Test
    void cancelsOneOrderTransactionally() {
        OrderHead head = new OrderHead();
        head.setId(10L);
        head.setOrderNo("XG10");
        head.setStatus(OrderStatusService.UNPAID);

        OrderItem item = new OrderItem();
        item.setItemType("PRODUCT");
        item.setProductId(1L);
        item.setQuantity(2);
        when(orderItemMapper.selectList(anyOrderItemQuery())).thenReturn(List.of(item));

        service.cancelOne(head);

        assertThat(head.getStatus()).isEqualTo(OrderStatusService.CANCELLED);
        assertThat(head.getCancelledAt()).isNotNull();
        verify(inventoryService).restore(item);
        verify(orderHeadMapper).updateById(head);
    }

    @SuppressWarnings("unchecked")
    private LambdaQueryWrapper<OrderItem> anyOrderItemQuery() {
        return any(LambdaQueryWrapper.class);
    }
}
