package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.dto.ScorecardVO;
import com.xiyuguopu.entity.Scorecard;
import com.xiyuguopu.entity.ScorecardDim;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.mapper.ScorecardDimMapper;
import com.xiyuguopu.mapper.ScorecardMapper;
import com.xiyuguopu.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScorecardService {

    private final ScorecardMapper scorecardMapper;
    private final ScorecardDimMapper dimMapper;
    private final ProductMapper productMapper;

    public List<ScorecardVO> listAll() {
        List<Scorecard> scorecards = scorecardMapper.selectList(null);
        return scorecards.stream().map(s -> {
            Product product = productMapper.selectById(s.getProductId());
            String productName = product != null ? product.getName() : "";

            List<ScorecardDim> dims = dimMapper.selectList(
                    new LambdaQueryWrapper<ScorecardDim>()
                            .eq(ScorecardDim::getScorecardId, s.getId())
                            .orderByAsc(ScorecardDim::getSortOrder));

            List<ScorecardVO.Dimension> dimensions = dims.stream()
                    .map(d -> ScorecardVO.Dimension.builder()
                            .label(d.getLabel())
                            .score(d.getScore())
                            .pct(d.getPct())
                            .build())
                    .collect(Collectors.toList());

            return ScorecardVO.builder()
                    .productId(s.getProductId())
                    .productName(productName)
                    .totalScore(s.getTotalScore())
                    .image(s.getImage())
                    .originText(s.getOriginText())
                    .ingredientText(s.getIngredientText())
                    .dimensions(dimensions)
                    .build();
        }).collect(Collectors.toList());
    }
}
