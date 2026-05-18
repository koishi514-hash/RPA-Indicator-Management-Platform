import { request } from "@umijs/max";

// 查询所有资源（树形结构，角色授权使用）
export async function listAllResources(params: any) {
    return request('/v1/system/resources', {
        method: 'GET',
        params,
    });
}
export async function createResource(data: any) {
    return request('/v1/system/resources', {
        method: 'POST',
        data,
    });
}
// 修改资源
export async function updateResource(resourceId: any, data: any) {
    return request(`/v1/system/resources/${resourceId}`, {
        method: 'PUT',
        data,
    });
}
// 删除资源
export async function deleteResource(resourceId: any) {
    return request(`/v1/system/resources/${resourceId}`, {
        method: 'DELETE',
    });
}