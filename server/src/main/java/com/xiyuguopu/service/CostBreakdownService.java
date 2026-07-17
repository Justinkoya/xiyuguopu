package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.dto.CostBreakdownVO;
import com.xiyuguopu.entity.CostBreakdown;
import com.xiyuguopu.entity.CostBreakdownItem;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.mapper.CostBreakdownItemMapper;
import com.xiyuguopu.mapper.CostBreakdownMapper;
import com.xiyuguopu.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CostBreakdownService {

    private final CostBreakdownMapper breakdownMapper;
    private final CostBreakdownItemMapper itemMapper;
    private final ProductMapper productMapper;

    public List<CostBreakdownVO> listAll() {
        List<CostBreakdown> breakdowns = breakdownMapper.selectList(null);
        return breakdowns.stream().map(b -> {
            // 关联商品名
            Product product = productMapper.selectById(b.getProductId());
            String productName = product != null ? product.getName() : "";

            // 子项
            List<CostBreakdownItem> items = itemMapper.selectList(
                    new LambdaQueryWrapper<CostBreakdownItem>()
                            .eq(CostBreakdownItem::getBreakdownId, b.getId())
                            .orderByAsc(CostBreakdownItem::getSortOrder));

            List<CostBreakdownVO.CostItem> costItems = items.stream()
                    .map(i -> CostBreakdownVO.CostItem.builder()
                            .label(i.getLabel())
                            .amount(i.getAmount())
                            .pct(i.getPct())
                            .build())
                    .collect(Collectors.toList());

            return CostBreakdownVO.builder()
                    .productId(b.getProductId())
                    .icon(b.getIcon())
                    .productName(productName)
                    .weight(b.getWeight())
                    .price(b.getPrice())
                    .profit(b.getProfit())
                    .profitPct(b.getProfitPct())
                    .items(costItems)
                    .build();
        }).collect(Collectors.toList());
    }
}
