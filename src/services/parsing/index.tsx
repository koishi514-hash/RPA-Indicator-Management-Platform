import { request } from '@umijs/max';

export async function getParsingList(params: any) {
    return request('/v1/system/data/parsing/list', {
        method: 'GET',
        params,
    });
}

export async function getParsingDetail(parsingId: number) {
    return request(`/v1/system/data/parsing/detail/${parsingId}`, {
        method: 'GET',
    });
}

export async function deleteParsing(parsingId: number) {
    return request(`/v1/system/data/parsing/delete/${parsingId}`, {
        method: 'DELETE',
    });
}
