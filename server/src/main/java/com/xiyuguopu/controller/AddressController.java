package com.xiyuguopu.controller;

import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.AddressSaveDTO;
import com.xiyuguopu.entity.Address;
import com.xiyuguopu.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址 — 小程序端
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * GET /api/user/addresses
     */
    @GetMapping("/addresses")
    public Result<List<Address>> list(HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.ok(addressService.list(userId));
    }

    /**
     * GET /api/user/addresses/{id}
     */
    @GetMapping("/addresses/{id}")
    public Result<Address> detail(@PathVariable Long id,
                                   HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.ok(addressService.get(userId, id));
    }

    /**
     * POST /api/user/addresses
     */
    @PostMapping("/addresses")
    public Result<Address> add(@Valid @RequestBody AddressSaveDTO dto,
                               HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.ok(addressService.add(userId, dto));
    }

    /**
     * PUT /api/user/addresses/{id}
     */
    @PutMapping("/addresses/{id}")
    public Result<Address> edit(@PathVariable Long id,
                                @Valid @RequestBody AddressSaveDTO dto,
                                HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.ok(addressService.edit(userId, id, dto));
    }

    /**
     * DELETE /api/user/addresses/{id}
     */
    @DeleteMapping("/addresses/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        addressService.delete(userId, id);
        return Result.ok();
    }

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }
}
