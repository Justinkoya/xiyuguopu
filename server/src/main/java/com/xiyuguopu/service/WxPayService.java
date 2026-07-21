package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.entity.PackageDef;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.mapper.OrderHeadMapper;
import com.xiyuguopu.mapper.OrderItemMapper;
import com.xiyuguopu.mapper.PackageDefMapper;
import com.xiyuguopu.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信支付 — Mock 模式
 */
@Service
@RequiredArgsConstructor
public class WxPayService {

    private final OrderHeadMapper orderHeadMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final PackageDefMapper packageDefMapper;

    /**
     * 发起支付 — 返回 mock 支付参数
     */
    public Map<String, Object> pay(Long userId, Long orderId) {
        OrderHead head = orderHeadMapper.selectById(orderId);
        if (head == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!head.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该订单");
        }
        if (!"UNPAID".equals(head.getStatus())) {
            throw new RuntimeException("订单状态不允许支付");
        }

        String prepayId = "prepay_mock_" + System.currentTimeMillis();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("prepayId", prepayId);
        result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        result.put("nonceStr", "mock_nonce_" + (int)(Math.random() * 100000));
        result.put("signType", "MD5");
        result.put("paySign", "MOCK_SIGN_" + System.currentTimeMillis());
        result.put("package", "prepay_id=" + prepayId);
        return result;
    }

    /**
     * 支付回调 — 更新订单状态 + 累加销量（库存已在创建订单时扣减）
     */
    @Transactional
    public void handleCallback(Long orderId, String transactionId) {
        OrderHead head = orderHeadMapper.selectById(orderId);
        if (head == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"UNPAID".equals(head.getStatus())) {
            throw new RuntimeException("订单状态异常: " + head.getStatus());
        }

        // 更新订单状态
        head.setStatus("PAID");
        head.setWxTransactionId(transactionId != null ? transactionId : "");
        head.setPaidAt(LocalDateTime.now());
        orderHeadMapper.updateById(head);

        // 累加销量（付款成功才算销量）
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            int qty = item.getQuantity();
            if ("PACKAGE".equals(item.getItemType())) {
                packageDefMapper.update(null,
                        new LambdaUpdateWrapper<PackageDef>()
                                .setSql("sale = COALESCE(sale,0) + " + qty)
                                .eq(PackageDef::getCode, item.getPackageCode()));
            } else if (item.getProductId() != null) {
                productMapper.update(null,
                        new LambdaUpdateWrapper<Product>()
                                .setSql("sale = COALESCE(sale,0) + " + qty)
                                .eq(Product::getId, item.getProductId()));
            }
        }
    }
}
