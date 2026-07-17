package com.xiyuguopu.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 — 新增/编辑套餐表单
 */
@Data
public class AdminPackageSaveDTO {

    @NotBlank(message = "套餐编码不能为空")
    private String code;

    private String icon;

    @NotBlank(message = "套餐名不能为空")
    private String name;

    private String subtitle;

    @NotNull(message = "套餐价不能为空")
    private Double price;

    private Integer stock = 999;
    private Integer sale = 0;
    private Boolean featured = false;
    private String badge;
    private String extra;
    private Integer sortOrder = 0;
    @Valid
    private List<ItemDTO> items;

    @Data
    public static class ItemDTO {
        @NotBlank(message = "套餐商品名不能为空")
        private String productName;

        private String quantity = "500g";
    }
}
