package com.rbac.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rbac.common.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quota_rule")
public class QuotaRule extends BaseEntity {

    private String quotaName;

    private String indicatorCodes;

    private String conditions;

    private String quotaCalculation;

    private String resultVarName;

    private String calculatedResult;

    private String outputTemplate;
}
