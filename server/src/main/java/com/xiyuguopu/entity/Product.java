package com.xiyuguopu.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品
 */
@Data
@TableName(value = "product", autoResultMap = true)
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;
    private String name;
    private Double price;
    private String unit;
    private String image;
    private String badge;

    /** JSON 数组: ["阿克苏185品种","手捏即开"] */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    private String description;
    private Boolean highlight;
    private String highlightText;
    private String extra;
    private Integer stock;
    private Integer sale;
    private Boolean isOnSale;
    private Integer sortOrder;

    /** 成本明细 JSON（保留字段，前端成本透明走独立接口） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object costDetail;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
