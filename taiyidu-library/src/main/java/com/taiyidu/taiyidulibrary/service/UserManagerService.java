package com.taiyidu.taiyidulibrary.service;

import com.taiyidu.taiyidulibrary.domain.dto.LoginDTO;
import com.taiyidu.taiyidulibrary.domain.dto.LoginRespDto;

public interface UserManagerService {
    LoginRespDto login(LoginDTO loginDTO);
}
