package com.xiyuguopu.controller;

import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.CostBreakdownVO;
import com.xiyuguopu.service.CostBreakdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 成本透明接口
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CostBreakdownController {

    private final CostBreakdownService costBreakdownService;

    @GetMapping("/cost-breakdown")
    public Result<List<CostBreakdownVO>> listAll() {
        return Result.ok(costBreakdownService.listAll());
    }
}
