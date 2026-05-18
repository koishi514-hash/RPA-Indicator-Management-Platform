// 个人信息相关服务
import { request } from '@umijs/max';

// 获取个人信息
export const getProfile = () => {
    return request('/v1/system/profile', {
        method: 'GET',
    });
};

// 更新个人信息
export async function updateProfile(data: any) {
    return request('/v1/system/profile', {
        method: 'PUT',
        data,
    });
}

// 修改密码
export async function updatePassword(data: any) {
    return request('/v1/system/profile/password', {
        method: 'PUT',
        data,
    });
}
