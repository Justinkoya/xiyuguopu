package com.xiyuguopu.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AdminScorecardSaveDTO {

    @NotNull(message = "商品不能为空")
    private Long productId;

    @NotNull(message = "综合评分不能为空")
    private Double totalScore;

    private String originText;
    private String image;
    private String ingredientText;

    @Valid
    private List<DimensionDTO> dimensions;

    @Data
    public static class DimensionDTO {
        @NotBlank(message = "维度名不能为空")
        private String label;

        @NotNull(message = "维度评分不能为空")
        private Double score;

        private Integer pct;
        private Integer sortOrder;
    }
}
