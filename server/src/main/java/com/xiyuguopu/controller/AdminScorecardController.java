package com.xiyuguopu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.AdminScorecardSaveDTO;
import com.xiyuguopu.dto.AdminScorecardVO;
import com.xiyuguopu.service.AdminScorecardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminScorecardController {

    private final AdminScorecardService adminScorecardService;

    @GetMapping("/scorecards")
    public Result<Page<AdminScorecardVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.ok(adminScorecardService.list(page, size, keyword));
    }

    @GetMapping("/scorecards/{id}")
    public Result<AdminScorecardVO> detail(@PathVariable Long id) {
        return Result.ok(adminScorecardService.detail(id));
    }

    @PostMapping("/scorecards")
    public Result<AdminScorecardVO> add(@Valid @RequestBody AdminScorecardSaveDTO dto) {
        return Result.ok(adminScorecardService.add(dto));
    }

    @PutMapping("/scorecards/{id}")
    public Result<AdminScorecardVO> edit(@PathVariable Long id, @Valid @RequestBody AdminScorecardSaveDTO dto) {
        return Result.ok(adminScorecardService.edit(id, dto));
    }

    @DeleteMapping("/scorecards/{id}")
    public Result<?> delete(@PathVariable Long id) {
        adminScorecardService.delete(id);
        return Result.ok();
    }
}
