import { request } from "@umijs/max";

// 查询任务列表（分页）
export async function listTasks(params: any) {
    return request('/v1/system/tasks/list', {
        method: 'GET',
        params,
    });
}
// 新建任务
export async function createTask(data: any) {
    return request('/v1/system/tasks/create', {
        method: 'POST',
        data,
    });
}
// 编辑任务
export async function updateTask(data: any) {
    return request('/v1/system/tasks/update', {
        method: 'POST',
        data,
    });
}
// 查看任务详情
export async function getTaskDetail(taskCode: string) {
    return request(`/v1/system/tasks/detail/${taskCode}`, {
        method: 'GET',
    });
}
// 执行任务
export async function executeTask(taskCode: string) {
    return request(`/v1/system/tasks/execute/${taskCode}`, {
        method: 'POST',
    });
}
// 删除任务
export async function deleteTask(taskCode: string) {
    return request(`/v1/system/tasks/delete/${taskCode}`, {
        method: 'DELETE',
    });
}