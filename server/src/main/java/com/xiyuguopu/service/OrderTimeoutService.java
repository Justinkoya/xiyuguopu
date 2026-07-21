package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.entity.PackageDef;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 超时未支付订单自动取消 + 回退库存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderTimeoutService {

    private final OrderHeadMapper orderHeadMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final PackageDefMapper packageDefMapper;

    /** 超时时间（分钟）*/
    private static final int TIMEOUT_MINUTES = 30;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void cancelTimeoutOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);

        // 查超时未支付订单
        List<OrderHead> timeoutOrders = orderHeadMapper.selectList(
                new LambdaQueryWrapper<OrderHead>()
                        .eq(OrderHead::getStatus, "UNPAID")
                        .le(OrderHead::getCreatedAt, deadline));

        if (timeoutOrders.isEmpty()) return;

        log.info("自动取消超时订单: {} 笔", timeoutOrders.size());

        for (OrderHead head : timeoutOrders) {
            // 回退库存
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, head.getId()));
            for (OrderItem item : items) {
                int qty = item.getQuantity();
                if ("PACKAGE".equals(item.getItemType())) {
                    packageDefMapper.update(null,
                            new LambdaUpdateWrapper<PackageDef>()
                                    .setSql("stock = stock + " + qty)
                                    .eq(PackageDef::getCode, item.getPackageCode()));
                } else if (item.getProductId() != null) {
                    productMapper.update(null,
                            new LambdaUpdateWrapper<Product>()
                                    .setSql("stock = stock + " + qty)
                                    .eq(Product::getId, item.getProductId()));
                }
            }

            // 更新订单状态
            head.setStatus("CANCELLED");
            head.setCancelledAt(LocalDateTime.now());
            orderHeadMapper.updateById(head);

            log.info("订单 {} 已自动取消", head.getOrderNo());
        }
    }
}
