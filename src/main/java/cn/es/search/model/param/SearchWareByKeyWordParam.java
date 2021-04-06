package cn.es.search.model.param;

import lombok.Data;

/**
 * @author huanghuajie
 * @version 1.0
 * @date 2021/4/6 14:10
 */

@Data
public class SearchWareByKeyWordParam extends PageParam {
    private String keyWord;
}
