package com.xiyuguopu.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品展示 VO
 */
@Data
public class ProductVO {

    private Long id;
    private Long categoryId;
    private String name;
    private Double price;
    private String unit;
    private String image;
    private String badge;
    private List<String> tags;
    private String description;
    private Boolean highlight;
    private String highlightText;
    private String extra;
    private Integer stock;
    private Boolean isOnSale;
    private Integer sortOrder;
    private Object costDetail;
    private Integer sales;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
