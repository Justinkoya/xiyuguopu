package com.xiyuguopu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminScorecardVO {

    private Long id;
    private Long productId;
    private String productName;
    private Double totalScore;
    private String image;
    private String originText;
    private String ingredientText;
    private LocalDateTime createdAt;
    private List<DimensionVO> dimensions;

    @Data
    @Builder
    public static class DimensionVO {
        private Long id;
        private String label;
        private Double score;
        private Integer pct;
        private Integer sortOrder;
    }
}
