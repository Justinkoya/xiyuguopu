package com.xiyuguopu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.AdminOrderVO;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理后台 — 订单管理
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    /**
     * GET /api/admin/orders?page=1&size=10&status=PAID
     */
    @GetMapping("/orders")
    public Result<Page<OrderHead>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return Result.ok(adminOrderService.list(page, size, status));
    }

    /**
     * GET /api/admin/orders/{id}
     */
    @GetMapping("/orders/{id}")
    public Result<AdminOrderVO> detail(@PathVariable Long id) {
        return Result.ok(adminOrderService.detail(id));
    }

    /**
     * PUT /api/admin/orders/{id}/status
     * Body: {"status": "SHIPPED"}
     */
    @PutMapping("/orders/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return Result.fail("状态不能为空");
        }
        adminOrderService.updateStatus(id, status);
        return Result.ok();
    }
}
