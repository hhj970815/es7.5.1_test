package cn.es.search.model.param;

import lombok.Data;

/**
 * 分页参数
 *
 * @author dengbh
 * @date 2020/06/21
 */
@Data
public class PageParam {
    private Long currentPage;
    private Long pageSize;
}
