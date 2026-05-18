import { request } from '@umijs/max';

export const userLogin = (data: any) => {
    return request('/v1/system/login', {
        method: 'POST',
        data
    });
};
export const getAuthInfo = () => {
    return request('/v1/system/auth/info', {
        method: 'GET',
    });
};