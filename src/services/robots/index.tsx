import { request } from "@umijs/max";
// 查询机器人列表（分页）
export async function listRobots(params: any) {
    return request('/v1/system/robots/list', {
        method: 'GET',
        params,
    });
}
// 新增机器人
export async function createRobot(data: any) {
    return request('/v1/system/robots/create', {
        method: 'POST',
        data,
    });
}
// 编辑机器人
export async function updateRobot(data: any) {
    return request('/v1/system/robots/update', {
        method: 'POST',
        data,
    });
}
// 查看机器人详情
export async function getRobotDetail(robotCode: string) {
    return request(`/v1/system/robots/detail/${robotCode}`, {
        method: 'GET',
    });
}
// 删除机器人
export async function deleteRobot(robotCode: string) {
    return request(`/v1/system/robots/delete/${robotCode}`, {
        method: 'DELETE',
    });
}