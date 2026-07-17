package com.xiyuguopu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信登录响应
 */
@Data
@Builder
public class WxLoginVO {
    private String token;

    @Builder.Default
    private UserInfo user = new UserInfo();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatar;
    }
}
