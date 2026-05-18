import { request } from "@umijs/max";

// 指标管理 API

export async function listIndicators(params: { keyword?: string; pageNum?: number; pageSize?: number }) {
    return request('/v1/indicators/list', {
        method: 'GET',
        params,
    });
}

export async function getAllIndicators() {
    return request('/v1/indicators/all', {
        method: 'GET',
    });
}

export async function getIndicatorDetail(id: number) {
    return request(`/v1/indicators/detail/${id}`, {
        method: 'GET',
    });
}

export async function createIndicator(data: { indicatorName: string; indicatorCode: string; indicatorLogic: string; taskId?: number }) {
    return request('/v1/indicators/create', {
        method: 'POST',
        data,
    });
}

export async function updateIndicator(data: { id: number; indicatorName: string; indicatorCode: string; indicatorLogic: string; taskId?: number }) {
    return request('/v1/indicators/update', {
        method: 'POST',
        data,
    });
}

export async function deleteIndicator(id: number) {
    return request(`/v1/indicators/delete/${id}`, {
        method: 'DELETE',
    });
}

// 审核规则 API
export async function listQuotaRules(params: { keyword?: string; pageNum?: number; pageSize?: number }) {
    return request('/v1/quota-rules/list', {
        method: 'GET',
        params,
    });
}

export async function createQuotaRule(data: { quotaName: string; indicatorCodes: string; conditions: string; quotaCalculation: string; resultVarName: string; outputTemplate: string }) {
    return request('/v1/quota-rules/create', {
        method: 'POST',
        data,
    });
}

export async function updateQuotaRule(data: { id: number; quotaName: string; indicatorCodes: string; conditions: string; quotaCalculation: string; resultVarName: string; outputTemplate: string }) {
    return request('/v1/quota-rules/update', {
        method: 'POST',
        data,
    });
}

export async function getQuotaRuleDetail(id: number) {
    return request(`/v1/quota-rules/detail/${id}`, {
        method: 'GET',
    });
}

export async function deleteQuotaRule(id: number) {
    return request(`/v1/quota-rules/delete/${id}`, {
        method: 'DELETE',
    });
}

export async function calculateQuotaRule(data: { quotaRuleId: number; data?: any }) {
    return request('/v1/quota-rules/calculate', {
        method: 'POST',
        data,
    });
}
