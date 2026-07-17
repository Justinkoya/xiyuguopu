package com.xiyuguopu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.AdminPackageSaveDTO;
import com.xiyuguopu.dto.AdminPackageVO;
import com.xiyuguopu.entity.PackageDef;
import com.xiyuguopu.service.AdminPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台 — 套餐管理
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminPackageController {

    private final AdminPackageService adminPackageService;

    @GetMapping("/packages")
    public Result<Page<PackageDef>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.ok(adminPackageService.list(page, size, keyword));
    }

    @GetMapping("/packages/{id}")
    public Result<AdminPackageVO> detail(@PathVariable Long id) {
        return Result.ok(adminPackageService.detail(id));
    }

    @PostMapping("/packages")
    public Result<AdminPackageVO> add(@Valid @RequestBody AdminPackageSaveDTO dto) {
        return Result.ok(adminPackageService.add(dto));
    }

    @PutMapping("/packages/{id}")
    public Result<AdminPackageVO> edit(@PathVariable Long id, @Valid @RequestBody AdminPackageSaveDTO dto) {
        return Result.ok(adminPackageService.edit(id, dto));
    }

    @DeleteMapping("/packages/{id}")
    public Result<?> delete(@PathVariable Long id) {
        adminPackageService.delete(id);
        return Result.ok();
    }
}
