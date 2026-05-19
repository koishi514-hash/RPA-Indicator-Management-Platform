package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 数据解析表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rpa_data_parsing")
public class RpaDataParsing extends BaseEntity {

    /**
     * 采集数据ID
     */
    private Long collectionId;

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
     * 解析数据（JSON）
     */
    private String parsedData;

    /**
     * 解析时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime parsingTime;

    /**
     * 状态：parsed/processing/processed/failed
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