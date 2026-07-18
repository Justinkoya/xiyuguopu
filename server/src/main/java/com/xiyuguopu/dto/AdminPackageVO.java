package com.xiyuguopu.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 — 套餐详情 VO
 */
@Data
@Builder
public class AdminPackageVO {

    private Long id;
    private String code;
    private String icon;
    private String image;
    private String name;
    private String subtitle;
    private Double price;
    private Integer stock;
    private Integer sale;
    private Boolean featured;
    private String badge;
    private String extra;
    private Integer sortOrder;
    private List<ItemVO> items;

    @Data
    @Builder
    public static class ItemVO {
        private Long id;
        private String productName;
        private String quantity;
        private Integer sortOrder;
    }
}
