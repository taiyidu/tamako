package com.taiyidu.taiyidulibrary.domain.dto;

import lombok.Data;


@Data
public class RegisterDTO {

    /**
     * 用户名/账号
     */
    private String username;

    /**
     * 登录密码（明文，service 层 MD5 后入库）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * QQ号
     */
    private String qq;

    /**
     * QQ邮箱
     */
    private String qqEmail;
}
