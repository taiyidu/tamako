package com.taiyidu.taiyidulibrary.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.taiyidu.taiyiducommon.exception.BusinessException;
import com.taiyidu.taiyidulibrary.domain.dto.LoginDTO;
import com.taiyidu.taiyidulibrary.domain.dto.LoginRespDto;
import com.taiyidu.taiyidulibrary.domain.dto.RegisterDTO;
import com.taiyidu.taiyidulibrary.domain.dto.SysUserDto;
import com.taiyidu.taiyidulibrary.domain.po.SysUser;
import com.taiyidu.taiyidulibrary.mapper.UserManagerMapper;
import com.taiyidu.taiyidulibrary.service.UserManagerService;
import com.taiyidu.taiyidulibrary.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagerServiceImpl implements UserManagerService {
    private final UserManagerMapper userManagerMapper;
    private final JwtUtil jwtUtil;

    /**
     * 这个只是一个登录的接口应该不需要写redis，没有什么必要
     * @param loginDTO 登录参数
     * @return 登录结果
     */
    @Override
    public LoginRespDto login(LoginDTO loginDTO) {
        // 用户信息校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper.eq(SysUser::getUsername, username);
        //TODO 后面注意一下好像得给名字上索引
        SysUser sysUser = userManagerMapper.selectOne(sysUserLambdaQueryWrapper);
        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }
        if(!sysUser.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
//            log.info("前端{}", DigestUtils.md5DigestAsHex(password.getBytes()));
//            log.info("后端{}", sysUser.getPassword());
            throw new BusinessException("账号或者密码错误");
        }
        // 构造登录token返回信息
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("role", sysUser.getRole());
        claims.put("userId", sysUser.getId());
        String token = jwtUtil.generateToken(sysUser.getUsername(),claims);
        return new LoginRespDto(token, sysUser.getUsername());
    }

    /**
     * 展示个人的详细信息的接口
     * @return  用户详细信息
     */
    @Override
    public SysUserDto getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new BusinessException("登录消息有误或者位置错误，请向管理员申报");
        }
        String userId = (String) auth.getPrincipal(); // 拿网关传的 ID
        SysUser sysUser = userManagerMapper.selectById(userId);
        return BeanUtil.copyProperties(sysUser, SysUserDto.class);
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        if (!StringUtils.hasText(registerDTO.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (!StringUtils.hasText(registerDTO.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        SysUser sysUser = new SysUser();
        if (!StringUtils.hasText(registerDTO.getNickname())){
            sysUser.setNickname(registerDTO.getNickname());
        }
        BeanUtil.copyProperties(registerDTO, sysUser);
        if (StringUtils.hasText(registerDTO.getQq())){
            sysUser.setAvatar("https://q.qlogo.cn/headimg_dl?dst_uin="+registerDTO.getQq()+"&spec=640");
        }
        sysUser.setPassword(DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes()));
        sysUser.setPoints(100);
        sysUser.setStatus(1);
        sysUser.setRole("ROLE_USER");
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setUpdateTime(LocalDateTime.now());
        userManagerMapper.insert(sysUser);
    }
}
