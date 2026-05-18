// services/query.ts
import { request } from '@umijs/max';

/** 12.1 查询数据列表（分页 + 筛选） */
export async function getQueryList(params: any) {
    return request('/v1/system/data/query/list', {
        method: 'GET',
        params,
    });
}

/** 12.2 查看查询数据详情 */
export async function getQueryDetail(queryId: number) {
    return request(`/v1/system/data/query/detail/${queryId}`, {
        method: 'GET',
    });
}

/** 12.3 删除查询数据 */
export async function deleteQuery(queryId: number) {
    return request(`/v1/system/data/query/delete/${queryId}`, {
        method: 'DELETE',
    });
}