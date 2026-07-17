package com.xiyuguopu.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 下单请求
 */
@Data
public class CreateOrderDTO {

    @NotNull(message = "地址不能为空")
    private Long addressId;

    @NotEmpty(message = "商品不能为空")
    private List<OrderItemDTO> items;

    /** 备注 */
    private String remark;

    @Data
    public static class OrderItemDTO {
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @NotNull(message = "数量不能为空")
        private Integer quantity;
    }
}
