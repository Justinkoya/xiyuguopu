package com.xiyuguopu.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CatalogItemVO {

    private String itemType;
    private Long productId;
    private String packageCode;
    private String name;
    private String subtitle;
    private String description;
    private Double price;
    private String unit;
    private String image;
    private String badge;
    private Integer stock;
    private Integer sale;
    private Boolean featured;
    private Boolean highlight;
    private String highlightText;
    private String extra;
    private List<String> tags;
    private List<String> items;
}
