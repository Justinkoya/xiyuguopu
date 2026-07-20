package com.xiyuguopu.controller;

import com.xiyuguopu.common.Result;
import com.xiyuguopu.entity.Category;
import com.xiyuguopu.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类接口 — 小程序公用
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;

    /**
     * GET /api/categories — 所有分类，按 sortOrder 排序
     */
    @GetMapping("/categories")
    public Result<List<Category>> listAll() {
        List<Category> list = categoryMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                        .eq(Category::getIsEnabled, true)
                        .orderByAsc(Category::getSortOrder)
                        .orderByAsc(Category::getId));
        return Result.ok(list);
    }
}
