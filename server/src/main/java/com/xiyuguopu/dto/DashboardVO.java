package com.xiyuguopu.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理后台 — 仪表盘统计
 */
@Data
@Builder
public class DashboardVO {
    /** 今日订单数 */
    private long todayOrders;
    /** 待发货数（PAID状态的订单） */
    private long pendingShipment;
    /** 本月收入（COMPLETED状态） */
    private BigDecimal monthlyRevenue;
    /** 商品总数 */
    private long productCount;
}
