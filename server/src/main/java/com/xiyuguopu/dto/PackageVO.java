package com.xiyuguopu.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * 套餐 VO
 */
@Data
@Builder
public class PackageVO {

    private String code;
    private String icon;
    private String name;
    private String subtitle;
    private Double price;
    private Boolean featured;
    private String badge;
    private String extra;
    /** 套餐内商品: ["纸皮核桃 500g", "小白杏 500g", ...] */
    private List<String> items;
}
