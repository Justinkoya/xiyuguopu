package com.xiyuguopu.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台 — 订单详情 VO
 */
@Data
@Builder
public class AdminOrderVO {

    private Long id;
    private String orderNo;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private String remark;

    /** 地址快照 JSON */
    private Object addressSnapshot;

    /** 订单明细 */
    private List<OrderItemVO> items;

    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class OrderItemVO {
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}
