package com.xiyuguopu.controller;

import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.PayCallbackDTO;
import com.xiyuguopu.dto.PayRequestDTO;
import com.xiyuguopu.service.WxPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 微信支付 — 小程序端
 */
@RestController
@RequestMapping("/api/wx")
@RequiredArgsConstructor
public class WxPayController {

    private final WxPayService wxPayService;

    /**
     * POST /api/wx/pay — 发起支付（需登录）
     */
    @PostMapping("/pay")
    public Result<Map<String, Object>> pay(@RequestBody PayRequestDTO dto,
                                           HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.ok(wxPayService.pay(userId, dto.getOrderId()));
    }

    /**
     * POST /api/wx/pay-callback — 支付回调（公开，不走用户拦截器）
     */
    @PostMapping("/pay-callback")
    public Result<?> callback(@RequestBody PayCallbackDTO dto) {
        wxPayService.handleCallback(dto.getOrderId(), dto.getTransactionId());
        return Result.ok();
    }

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }
}
