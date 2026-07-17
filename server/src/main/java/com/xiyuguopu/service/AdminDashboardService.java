package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.dto.DashboardVO;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.mapper.OrderHeadMapper;
import com.xiyuguopu.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 管理后台 — 仪表盘
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final OrderHeadMapper orderHeadMapper;
    private final ProductMapper productMapper;

    public DashboardVO stats() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);

        // 今日订单数
        long todayOrders = orderHeadMapper.selectCount(
                new LambdaQueryWrapper<OrderHead>()
                        .ge(OrderHead::getCreatedAt, todayStart));

        // 待发货
        long pendingShipment = orderHeadMapper.selectCount(
                new LambdaQueryWrapper<OrderHead>()
                        .eq(OrderHead::getStatus, "PAID"));

        // 本月收入
        List<OrderHead> completedThisMonth = orderHeadMapper.selectList(
                new LambdaQueryWrapper<OrderHead>()
                        .eq(OrderHead::getStatus, "COMPLETED")
                        .ge(OrderHead::getCompletedAt, monthStart));
        BigDecimal monthlyRevenue = completedThisMonth.stream()
                .map(OrderHead::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 商品总数
        long productCount = productMapper.selectCount(null);

        return DashboardVO.builder()
                .todayOrders(todayOrders)
                .pendingShipment(pendingShipment)
                .monthlyRevenue(monthlyRevenue)
                .productCount(productCount)
                .build();
    }
}
