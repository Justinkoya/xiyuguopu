package com.xiyuguopu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.CreateOrderDTO;
import com.xiyuguopu.dto.UserOrderVO;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单 — 小程序端
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * POST /api/orders — 创建订单
     */
    @PostMapping
    public Result<OrderHead> create(@Valid @RequestBody CreateOrderDTO dto,
                                    HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.ok(orderService.create(userId, dto));
    }

    /**
     * GET /api/orders?page=1&size=10 — 我的订单列表
     */
    @GetMapping
    public Result<Page<UserOrderVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.ok(orderService.list(userId, page, size));
    }

    /**
     * GET /api/orders/{id} — 订单详情
     */
    @GetMapping("/{id}")
    public Result<UserOrderVO> detail(@PathVariable Long id,
                                      HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.ok(orderService.detail(userId, id));
    }

    /**
     * POST /api/orders/{id}/cancel — 取消订单
     */
    @PostMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id,
                            HttpServletRequest request) {
        Long userId = getUserId(request);
        orderService.cancel(userId, id);
        return Result.ok();
    }

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }
}
