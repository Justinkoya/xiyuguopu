package com.xiyuguopu.controller;

import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.PackageVO;
import com.xiyuguopu.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @GetMapping("/packages")
    public Result<List<PackageVO>> listAll() {
        return Result.ok(packageService.listAll());
    }

    @GetMapping("/packages/{code}")
    public Result<PackageVO> detail(@PathVariable String code) {
        return Result.ok(packageService.detail(code));
    }
}
