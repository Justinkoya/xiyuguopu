package com.xiyuguopu.controller;

import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.WxLoginDTO;
import com.xiyuguopu.dto.WxLoginVO;
import com.xiyuguopu.service.WxAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 微信登录 — 小程序端
 */
@RestController
@RequestMapping("/api/wx")
@RequiredArgsConstructor
public class WxAuthController {

    private final WxAuthService wxAuthService;

    /**
     * POST /api/wx/login
     * Body: {"code": "mock_openid_xxx"}
     */
    @PostMapping("/login")
    public Result<WxLoginVO> login(@RequestBody WxLoginDTO dto) {
        return Result.ok(wxAuthService.login(dto));
    }
}
