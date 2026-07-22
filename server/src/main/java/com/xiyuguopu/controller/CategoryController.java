package com.xiyuguopu.controller;

import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.CatalogItemVO;
import com.xiyuguopu.entity.Category;
import com.xiyuguopu.service.CategoryCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryCatalogService categoryCatalogService;

    @GetMapping("/categories")
    public Result<List<Category>> listAll() {
        return Result.ok(categoryCatalogService.listEnabledCategories());
    }

    @GetMapping("/categories/{code}/items")
    public Result<List<CatalogItemVO>> listItems(@PathVariable String code) {
        return Result.ok(categoryCatalogService.listItems(code));
    }
}
