package com.xiyuguopu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.AdminProductSaveDTO;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.service.AdminProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理后台 — 商品管理
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    /**
     * GET /api/admin/products?page=1&size=10&categoryId=1&keyword=核桃
     */
    @GetMapping("/products")
    public Result<Page<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return Result.ok(adminProductService.list(page, size, categoryId, keyword));
    }

    /**
     * GET /api/admin/products/{id}
     */
    @GetMapping("/products/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.ok(adminProductService.detail(id));
    }

    /**
     * POST /api/admin/products
     */
    @PostMapping("/products")
    public Result<Product> add(@Valid @RequestBody AdminProductSaveDTO dto) {
        return Result.ok(adminProductService.add(dto));
    }

    /**
     * PUT /api/admin/products/{id}
     */
    @PutMapping("/products/{id}")
    public Result<Product> edit(@PathVariable Long id, @Valid @RequestBody AdminProductSaveDTO dto) {
        return Result.ok(adminProductService.edit(id, dto));
    }

    /**
     * PUT /api/admin/products/{id}/stock
     * Body: {"stock": 200}
     */
    @PutMapping("/products/{id}/stock")
    public Result<?> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer stock = body.get("stock");
        if (stock == null || stock < 0) {
            return Result.fail("库存数量不合法");
        }
        adminProductService.updateStock(id, stock);
        return Result.ok();
    }

    /**
     * DELETE /api/admin/products/{id}
     */
    @DeleteMapping("/products/{id}")
    public Result<?> delete(@PathVariable Long id) {
        adminProductService.delete(id);
        return Result.ok();
    }
}
