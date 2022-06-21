package com.TanNgee.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;

/**
 * @Author TanNgee
 * @Date 2022/6/21 21:20
 **/
@Mapper
public interface DemoDao {
    public Long query(Long id);
}
