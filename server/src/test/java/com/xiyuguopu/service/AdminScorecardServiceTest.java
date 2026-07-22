package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.common.BusinessException;
import com.xiyuguopu.dto.AdminScorecardSaveDTO;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.entity.Scorecard;
import com.xiyuguopu.entity.ScorecardDim;
import com.xiyuguopu.mapper.ProductMapper;
import com.xiyuguopu.mapper.ScorecardDimMapper;
import com.xiyuguopu.mapper.ScorecardMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminScorecardServiceTest {

    private final ScorecardMapper scorecardMapper = mock(ScorecardMapper.class);
    private final ScorecardDimMapper dimMapper = mock(ScorecardDimMapper.class);
    private final ProductMapper productMapper = mock(ProductMapper.class);
    private final AdminScorecardService service = new AdminScorecardService(scorecardMapper, dimMapper, productMapper);

    @Test
    void addsScorecardWithDimensions() {
        Product product = product(1L, "纸皮核桃");
        when(productMapper.selectById(1L)).thenReturn(product);
        when(scorecardMapper.selectList(anyScorecardQuery())).thenReturn(List.of());
        when(dimMapper.selectList(anyDimensionQuery())).thenReturn(List.of());
        doAnswer(invocation -> {
            Scorecard scorecard = invocation.getArgument(0);
            scorecard.setId(10L);
            when(scorecardMapper.selectById(10L)).thenReturn(scorecard);
            return 1;
        }).when(scorecardMapper).insert(any(Scorecard.class));

        service.add(saveDTO(1L));

        ArgumentCaptor<ScorecardDim> dimCaptor = ArgumentCaptor.forClass(ScorecardDim.class);
        verify(dimMapper).insert(dimCaptor.capture());
        assertThat(dimCaptor.getValue().getScorecardId()).isEqualTo(10L);
        assertThat(dimCaptor.getValue().getLabel()).isEqualTo("产地透明");
        assertThat(dimCaptor.getValue().getPct()).isEqualTo(100);
    }

    @Test
    void rejectsDuplicateProductScorecard() {
        when(productMapper.selectById(1L)).thenReturn(product(1L, "纸皮核桃"));
        Scorecard exists = new Scorecard();
        exists.setId(10L);
        exists.setProductId(1L);
        when(scorecardMapper.selectList(anyScorecardQuery())).thenReturn(List.of(exists));

        assertThatThrownBy(() -> service.add(saveDTO(1L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已有评分卡");
    }

    @Test
    void deletesScorecardAndDimensions() {
        Scorecard scorecard = new Scorecard();
        scorecard.setId(10L);
        scorecard.setProductId(1L);
        when(scorecardMapper.selectById(10L)).thenReturn(scorecard);

        service.delete(10L);

        verify(dimMapper).delete(anyDimensionQuery());
        verify(scorecardMapper).deleteById(10L);
    }

    private AdminScorecardSaveDTO saveDTO(Long productId) {
        AdminScorecardSaveDTO dto = new AdminScorecardSaveDTO();
        dto.setProductId(productId);
        dto.setTotalScore(5.8);
        dto.setOriginText("阿克苏温宿县");
        dto.setIngredientText("配料：纸皮核桃");

        AdminScorecardSaveDTO.DimensionDTO dimension = new AdminScorecardSaveDTO.DimensionDTO();
        dimension.setLabel("产地透明");
        dimension.setScore(6.0);
        dimension.setPct(100);
        dimension.setSortOrder(1);
        dto.setDimensions(List.of(dimension));
        return dto;
    }

    private Product product(Long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        return product;
    }

    @SuppressWarnings("unchecked")
    private LambdaQueryWrapper<Scorecard> anyScorecardQuery() {
        return any(LambdaQueryWrapper.class);
    }

    @SuppressWarnings("unchecked")
    private LambdaQueryWrapper<ScorecardDim> anyDimensionQuery() {
        return any(LambdaQueryWrapper.class);
    }
}
