package com.xiyuguopu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 6星评分卡（维度）
 */
@Data
@TableName("scorecard_dim")
public class ScorecardDim {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long scorecardId;
    private String label;
    private Double score;
    private Integer pct;
    private Integer sortOrder;
}
