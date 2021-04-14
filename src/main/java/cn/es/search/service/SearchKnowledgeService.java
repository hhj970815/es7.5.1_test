package cn.es.search.service;

import cn.es.search.model.bo.ListWareDetailBo;
import cn.es.search.model.bo.PageResponse;
import cn.es.search.model.bo.WareDetailBo;
import cn.es.search.model.param.SearchWareByKeyWordParam;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 根据关键字查询商品库
 *
 * @author huanghuajie
 * @version 1.0
 * @date 2021/4/6 9:48
 */

@Service
@Slf4j
public class SearchKnowledgeService {

    @Qualifier("restHighLevelClient")
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 建议请求
     *
     * @param keyWord
     * @return
     */
    public List<String> getSuggestWord(String keyWord) {
        List<String> suggestKw = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest("knowledge_ware");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        TermSuggestionBuilder term = SuggestBuilders.termSuggestion("name").text(keyWord);
        CompletionSuggestionBuilder completionSuggestionBuilder = SuggestBuilders.completionSuggestion("name.suggest").prefix(keyWord);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        // 添加建议生成器并命名
        suggestBuilder.addSuggestion("suggest_ware", completionSuggestionBuilder);
        // 将suggestBuilder添加到searchSourceBuilder
        searchSourceBuilder.suggest(suggestBuilder);
        // 执行搜索
        searchRequest.source(searchSourceBuilder);

        SearchResponse search = null;
        try {
            search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }

        // 按suggest搜索结果
        Suggest suggest = search.getSuggest();
        // 按content搜索Suggest
        TermSuggestion termSuggestion = suggest.getSuggestion("name.suggest");
        if (termSuggestion == null) {
            log.info("查无数据");
            return new ArrayList<>();
        }
        for (TermSuggestion.Entry entry : termSuggestion.getEntries()) {
            String suggestText = entry.getText().string();
            suggestKw.add(suggestText);
        }
        return suggestKw;
    }

    public ListWareDetailBo searchByKeyWord(SearchWareByKeyWordParam searchWareByKeyWordParam) {
        ListWareDetailBo listWareDetailBo = new ListWareDetailBo();
        List<WareDetailBo> wareDetailBoList = new ArrayList<>();
        int pageNo = searchWareByKeyWordParam.getCurrentPage().intValue();
        if (pageNo <= 0) {
            pageNo = 0;
        } else {
            pageNo = pageNo - 1;
        }
        log.info("搜索关键字-{}", searchWareByKeyWordParam.getKeyWord());
        // 条件搜索
        SearchRequest knowledgeWare = new SearchRequest("knowledge_ware");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(searchWareByKeyWordParam.getPageSize().intValue());

        // 精准匹配
//        TermQueryBuilder title = QueryBuilders.termQuery("content", keywords);
        // 组合查询
//        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
//                .multiMatchQuery(keywords, "title", "content");
        BoolQueryBuilder should = QueryBuilders
                .boolQuery()
                .should(QueryBuilders.termQuery("title", searchWareByKeyWordParam.getKeyWord()))
                .should(QueryBuilders.termQuery("content", searchWareByKeyWordParam.getKeyWord()).boost(5f));
//        TermQueryBuilder content = QueryBuilders.termQuery("content", keywords);
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(searchWareByKeyWordParam.getKeyWord()
                , "name", "indro", "avail_tag", "pro_view",
                "forbidden", "bad_fact", "main_func", "usage_num", "attention",
                "common_name", "alias", "med_type", "med_sort", "med_form", "med_way", "med_sick");



        searchSourceBuilder.query(multiMatchQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 执行查询
        knowledgeWare.source(searchSourceBuilder);
        SearchResponse search = null;
        try {
            search = restHighLevelClient.search(knowledgeWare, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        if (search == null) {
            return listWareDetailBo;
        }

        // 解析对象
        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            WareDetailBo wareDetailBo = new WareDetailBo();
            wareDetailBo.setId(Integer.parseInt(hit.getId()));
            wareDetailBo.setWarename(sourceAsMap.get("name").toString());
            wareDetailBo.setIntro(sourceAsMap.get("indro").toString());
            wareDetailBo.setCommonName(sourceAsMap.get("common_name").toString());
            wareDetailBo.setAlias(sourceAsMap.get("alias").toString());
            wareDetailBo.setMedType(sourceAsMap.get("med_type").toString());
            wareDetailBo.setMedSort(sourceAsMap.get("med_sort").toString());
            wareDetailBo.setMedForm(sourceAsMap.get("med_form").toString());
            wareDetailBo.setIsChu(sourceAsMap.get("is_chu").toString());
            wareDetailBo.setIsYibao(sourceAsMap.get("is_yibao").toString());
            wareDetailBo.setIsJiyao(sourceAsMap.get("is_jiyao").toString());
            wareDetailBo.setMedWay(sourceAsMap.get("med_way").toString());
            wareDetailBo.setMedSick(sourceAsMap.get("med_sick").toString());
            wareDetailBo.setWareUrlList(new ArrayList<>());
            wareDetailBo.setAvailTagsList(new ArrayList<>());
            wareDetailBo.setSuitSymptom(sourceAsMap.get("pro_view").toString());
            wareDetailBo.setForbidden(sourceAsMap.get("forbidden").toString());
            wareDetailBo.setBadFact(sourceAsMap.get("bad_fact").toString());
            wareDetailBo.setMainFunc(sourceAsMap.get("main_func").toString());
            wareDetailBo.setUsageNum(sourceAsMap.get("usage_num").toString());
            wareDetailBo.setAttention(sourceAsMap.get("attention").toString());

            wareDetailBoList.add(wareDetailBo);
        }
        listWareDetailBo.setCurrentPage(searchWareByKeyWordParam.getCurrentPage());
        listWareDetailBo.setPageSize(searchWareByKeyWordParam.getPageSize());
        listWareDetailBo.setRecords(wareDetailBoList);
        listWareDetailBo.setTotal(search.getHits().getTotalHits().value);

        return listWareDetailBo;
    }
}
