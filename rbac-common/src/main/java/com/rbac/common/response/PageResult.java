package com.rbac.common.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 分页响应结果
 *
 * @param <T> 数据类型
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageResult<T> extends Result<List<T>> {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Long pageNum;

    /**
     * 每页数量
     */
    private Long pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    public PageResult() {
        super();
    }

    public PageResult(Long pageNum, Long pageSize, Long total, List<T> data) {
        super.setCode(200);
        super.setMessage("操作成功");
        super.setData(data);
        super.setTimestamp(System.currentTimeMillis());
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = (total + pageSize - 1) / pageSize;
    }

    /**
     * 成功返回分页结果
     */
    public static <T> PageResult<T> success(Long pageNum, Long pageSize, Long total, List<T> data) {
        return new PageResult<>(pageNum, pageSize, total, data);
    }
}
