package com.taiyidu.taiyidulibrary.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUserDto {


    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名/账号
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 积分余额
     */
    private Integer points;
}
