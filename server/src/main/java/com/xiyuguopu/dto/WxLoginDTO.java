package com.xiyuguopu.dto;

import lombok.Data;

/**
 * 微信登录请求
 */
@Data
public class WxLoginDTO {
    /** 微信登录 code，mock 模式下直接用 openid */
    private String code;
}
