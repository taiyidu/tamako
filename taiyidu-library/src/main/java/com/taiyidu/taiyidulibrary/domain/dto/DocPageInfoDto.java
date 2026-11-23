package com.taiyidu.taiyidulibrary.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocPageInfoDto {
    /** 文档ID */
    private Long id;

    /** 文档标题 */
    private String title;

    /** 文档简介/摘要 */
    private String summary;

    /** 分类ID */
    private Long categoryId;

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
}
