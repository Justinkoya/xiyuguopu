package com.xiyuguopu.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.common.JwtUtils;
import com.xiyuguopu.dto.LoginDTO;
import com.xiyuguopu.dto.LoginVO;
import com.xiyuguopu.entity.SysUser;
import com.xiyuguopu.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 管理后台认证
 */
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final SysUserMapper sysUserMapper;

    /**
     * 账号密码登录，成功返回 token，失败抛异常
     */
    public LoginVO login(LoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
                        .eq(SysUser::getIsEnabled, true));

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = JwtUtils.createToken(user.getId(), user.getUsername());
        return LoginVO.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
