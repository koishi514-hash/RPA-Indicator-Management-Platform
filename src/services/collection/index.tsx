// services/collection.ts
import { request } from '@umijs/max';

/** 9.1 采集数据列表（分页 + 筛选） */
export async function getCollectionList(params: any) {
    return request('/v1/system/data/collection/list', {
        method: 'GET',
        params,
    })
}

/** 9.2 查看采集数据详情 */
export async function getCollectionDetail(collectionId: number) {
    return request(`/v1/system/data/collection/detail/${collectionId}`, {
        method: 'GET',
    });
}

/** 9.3 删除采集数据 */
export async function deleteCollection(collectionId: number) {
    return request(`/v1/system/data/collection/delete/${collectionId}`, {
        method: 'DELETE',
    });
}

/** 9.4 新增采集数据 */
export async function addCollection(data: any) {
    return request('/v1/system/data/collection/add', {
        method: 'POST',
        data,
    });
}