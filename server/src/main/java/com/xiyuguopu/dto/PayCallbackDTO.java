package com.xiyuguopu.dto;

import lombok.Data;

/**
 * 支付回调
 */
@Data
public class PayCallbackDTO {
    private Long orderId;
    private String transactionId;
}
