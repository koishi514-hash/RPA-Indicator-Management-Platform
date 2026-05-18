package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 数据加工表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rpa_data_processing")
public class RpaDataProcessing extends BaseEntity {

    /**
     * 解析数据ID
     */
    private Long parsingId;

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
     * 加工数据（JSON）
     */
    private String processedData;

    /**
     * 验证结果（JSON）
     */
    private String validationResult;

    /**
     * 加工时间
     */
    private LocalDateTime processingTime;

    /**
     * 状态：processed/exported/failed
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