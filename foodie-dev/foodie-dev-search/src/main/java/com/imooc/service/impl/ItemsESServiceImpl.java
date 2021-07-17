package com.imooc.service.impl;

import com.imooc.es.pojo.Items;
import com.imooc.es.pojo.Stu;
import com.imooc.service.ItemsESService;
import com.imooc.utils.PagedGridResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemsESServiceImpl implements ItemsESService {

    @Autowired
    private ElasticsearchTemplate eshTemplate;


    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {

        String preTag = "<font color='red' >";
        String postTag = "</font>";

        String itemNameField = "itemName";

        Pageable pageable = PageRequest.of(page,pageSize);
        SortBuilder sortBuilder = null;
        if(sort.equals("c")){
            sortBuilder = new FieldSortBuilder("sellCounts").order(SortOrder.DESC);
        }else if(sort.equals("p")){
            sortBuilder = new FieldSortBuilder("price").order(SortOrder.ASC);
        }else {
            sortBuilder = new FieldSortBuilder("itemName.keyword").order(SortOrder.ASC);
        }
//        SortBuilder sortBuilderAge = new FieldSortBuilder("age").order(SortOrder.ASC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(itemNameField,keywords))
                .withHighlightFields(new HighlightBuilder.Field(itemNameField)
//                        .preTags(preTag).postTags(postTag)
                )
                .withSort(sortBuilder)
//                .withSort(sortBuilderAge)
                .withPageable(pageable)
                .build();
        AggregatedPage<Items> pagedItems = eshTemplate.queryForPage(searchQuery, Items.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<Items> itemsList = new ArrayList<>();
                SearchHits hits = searchResponse.getHits();
                for(SearchHit hit: hits){
                    HighlightField description = hit.getHighlightFields().get(itemNameField);
                    String itemName = description.getFragments()[0].toString();
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    Items itemsHL = new Items();
                    itemsHL.setItemId((String)sourceAsMap.get("itemId"));
                    itemsHL.setItemName(itemName);
                    itemsHL.setImgUrl((String) sourceAsMap.get("imgUrl"));
                    itemsHL.setPrice(Integer.parseInt(sourceAsMap.get("price").toString()));
                    itemsHL.setSellCounts(Integer.parseInt(sourceAsMap.get("sellCounts").toString()));
                    itemsList.add(itemsHL);
                }
                return new AggregatedPageImpl<>((List<T>) itemsList,pageable,searchResponse.getHits().getTotalHits());
            }
        });
//        System.out.println("检索后的总分页："+pagedStu.getTotalPages());
//        List<Items> content = pagedStu.getContent();
//        content.stream().forEach(System.out::println);
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(pagedItems.getContent());
        gridResult.setPage(page + 1);
        gridResult.setTotal(pagedItems.getTotalPages());
        gridResult.setRecords(pagedItems.getTotalElements());
        return gridResult;
    }


}
