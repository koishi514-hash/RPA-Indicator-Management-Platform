package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 数据采集表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rpa_data_collection")
public class RpaDataCollection extends BaseEntity {

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
     * 原始数据（JSON）
     */
    private String rawData;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 采集时间
     */
    private LocalDateTime collectionTime;

    /**
     * 状态：collected/parsing/parsed/failed
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 逻辑删除：0-否，1-是
     */
    private Integer deleted;
}