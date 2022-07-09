package com.TanNgee.bilibili.dao.repository;

import com.TanNgee.bilibili.domain.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ES 操作
 *
 * @Author TanNgee
 * @Date 2022/7/7 22:20
 **/
public interface UserInfoRepository extends ElasticsearchRepository<UserInfo, Long> {
}
