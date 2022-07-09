package com.TanNgee.bilibili.dao.repository;

import com.TanNgee.bilibili.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 将视频数据保存到ES
 *
 * @Author TanNgee
 * @Date 2022/7/7 22:21
 **/
public interface VideoRepository extends ElasticsearchRepository<Video, Long> {
    // ES会自动进行拆分  find by title like
    Video findByTitleLike(String keyword);

}
