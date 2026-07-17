package com.xiyuguopu.controller;

import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.ScorecardVO;
import com.xiyuguopu.service.ScorecardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScorecardController {

    private final ScorecardService scorecardService;

    @GetMapping("/scorecards")
    public Result<List<ScorecardVO>> listAll() {
        return Result.ok(scorecardService.listAll());
    }
}
