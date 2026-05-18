import { request } from '@umijs/max';
// 分页查询角色列表
export async function pageRoleList(params: any) {
    return request('/v1/system/roles', {
        method: 'GET',
        params,
    });
}
export async function createRole(data: any) {
    return request('/v1/system/roles', {
        method: 'POST',
        data,
    });
}
// 修改角色（基本信息 + 权限分配）
export async function updateRole(roleId: number, data: any) {
    return request(`/v1/system/roles/${roleId}`, {
        method: 'PUT',
        data,
    });
}
// 删除角色
export async function deleteRole(roleId: number) {
    return request(`/v1/system/roles/${roleId}`, {
        method: 'DELETE',
    });
}
export async function listAllResources() {
    return request('/v1/system/roles/resources', {
        method: 'GET',
    });
}