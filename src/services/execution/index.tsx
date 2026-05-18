import { request } from "@umijs/max";

// 查询执行记录列表（分页）
export async function listExecutions(params: any) {
    return request('/v1/system/executions/list', {
        method: 'GET',
        params,
    });
}
// 查看执行记录详情（含步骤）
export async function getExecutionDetail(executionId: number) {
    return request(`/v1/system/executions/detail/${executionId}`, {
        method: 'GET',
    });
}