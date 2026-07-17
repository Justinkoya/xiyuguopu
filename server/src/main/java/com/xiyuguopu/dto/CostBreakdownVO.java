package com.xiyuguopu.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * 成本透明 VO — 前端 cost-breakdown 接口返回
 */
@Data
@Builder
public class CostBreakdownVO {

    private Long productId;
    private String icon;
    private String productName;
    private String weight;
    private Double price;
    private Double profit;
    private String profitPct;
    private List<CostItem> items;

    @Data
    @Builder
    public static class CostItem {
        private String label;
        private Double amount;
        private String pct;
    }
}
