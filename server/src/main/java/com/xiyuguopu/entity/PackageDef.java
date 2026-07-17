package com.xiyuguopu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 套餐定义
 */
@Data
@TableName("package_def")
public class PackageDef {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;
    private String icon;
    private String name;
    private String subtitle;
    private Double price;
    private Integer stock;
    private Integer sale;
    private Boolean featured;
    private String badge;
    private String extra;
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
