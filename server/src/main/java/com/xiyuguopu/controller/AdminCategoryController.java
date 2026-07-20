package com.xiyuguopu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.AdminCategorySaveDTO;
import com.xiyuguopu.entity.Category;
import com.xiyuguopu.service.AdminCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台 - 分类入口管理
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @GetMapping("/categories")
    public Result<Page<Category>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        return Result.ok(adminCategoryService.list(page, size, keyword));
    }

    @GetMapping("/categories/{id}")
    public Result<Category> detail(@PathVariable Long id) {
        return Result.ok(adminCategoryService.detail(id));
    }

    @PostMapping("/categories")
    public Result<Category> add(@Valid @RequestBody AdminCategorySaveDTO dto) {
        return Result.ok(adminCategoryService.add(dto));
    }

    @PutMapping("/categories/{id}")
    public Result<Category> edit(@PathVariable Long id, @Valid @RequestBody AdminCategorySaveDTO dto) {
        return Result.ok(adminCategoryService.edit(id, dto));
    }

    @DeleteMapping("/categories/{id}")
    public Result<?> delete(@PathVariable Long id) {
        adminCategoryService.delete(id);
        return Result.ok();
    }
}
