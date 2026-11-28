package com.taiyidu.taiyidulibrary.service;

import com.taiyidu.taiyidulibrary.domain.dto.LoginDTO;
import com.taiyidu.taiyidulibrary.domain.dto.LoginRespDto;
import com.taiyidu.taiyidulibrary.domain.dto.RegisterDTO;
import com.taiyidu.taiyidulibrary.domain.dto.SysUserDto;

public interface UserManagerService {
    LoginRespDto login(LoginDTO loginDTO);

    SysUserDto getUserInfo();

    void register(RegisterDTO registerDTO);
}
