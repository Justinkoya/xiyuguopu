package com.xiyuguopu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 成本透明明细（主表）
 */
@Data
@TableName("cost_breakdown")
public class CostBreakdown {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;
    private String icon;
    private Double price;
    private String weight;
    private Double profit;
    private String profitPct;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
