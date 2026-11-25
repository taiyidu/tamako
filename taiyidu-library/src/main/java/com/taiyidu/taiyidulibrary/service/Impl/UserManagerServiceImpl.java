package com.taiyidu.taiyidulibrary.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.taiyidu.taiyiducommon.exception.BusinessException;
import com.taiyidu.taiyidulibrary.domain.dto.LoginDTO;
import com.taiyidu.taiyidulibrary.domain.dto.LoginRespDto;
import com.taiyidu.taiyidulibrary.domain.po.SysUser;
import com.taiyidu.taiyidulibrary.mapper.UserManagerMapper;
import com.taiyidu.taiyidulibrary.service.UserManagerService;
import com.taiyidu.taiyidulibrary.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagerServiceImpl implements UserManagerService {
    private final UserManagerMapper userManagerMapper;
    private final JwtUtil jwtUtil;
    @Override
    public LoginRespDto login(LoginDTO loginDTO) {
        // 用户信息校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = userManagerMapper.selectOne(sysUserLambdaQueryWrapper);
        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }
        if(!sysUser.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
            log.info("前端{}", DigestUtils.md5DigestAsHex(password.getBytes()));
            log.info("后端{}", sysUser.getPassword());
            throw new BusinessException("账号或者密码错误");
        }
        // 构造登录token返回信息
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("Permission", sysUser.getRole());
        String token = jwtUtil.generateToken(sysUser.getUsername(),claims);
        return new LoginRespDto(token, sysUser.getUsername());
    }
}
