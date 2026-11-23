package com.taiyidu.taiyidulibrary.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档收藏表
 */
@Data
@TableName("doc_favorite")
public class DocFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 收藏记录ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 文档ID */
    private Long docId;

    /** 收藏时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}