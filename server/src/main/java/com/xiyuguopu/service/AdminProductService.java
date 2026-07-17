package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.dto.AdminProductSaveDTO;
import com.xiyuguopu.entity.Category;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.mapper.CategoryMapper;
import com.xiyuguopu.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 管理后台 — 商品管理
 */
@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    /**
     * 分页列表，支持按分类和关键词筛选
     */
    public Page<Product> list(int page, int size, Long categoryId, String keyword) {
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            qw.eq(Product::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            qw.like(Product::getName, keyword);
        }
        qw.orderByAsc(Product::getSortOrder);
        return productMapper.selectPage(new Page<>(page, size), qw);
    }

    /**
     * 详情
     */
    public Product detail(Long id) {
        Product p = productMapper.selectById(id);
        if (p == null) {
            throw new RuntimeException("商品不存在");
        }
        return p;
    }

    /**
     * 新增
     */
    public Product add(AdminProductSaveDTO dto) {
        // 校验分类存在
        Category cat = categoryMapper.selectById(dto.getCategoryId());
        if (cat == null) {
            throw new RuntimeException("分类不存在");
        }
        Product p = new Product();
        BeanUtils.copyProperties(dto, p);
        productMapper.insert(p);
        return p;
    }

    /**
     * 编辑
     */
    public Product edit(Long id, AdminProductSaveDTO dto) {
        Product p = productMapper.selectById(id);
        if (p == null) {
            throw new RuntimeException("商品不存在");
        }
        Category cat = categoryMapper.selectById(dto.getCategoryId());
        if (cat == null) {
            throw new RuntimeException("分类不存在");
        }
        BeanUtils.copyProperties(dto, p);
        p.setId(id); // 防止 copy 覆盖 id
        productMapper.updateById(p);
        return p;
    }

    /**
     * 只改库存
     */
    public void updateStock(Long id, int stock) {
        Product p = productMapper.selectById(id);
        if (p == null) {
            throw new RuntimeException("商品不存在");
        }
        p.setStock(stock);
        productMapper.updateById(p);
    }

    /**
     * 删除商品
     */
    public void delete(Long id) {
        if (productMapper.selectById(id) == null) {
            throw new RuntimeException("商品不存在");
        }
        productMapper.deleteById(id);
    }
}
