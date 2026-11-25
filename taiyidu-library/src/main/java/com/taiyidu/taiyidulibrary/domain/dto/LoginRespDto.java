package com.taiyidu.taiyidulibrary.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRespDto {
    private String token;
    private String username;
}
