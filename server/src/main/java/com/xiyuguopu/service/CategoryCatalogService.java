package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.common.BusinessException;
import com.xiyuguopu.dto.CatalogItemVO;
import com.xiyuguopu.dto.PackageVO;
import com.xiyuguopu.entity.Category;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryCatalogService {

    private final CategoryMapper categoryMapper;
    private final ProductService productService;
    private final PackageService packageService;

    public List<Category> listEnabledCategories() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getIsEnabled, true)
                        .orderByAsc(Category::getSortOrder)
                        .orderByAsc(Category::getId));
    }

    public List<CatalogItemVO> listItems(String code) {
        Category category = categoryMapper.selectOne(
                new LambdaQueryWrapper<Category>().eq(Category::getCode, code));
        if (category == null || Boolean.FALSE.equals(category.getIsEnabled())) {
            throw BusinessException.notFound("分类不存在");
        }

        String entryType = category.getEntryType() == null ? "PRODUCT" : category.getEntryType().toUpperCase();
        if ("PACKAGE".equals(entryType)) {
            return packageService.listAll().stream()
                    .map(this::toCatalogItem)
                    .collect(Collectors.toList());
        }
        return productService.getByCategoryCode(code).stream()
                .map(this::toCatalogItem)
                .collect(Collectors.toList());
    }

    private CatalogItemVO toCatalogItem(Product product) {
        return CatalogItemVO.builder()
                .itemType("PRODUCT")
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .unit(product.getUnit())
                .image(product.getImage())
                .badge(product.getBadge())
                .stock(product.getStock())
                .sale(product.getSale())
                .featured(product.getFeatured())
                .highlight(product.getHighlight())
                .highlightText(product.getHighlightText())
                .extra(product.getExtra())
                .tags(product.getTags())
                .build();
    }

    private CatalogItemVO toCatalogItem(PackageVO pkg) {
        return CatalogItemVO.builder()
                .itemType("PACKAGE")
                .packageCode(pkg.getCode())
                .name(pkg.getName())
                .subtitle(pkg.getSubtitle())
                .description(pkg.getSubtitle())
                .price(pkg.getPrice())
                .unit("套")
                .image(pkg.getImage())
                .badge(pkg.getBadge())
                .stock(pkg.getStock())
                .sale(pkg.getSale())
                .featured(pkg.getFeatured())
                .extra(pkg.getExtra())
                .items(pkg.getItems())
                .build();
    }
}
