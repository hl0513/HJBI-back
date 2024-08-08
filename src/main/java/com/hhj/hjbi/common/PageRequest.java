package com.hhj.hjbi.common;

import com.hhj.hjbi.constant.CommonConstant;
import lombok.Data;

/**
 * 分页请求
 *
 * @author <a href="https://github.com/hl0513">程序员泥嚎鸭</a>
 * @from <a href="https://www.yuque.com/dashboard/books">泥嚎鸭的语雀笔记</a>
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
