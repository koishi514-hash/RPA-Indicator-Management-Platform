import { request } from '@umijs/max';
export async function listProcesses(params: any) {
    return request('/v1/system/processes/list', {
        method: 'GET',
        params,
    });
}
// 新增流程
export async function createProcess(data: any) {
    return request('/v1/system/processes/create', {
        method: 'POST',
        data,
    });
}
// 编辑流程
export async function updateProcess(data: any) {
    return request('/v1/system/processes/update', {
        method: 'POST',
        data,
    });
}
// 删除流程
export async function deleteProcess(processCode: string) {
    return request(`/v1/system/processes/delete/${processCode}`, {
        method: 'DELETE',
    });
}

// 查询流程步骤
export async function listProcessSteps(processCode: string) {
    return request(`/v1/system/processes/step/list/${processCode}`, {
        method: 'GET',
    });
}

// 保存流程步骤
export async function saveProcessSteps(data: any) {
    return request('/v1/system/processes/step/save', {
        method: 'POST',
        data,
    });
}
