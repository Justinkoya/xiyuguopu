package com.xiyuguopu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 6星评分卡（主表）
 */
@Data
@TableName("scorecard")
public class Scorecard {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;
    private Double totalScore;
    private String originText;
    private String image;
    private String ingredientText;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
