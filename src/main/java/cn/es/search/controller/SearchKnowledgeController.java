package cn.es.search.controller;

import cn.es.search.model.bo.ListWareDetailBo;
import cn.es.search.model.bo.ResponseData;
import cn.es.search.model.bo.WareDetailBo;
import cn.es.search.model.param.SearchWareByKeyWordParam;
import cn.es.search.service.SearchKnowledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author huanghuajie
 * @version 1.0
 * @date 2021/4/6 9:44
 */

@Slf4j
@RestController
@RequestMapping("/api/searchknowledge")
public class SearchKnowledgeController {

    @Autowired
    private SearchKnowledgeService searchKnowledgeService;

    @GetMapping("/{keyword}/{pageNo}/{pageSize}")
    public ResponseData<ListWareDetailBo> getWareDetailBo(@PathVariable String keyword,
                                                          @PathVariable long pageNo,
                                                          @PathVariable long pageSize) {
        SearchWareByKeyWordParam searchWareByKeyWordParam = new SearchWareByKeyWordParam();
        searchWareByKeyWordParam.setKeyWord(keyword);
        searchWareByKeyWordParam.setCurrentPage(pageNo);
        searchWareByKeyWordParam.setPageSize(pageSize);
        return ResponseData.success(searchKnowledgeService.searchByKeyWord(searchWareByKeyWordParam));
    }

    @GetMapping("/kw/{keyword}")
    public ResponseData<List<String>> getSuggestWord(@PathVariable String keyword) {
        return ResponseData.success(searchKnowledgeService.getSuggestWord(keyword));
    }
}
