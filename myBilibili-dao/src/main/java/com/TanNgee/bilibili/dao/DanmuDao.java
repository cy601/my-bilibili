package com.TanNgee.bilibili.dao;

import com.TanNgee.bilibili.domain.Danmu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author TanNgee
 * @Date 2022/7/4 22:16
 **/
@Mapper
public interface DanmuDao {
    Integer addDanmu(Danmu danmu);
    List<Danmu> getDanmus(Map<String, Object> params);
}
