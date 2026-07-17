package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.entity.Category;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.mapper.CategoryMapper;
import com.xiyuguopu.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 商品服务
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    /**
     * 按 ID 查询单个商品
     */
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }

    /**
     * 按分类编码查询商品列表；categoryCode 为 null 时返回全部上架商品
     */
    public List<Product> getByCategoryCode(String categoryCode) {
        if (categoryCode == null || categoryCode.isEmpty()) {
            return productMapper.selectList(
                    new LambdaQueryWrapper<Product>()
                            .eq(Product::getIsOnSale, true)
                            .orderByAsc(Product::getSortOrder));
        }
        // 1. 查分类
        Category category = categoryMapper.selectOne(
                new LambdaQueryWrapper<Category>().eq(Category::getCode, categoryCode));
        if (category == null) {
            return Collections.emptyList();
        }
        // 2. 查该分类下的商品，按排序字段升序
        return productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getCategoryId, category.getId())
                        .eq(Product::getIsOnSale, true)
                        .orderByAsc(Product::getSortOrder));
    }
}
