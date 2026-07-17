package com.xiyuguopu.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 登录成功返回
 */
@Data
@Builder
public class LoginVO {

    private String token;
    private String username;
    private String role;
}
