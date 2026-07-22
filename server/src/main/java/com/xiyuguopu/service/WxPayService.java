package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.common.BusinessException;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.mapper.OrderHeadMapper;
import com.xiyuguopu.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WxPayService {

    private final OrderHeadMapper orderHeadMapper;
    private final OrderItemMapper orderItemMapper;
    private final InventoryService inventoryService;
    private final OrderStatusService orderStatusService;

    public Map<String, Object> pay(Long userId, Long orderId) {
        OrderHead head = orderHeadMapper.selectById(orderId);
        if (head == null) {
            throw BusinessException.notFound("订单不存在");
        }
        if (!head.getUserId().equals(userId)) {
            throw BusinessException.forbidden("无权操作该订单");
        }
        orderStatusService.requirePayable(head);

        String prepayId = "prepay_mock_" + System.currentTimeMillis();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("prepayId", prepayId);
        result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        result.put("nonceStr", "mock_nonce_" + (int) (Math.random() * 100000));
        result.put("signType", "MD5");
        result.put("paySign", "MOCK_SIGN_" + System.currentTimeMillis());
        result.put("package", "prepay_id=" + prepayId);
        return result;
    }

    @Transactional
    public void handleCallback(Long orderId, String transactionId) {
        OrderHead head = orderHeadMapper.selectById(orderId);
        if (head == null) {
            throw BusinessException.notFound("订单不存在");
        }

        orderStatusService.transition(head, OrderStatusService.PAID);
        head.setWxTransactionId(transactionId != null ? transactionId : "");
        orderHeadMapper.updateById(head);

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            inventoryService.incrementSale(item);
        }
    }
}
