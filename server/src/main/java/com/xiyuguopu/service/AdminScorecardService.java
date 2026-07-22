package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.common.BusinessException;
import com.xiyuguopu.dto.AdminScorecardSaveDTO;
import com.xiyuguopu.dto.AdminScorecardVO;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.entity.Scorecard;
import com.xiyuguopu.entity.ScorecardDim;
import com.xiyuguopu.mapper.ProductMapper;
import com.xiyuguopu.mapper.ScorecardDimMapper;
import com.xiyuguopu.mapper.ScorecardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminScorecardService {

    private final ScorecardMapper scorecardMapper;
    private final ScorecardDimMapper dimMapper;
    private final ProductMapper productMapper;

    public Page<AdminScorecardVO> list(int page, int size, String keyword) {
        List<AdminScorecardVO> all = scorecardMapper.selectList(
                        new LambdaQueryWrapper<Scorecard>().orderByDesc(Scorecard::getId))
                .stream()
                .map(this::toVO)
                .filter(vo -> keyword == null || keyword.isBlank()
                        || (vo.getProductName() != null && vo.getProductName().contains(keyword.trim())))
                .collect(Collectors.toList());

        int from = Math.min(Math.max(page - 1, 0) * size, all.size());
        int to = Math.min(from + size, all.size());
        Page<AdminScorecardVO> result = new Page<>(page, size, all.size());
        result.setRecords(all.subList(from, to));
        return result;
    }

    public AdminScorecardVO detail(Long id) {
        Scorecard scorecard = scorecardMapper.selectById(id);
        if (scorecard == null) {
            throw BusinessException.notFound("评分卡不存在");
        }
        return toVO(scorecard);
    }

    @Transactional
    public AdminScorecardVO add(AdminScorecardSaveDTO dto) {
        ensureProductExists(dto.getProductId());
        ensureProductUnique(dto.getProductId(), null);

        Scorecard scorecard = new Scorecard();
        BeanUtils.copyProperties(dto, scorecard);
        scorecardMapper.insert(scorecard);
        saveDimensions(scorecard.getId(), dto.getDimensions());
        return detail(scorecard.getId());
    }

    @Transactional
    public AdminScorecardVO edit(Long id, AdminScorecardSaveDTO dto) {
        Scorecard scorecard = scorecardMapper.selectById(id);
        if (scorecard == null) {
            throw BusinessException.notFound("评分卡不存在");
        }
        ensureProductExists(dto.getProductId());
        ensureProductUnique(dto.getProductId(), id);

        BeanUtils.copyProperties(dto, scorecard);
        scorecard.setId(id);
        scorecardMapper.updateById(scorecard);
        dimMapper.delete(new LambdaQueryWrapper<ScorecardDim>().eq(ScorecardDim::getScorecardId, id));
        saveDimensions(id, dto.getDimensions());
        return detail(id);
    }

    @Transactional
    public void delete(Long id) {
        if (scorecardMapper.selectById(id) == null) {
            throw BusinessException.notFound("评分卡不存在");
        }
        dimMapper.delete(new LambdaQueryWrapper<ScorecardDim>().eq(ScorecardDim::getScorecardId, id));
        scorecardMapper.deleteById(id);
    }

    private void saveDimensions(Long scorecardId, List<AdminScorecardSaveDTO.DimensionDTO> dimensions) {
        List<AdminScorecardSaveDTO.DimensionDTO> safeDimensions =
                dimensions == null ? Collections.emptyList() : dimensions;
        for (int i = 0; i < safeDimensions.size(); i++) {
            AdminScorecardSaveDTO.DimensionDTO dto = safeDimensions.get(i);
            if (dto.getLabel() == null || dto.getLabel().isBlank()) {
                continue;
            }
            ScorecardDim dim = new ScorecardDim();
            dim.setScorecardId(scorecardId);
            dim.setLabel(dto.getLabel().trim());
            dim.setScore(dto.getScore());
            dim.setPct(dto.getPct() != null ? dto.getPct() : toPct(dto.getScore()));
            dim.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : i + 1);
            dimMapper.insert(dim);
        }
    }

    private int toPct(Double score) {
        if (score == null) {
            return 0;
        }
        return Math.max(0, Math.min(100, (int) Math.round(score / 6.0 * 100)));
    }

    private void ensureProductExists(Long productId) {
        if (productMapper.selectById(productId) == null) {
            throw BusinessException.notFound("商品不存在");
        }
    }

    private void ensureProductUnique(Long productId, Long currentId) {
        List<Scorecard> scorecards = scorecardMapper.selectList(
                new LambdaQueryWrapper<Scorecard>().eq(Scorecard::getProductId, productId));
        boolean duplicated = scorecards.stream()
                .anyMatch(s -> currentId == null || !s.getId().equals(currentId));
        if (duplicated) {
            throw BusinessException.conflict("该商品已有评分卡");
        }
    }

    private AdminScorecardVO toVO(Scorecard scorecard) {
        Product product = productMapper.selectById(scorecard.getProductId());
        String productName = product == null ? "" : product.getName();

        List<AdminScorecardVO.DimensionVO> dimensions = dimMapper.selectList(
                        new LambdaQueryWrapper<ScorecardDim>()
                                .eq(ScorecardDim::getScorecardId, scorecard.getId())
                                .orderByAsc(ScorecardDim::getSortOrder))
                .stream()
                .sorted(Comparator.comparing(ScorecardDim::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .map(d -> AdminScorecardVO.DimensionVO.builder()
                        .id(d.getId())
                        .label(d.getLabel())
                        .score(d.getScore())
                        .pct(d.getPct())
                        .sortOrder(d.getSortOrder())
                        .build())
                .collect(Collectors.toList());

        return AdminScorecardVO.builder()
                .id(scorecard.getId())
                .productId(scorecard.getProductId())
                .productName(productName)
                .totalScore(scorecard.getTotalScore())
                .image(scorecard.getImage())
                .originText(scorecard.getOriginText())
                .ingredientText(scorecard.getIngredientText())
                .createdAt(scorecard.getCreatedAt())
                .dimensions(dimensions)
                .build();
    }
}
