package com.xiyuguopu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理后台 - 新增/编辑分类入口表单
 */
@Data
public class AdminCategorySaveDTO {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotBlank(message = "分类编码不能为空")
    private String code;

    private String entryType = "PRODUCT";
    private String themeColor = "blue";
    private Integer sortOrder = 0;
    private Boolean isEnabled = true;
}
