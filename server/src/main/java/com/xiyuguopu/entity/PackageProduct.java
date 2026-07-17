package com.xiyuguopu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 套餐包含商品
 */
@Data
@TableName("package_product")
public class PackageProduct {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long packageId;
    private String productName;
    private String quantity;
    private Integer sortOrder;
}
