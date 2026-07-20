package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.dto.AdminCategorySaveDTO;
import com.xiyuguopu.entity.Category;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.mapper.CategoryMapper;
import com.xiyuguopu.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 管理后台 - 分类入口管理
 */
@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private static final Set<String> ENTRY_TYPES = Set.of("PRODUCT", "PACKAGE");

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    public Page<Category> list(int page, int size, String keyword) {
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.and(w -> w.like(Category::getName, keyword).or().like(Category::getCode, keyword));
        }
        qw.orderByAsc(Category::getSortOrder).orderByAsc(Category::getId);
        return categoryMapper.selectPage(new Page<>(page, size), qw);
    }

    public Category detail(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        return category;
    }

    public Category add(AdminCategorySaveDTO dto) {
        Category category = new Category();
        fillCategory(category, dto);
        ensureCodeUnique(category.getCode(), null);
        categoryMapper.insert(category);
        return category;
    }

    public Category edit(Long id, AdminCategorySaveDTO dto) {
        Category category = detail(id);
        fillCategory(category, dto);
        category.setId(id);
        ensureCodeUnique(category.getCode(), id);
        categoryMapper.updateById(category);
        return category;
    }

    public void delete(Long id) {
        Category category = detail(id);
        if ("PRODUCT".equals(normalizeEntryType(category.getEntryType()))) {
            Long count = productMapper.selectCount(
                    new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, id));
            if (count != null && count > 0) {
                throw new RuntimeException("分类下还有商品，请先转移或删除商品");
            }
        }
        categoryMapper.deleteById(id);
    }

    private void fillCategory(Category category, AdminCategorySaveDTO dto) {
        BeanUtils.copyProperties(dto, category);
        category.setName(trimRequired(dto.getName(), "分类名称不能为空"));
        category.setCode(trimRequired(dto.getCode(), "分类编码不能为空"));
        category.setEntryType(normalizeEntryType(dto.getEntryType()));
        category.setThemeColor(defaultString(dto.getThemeColor(), "blue"));
        category.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
        category.setIsEnabled(dto.getIsEnabled() == null || dto.getIsEnabled());
    }

    private void ensureCodeUnique(String code, Long excludeId) {
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<Category>().eq(Category::getCode, code);
        if (excludeId != null) {
            qw.ne(Category::getId, excludeId);
        }
        if (categoryMapper.selectCount(qw) > 0) {
            throw new RuntimeException("分类编码已存在");
        }
    }

    private String normalizeEntryType(String entryType) {
        String type = defaultString(entryType, "PRODUCT").toUpperCase();
        if (!ENTRY_TYPES.contains(type)) {
            throw new RuntimeException("入口类型只能是 PRODUCT 或 PACKAGE");
        }
        return type;
    }

    private String trimRequired(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
        return value.trim();
    }

    private String defaultString(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }
}
