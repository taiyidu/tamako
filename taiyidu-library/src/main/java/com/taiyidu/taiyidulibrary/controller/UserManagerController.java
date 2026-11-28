package com.taiyidu.taiyidulibrary.controller;

import com.taiyidu.taiyiducommon.result.Result;
import com.taiyidu.taiyidulibrary.domain.dto.LoginDTO;
import com.taiyidu.taiyidulibrary.domain.dto.LoginRespDto;
import com.taiyidu.taiyidulibrary.domain.dto.RegisterDTO;
import com.taiyidu.taiyidulibrary.domain.dto.SysUserDto;
import com.taiyidu.taiyidulibrary.service.UserManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/usermanager")
@RequiredArgsConstructor
@RestController
public class UserManagerController {
    private final UserManagerService userManagerService;
    @PostMapping("/login")
    public Result<LoginRespDto> login(@RequestBody LoginDTO loginDTO) {
        log.info("login: {}", loginDTO.getUsername());
        LoginRespDto loginRespDto = userManagerService.login(loginDTO);
        return Result.success(loginRespDto);
    }
    @GetMapping("/showuserinfo")
    public Result<SysUserDto> getUserInfo() {
        log.info("get user info");
        return Result.success(userManagerService.getUserInfo());
    }
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        log.info("register: {}", registerDTO.getUsername());
        userManagerService.register(registerDTO);
        return Result.success("注册成功");
    }
}
