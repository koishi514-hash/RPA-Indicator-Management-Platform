import { request } from '@umijs/max';

// 分页查询用户列表
export async function pageUserList(params: any) {
    return request('/v1/system/users', {
        method: 'GET',
        params,
    });
}
export async function getUserDetail(userId: number) {
    return request(`/v1/system/users/${userId}`, {
        method: 'GET',
    });
}
export async function createUser(data: any) {
    return request('/v1/system/users', {
        method: 'POST',
        data,
    });
}
export async function updateUser(userId: number, data: any) {
    return request(`/v1/system/users/${userId}`, {
        method: 'PUT',
        data,
    });
}
export async function resetUserPassword(userId: number, data?: any) {
    return request(`/v1/system/users/${userId}/password`, {
        method: 'PUT',
        data,
    });
}
export async function deleteUser(userId: number) {
    return request(`/v1/system/users/${userId}`, {
        method: 'DELETE',
    });
}
export async function changeUserStatus(userId: number, data: any) {
    return request(`/v1/system/users/${userId}/status`, {
        method: 'PATCH',
        data,
    });
}