package com.rbac.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 添加RPA数据采集请求DTO
 */

@Data
public class AddRPADataCollectionRequest {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotBlank(message = "纳税人识别号不能为空")
    private String taxNo;

    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;

    @NotBlank(message = "状态不能为空")
    private String status;

    @NotBlank(message = "数据来源不能为空")
    private String dataSource;

    @NotNull(message = "采集时间不能为空")
    private LocalDateTime collectionTime;

    @NotBlank(message = "错误信息不能为空")
    private String errorMsg;

    @NotBlank(message = "原始数据不能为空")
    private String rawData;
}
