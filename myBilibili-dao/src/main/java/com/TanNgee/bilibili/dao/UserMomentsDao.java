package com.TanNgee.bilibili.dao;

import com.TanNgee.bilibili.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author TanNgee
 * @Date 2022/6/28 22:30
 **/
@Mapper
public interface UserMomentsDao {

    Integer addUserMoments(UserMoment userMoment);

}
