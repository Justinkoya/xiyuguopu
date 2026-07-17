package com.xiyuguopu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 成本透明明细（子项）
 */
@Data
@TableName("cost_breakdown_item")
public class CostBreakdownItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long breakdownId;
    private String label;
    private Double amount;
    private String pct;
    private Integer sortOrder;
}
