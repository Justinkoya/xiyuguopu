package com.xiyuguopu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 — 新增/编辑商品表单
 */
@Data
public class AdminProductSaveDTO {

    @NotNull(message = "请选择分类")
    private Long categoryId;

    @NotBlank(message = "商品名不能为空")
    private String name;

    @NotNull(message = "价格不能为空")
    private Double price;

    private String unit = "500g";
    private String image;
    private String badge;

    /** JSON 数组: ["阿克苏185品种","手捏即开"] */
    private List<String> tags;

    private String description;
    private Boolean featured = false;
    private Boolean highlight = false;
    private String highlightText;
    private String extra;
    private Integer stock = 999;
    private Integer sale = 0;
    private Boolean isOnSale = true;
    private Integer sortOrder = 0;

    /** 成本明细 JSON */
    private Object costDetail;
}
