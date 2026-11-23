package com.taiyidu.taiyidulibrary.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档主表
 */
@Data
@TableName("doc_info")
public class DocInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文档ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 文档标题 */
    private String title;

    /** 文档简介/摘要 */
    private String summary;

    /** 分类ID */
    private Long categoryId;

    /** 上传者用户ID */
    private Long uploaderId;

    /** 文件存储路径或URL */
    private String fileUrl;

    /** 封面图URL */
    private String coverUrl;

    /** 文件格式（pdf/docx/pptx/md等） */
    private String fileExt;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文档页数 */
    private Integer pageCount;

    /** 下载所需积分（0表示免费） */
    private Integer downloadPoints;

    /** 审核状态：0-待审核，1-已发布，2-已驳回，3-已下架 */
    private Integer auditStatus;

    /** 审核驳回原因 */
    private String rejectReason;

    /** 浏览量 */
    private Long viewCount;

    /** 下载量 */
    private Long downloadCount;

    /** 收藏量 */
    private Integer collectCount;

    /** 创建时间 */
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