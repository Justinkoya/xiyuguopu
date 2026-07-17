package com.xiyuguopu.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * 6星评分卡 VO
 */
@Data
@Builder
public class ScorecardVO {

    private Long productId;
    private String productName;
    private Double totalScore;
    private String image;
    private String originText;
    private String ingredientText;
    private List<Dimension> dimensions;

    @Data
    @Builder
    public static class Dimension {
        private String label;
        private Double score;
        private Integer pct;
    }
}
