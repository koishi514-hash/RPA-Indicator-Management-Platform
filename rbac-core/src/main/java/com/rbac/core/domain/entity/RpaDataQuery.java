package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据查询表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rpa_data_query")
public class RpaDataQuery extends BaseEntity {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 纳税人识别号
     */
    private String taxNo;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 税区ID
     */
    private Long categoryId;

    /**
     * 业务数据（JSON）
     */
    private String businessData;

    /**
     * 数据状态：available/archived
     */
    private Integer dataStatus;

    /**
     * 逻辑删除：0-否，1-是
     */
    private Integer deleted;
}