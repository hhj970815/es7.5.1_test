package cn.es.search.model.bo;

import lombok.Data;

import java.util.List;


/**
 * 分页响应基础参数
 *
 * @author dengbh
 * @date 2020/06/21
 */
@Data
public class PageResponse<T> {
    private Long total;
    private Long currentPage;
    private Long pageSize;
    private List<T> records;
}
