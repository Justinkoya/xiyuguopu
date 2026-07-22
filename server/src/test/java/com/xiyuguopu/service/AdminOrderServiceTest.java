package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.mapper.OrderHeadMapper;
import com.xiyuguopu.mapper.OrderItemMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminOrderServiceTest {

    private final OrderHeadMapper orderHeadMapper = mock(OrderHeadMapper.class);
    private final OrderItemMapper orderItemMapper = mock(OrderItemMapper.class);
    private final OrderAssembler orderAssembler = mock(OrderAssembler.class);
    private final OrderStatusService orderStatusService = new OrderStatusService();
    private final InventoryService inventoryService = mock(InventoryService.class);
    private final AdminOrderService service = new AdminOrderService(
            orderHeadMapper,
            orderItemMapper,
            orderAssembler,
            orderStatusService,
            inventoryService
    );

    @Test
    void restoresInventoryWhenAdminCancelsPaidOrder() {
        OrderHead head = new OrderHead();
        head.setId(10L);
        head.setStatus(OrderStatusService.PAID);

        OrderItem product = new OrderItem();
        product.setItemType("PRODUCT");
        product.setProductId(1L);
        product.setQuantity(2);

        OrderItem pkg = new OrderItem();
        pkg.setItemType("PACKAGE");
        pkg.setPackageCode("gift");
        pkg.setQuantity(1);

        when(orderHeadMapper.selectById(10L)).thenReturn(head);
        when(orderItemMapper.selectList(anyOrderItemQuery())).thenReturn(List.of(product, pkg));

        service.updateStatus(10L, OrderStatusService.CANCELLED, null, null);

        verify(inventoryService).restore(product);
        verify(inventoryService).restore(pkg);
        verify(inventoryService, times(2)).restore(any(OrderItem.class));
        verify(orderHeadMapper).updateById(head);
    }

    @SuppressWarnings("unchecked")
    private LambdaQueryWrapper<OrderItem> anyOrderItemQuery() {
        return any(LambdaQueryWrapper.class);
    }
}
