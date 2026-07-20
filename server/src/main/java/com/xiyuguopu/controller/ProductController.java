package com.xiyuguopu.controller;

import com.xiyuguopu.common.Result;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品接口 — 网页 + 小程序公用
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products?categoryCode=trial
     * 不传 categoryCode 时返回全部上架商品
     */
    @GetMapping("/products")
    public Result<List<Product>> listAll(
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) Boolean featured) {
        if (Boolean.TRUE.equals(featured)) {
            return Result.ok(productService.getFeatured());
        }
        return Result.ok(productService.getByCategoryCode(categoryCode));
    }

    /**
     * GET /api/products/{id} — 商品详情
     */
    @GetMapping("/products/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.ok(productService.getById(id));
    }
}
