package com.taiyidu.taiyidulibrary.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档评论表
 */
@Data
@TableName("doc_comment")
public class DocComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评论ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 文档ID */
    private Long docId;

    /** 评论用户ID */
    private Long userId;

    /** 评论内容 */
    private String content;

    /** 状态：1-正常，0-隐藏 */
    private Integer status;

    /** 评论时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除：0-未删除，1-已删除 */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}