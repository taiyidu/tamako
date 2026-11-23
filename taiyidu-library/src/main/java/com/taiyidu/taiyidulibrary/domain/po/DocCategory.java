package com.taiyidu.taiyidulibrary.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档分类表
 */
@Data
@TableName("doc_category")
public class DocCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 父分类ID，0表示顶级分类 */
    private Long parentId;

    /** 分类名称 */
    private String name;

    /** 排序权重（越大越靠前） */
    private Integer sort;

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