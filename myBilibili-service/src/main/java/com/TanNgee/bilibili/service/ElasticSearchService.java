package com.TanNgee.bilibili.service;

import com.TanNgee.bilibili.dao.repository.UserInfoRepository;
import com.TanNgee.bilibili.dao.repository.VideoRepository;
import com.TanNgee.bilibili.domain.UserInfo;
import com.TanNgee.bilibili.domain.Video;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.common.text.Text;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author TanNgee
 * @Date 2022/7/7 22:18
 **/
@Service
public class ElasticSearchService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 添加用户信息到ES
     *
     * @param userInfo
     */
    public void addUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    /**
     * 添加视频到ES
     *
     * @param video
     */
    public void addVideo(Video video) {
        videoRepository.save(video);
    }

    /**
     * 全文搜索
     *
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     */

    public List<Map<String, Object>> getContents(String keyword,
                                                 Integer pageNo,
                                                 Integer pageSize) throws IOException {
        String[] indices = {"videos", "user-infos"}; // 视频相关 用户信息
        SearchRequest searchRequest = new SearchRequest(indices);  // 索引

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); //查询相关的配置
        //分页

        sourceBuilder.from(pageNo - 1);
        sourceBuilder.size(pageSize);

        // 多条件查询的构建器
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "nick", "description");
        sourceBuilder.query(matchQueryBuilder);
        searchRequest.source(sourceBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));   //超时时间

        //高亮显示
        String[] array = {"title", "nick", "description"};
        HighlightBuilder highlightBuilder = new HighlightBuilder();  //高亮构建器

        for (String key : array) {
            highlightBuilder.fields().add(new HighlightBuilder.Field(key));
        }

        highlightBuilder.requireFieldMatch(false); //如果要多个字段进行高亮，要为false
        highlightBuilder.preTags("<span style=\"color:red\">");  //前置标签
        highlightBuilder.postTags("</span>"); //后置标签

        sourceBuilder.highlighter(highlightBuilder);   //设置高亮的配置

        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> arrayList = new ArrayList<>();

        //匹配到的条目
        for (SearchHit hit : searchResponse.getHits()) {

            //处理高亮字段
            Map<String, HighlightField> highLightBuilderFields = hit.getHighlightFields();
            Map<String, Object> sourceMap = hit.getSourceAsMap();

            for (String key : array) {
                HighlightField field = highLightBuilderFields.get(key);
                if (field != null) {
                    Text[] fragments = field.fragments();

                    String str = Arrays.toString(fragments);
                    str = str.substring(1, str.length() - 1);
                    sourceMap.put(key, str);
                }
            }

            arrayList.add(sourceMap);
        }
        return arrayList;
    }

    /**
     * 根据关键词从ES中查询视频
     *
     * @param keyword
     * @return
     */
    public Video getVideos(String keyword) {
        return videoRepository.findByTitleLike(keyword);
    }

    public void deleteAllVideos() {
        videoRepository.deleteAll();
    }
}
