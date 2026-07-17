package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.common.JwtUtils;
import com.xiyuguopu.dto.WxLoginDTO;
import com.xiyuguopu.dto.WxLoginVO;
import com.xiyuguopu.entity.User;
import com.xiyuguopu.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 微信登录（Mock 模式 — 本地开发无需真实微信 API）
 */
@Service
@RequiredArgsConstructor
public class WxAuthService {

    private final UserMapper userMapper;

    public WxLoginVO login(WxLoginDTO dto) {
        String code = dto.getCode();
        if (code == null || code.isBlank()) {
            throw new RuntimeException("code 不能为空");
        }

        // Mock 模式：把 code 直接当做 openid
        String openid = code.trim();

        // 查是否已有该用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));

        if (user == null) {
            // 自动注册新用户
            user = new User();
            user.setOpenid(openid);
            user.setNickname("用户" + openid.substring(Math.max(0, openid.length() - 6)));
            user.setAvatar("");
            userMapper.insert(user);
        }

        String token = JwtUtils.createUserToken(user.getId(), user.getOpenid());

        return WxLoginVO.builder()
                .token(token)
                .user(WxLoginVO.UserInfo.builder()
                        .id(user.getId())
                        .nickname(user.getNickname())
                        .avatar(user.getAvatar())
                        .build())
                .build();
    }
}
