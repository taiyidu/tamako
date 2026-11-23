package com.taiyidu.taiyidulibrary.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档浏览与下载记录表
 */
@Data
@TableName("doc_record")
public class DocRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 文档ID */
    private Long docId;

    /** 记录类型：1-浏览，2-下载 */
    private Integer recordType;

    /** 消耗积分 */
    private Integer pointsCost;

    /** 操作时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}