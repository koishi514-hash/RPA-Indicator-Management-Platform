// services/processing.ts
import { request } from '@umijs/max';

/** 11.1 加工数据列表（分页 + 筛选） */
export async function getProcessingList(params: any) {
    return request('/v1/system/data/processing/list', {
        method: 'GET',
        params,
    });
}

/** 11.2 查看加工数据详情 */
export async function getProcessingDetail(processingId: number) {
    return request(`/v1/system/data/processing/detail/${processingId}`, {
        method: 'GET',
    });
}

/** 11.3 删除加工数据 */
export async function deleteProcessing(processingId: number) {
    return request(`/v1/system/data/processing/delete/${processingId}`, {
        method: 'DELETE',
    });
}