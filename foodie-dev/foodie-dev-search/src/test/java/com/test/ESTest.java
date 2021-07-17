package com.test;

import com.imooc.Application;
import com.imooc.es.pojo.Stu;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ESTest {


    @Autowired
    private ElasticsearchTemplate eshTemplate;

    /**
     * 不建议使用 ElasticsearchTemplate 对索引进行管理（创建索引、更新映射、删除索引）
     * 索引就像是数据库中的表，平时是不会通过java代码频繁的去创建修改删除数据库或者表的
     * 我们只会针对数据做crud的操作，在 es 中也是同理，尽量使用对文档数据做 crud 的操作
     *
     * 1.属性类型(Field)不灵活
     * 2.主分片和副本分片数无法设置
     */

    @Test
    public void createIndexStu(){
        Stu stu = new Stu();
        stu.setStuId(1002L);
        stu.setName("spider man");
        stu.setAge(22);

        stu.setMoney(18.8F);
        stu.setSign("i am a spider man");
        stu.setDescription("i wish i am spider man");
        IndexQuery iq = new IndexQueryBuilder().withObject(stu).build();
        eshTemplate.index(iq);
    }

    @Test
    public void deleteIndexStu(){
        eshTemplate.deleteIndex(Stu.class);
    }



//    --------------------------------------------我是分割线----------------------------------------------------------

    @Test
    public void updateStuDoc(){

        Map<String,Object> sourceMap = new HashMap<>();
        //sourceMap.put("sign","i am not super man");
        sourceMap.put("money",99.8f);
        //sourceMap.put("age",33);

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(sourceMap);

        UpdateQuery updateQuery = new UpdateQueryBuilder()
                .withClass(Stu.class)
                .withId("1002")
                .withIndexRequest(indexRequest)
                .build();

        eshTemplate.update(updateQuery);
    }

    @Test
    public void queryStuDoc(){

        GetQuery getQuery = new GetQuery();
        getQuery.setId("1002");

        Stu stu = eshTemplate.queryForObject(getQuery,Stu.class);
        System.out.println(stu);

    }

    @Test
    public void delStuDoc(){
       eshTemplate.delete(Stu.class,"1002");
    }

//    --------------------------------------------我是分割线----------------------------------------------------------

    @Test
    public void searchStuDoc(){
        Pageable pageable = PageRequest.of(1,2);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description","save man"))
                .withPageable(pageable)
                .build();
        AggregatedPage<Stu> pagedStu = eshTemplate.queryForPage(searchQuery, Stu.class);
        System.out.println("检索后的总分页："+pagedStu.getTotalPages());
        List<Stu> content = pagedStu.getContent();
        content.stream().forEach(System.out::println);
    }

    @Test
    public void highlightStuDoc(){

        String preTag = "<font color='red' >";
        String postTag = "</font>";

        Pageable pageable = PageRequest.of(0,10);
        SortBuilder sortBuilder = new FieldSortBuilder("money").order(SortOrder.DESC);
        SortBuilder sortBuilderAge = new FieldSortBuilder("age").order(SortOrder.ASC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description","save man"))
                .withHighlightFields(new HighlightBuilder.Field("description").preTags(preTag).postTags(postTag))
                .withSort(sortBuilder).withSort(sortBuilderAge)
                .withPageable(pageable)
                .build();
        AggregatedPage<Stu> pagedStu = eshTemplate.queryForPage(searchQuery, Stu.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<Stu> stuList = new ArrayList<>();
                SearchHits hits = searchResponse.getHits();
                for(SearchHit hit: hits){
                    HighlightField description = hit.getHighlightFields().get("description");
                    String s = description.getFragments()[0].toString();
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    Stu stuHL = new Stu();
                    stuHL.setStuId(Long.parseLong(sourceAsMap.get("stuId").toString()));
                    stuHL.setName((String)sourceAsMap.get("name"));
                    stuHL.setAge((Integer) sourceAsMap.get("age"));
                    stuHL.setSign((String)sourceAsMap.get("sign"));
                    stuHL.setMoney(Float.parseFloat(sourceAsMap.get("money").toString()));
                    stuHL.setDescription(s);
                    stuList.add(stuHL);
                }
                if(stuList.size() > 0){
                    return new AggregatedPageImpl<>((List<T>) stuList);
                }
                return null;
            }
        });
        System.out.println("检索后的总分页："+pagedStu.getTotalPages());
        List<Stu> content = pagedStu.getContent();
        content.stream().forEach(System.out::println);
    }






}
