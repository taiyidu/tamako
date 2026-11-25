package com.taiyidu.taiyidulibrary.controller;

import com.taiyidu.taiyiducommon.result.Result;
import com.taiyidu.taiyidulibrary.domain.dto.LoginDTO;
import com.taiyidu.taiyidulibrary.domain.dto.LoginRespDto;
import com.taiyidu.taiyidulibrary.service.UserManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
