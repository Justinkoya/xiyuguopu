package com.xiyuguopu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品分类
 */
@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String code;
    private String entryType;
    private String themeColor;
    private Integer sortOrder;
    private Boolean isEnabled;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
