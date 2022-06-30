package com.TanNgee.bilibili.dao;

import com.TanNgee.bilibili.domain.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author TanNgee
 * @Date 2022/6/29 22:02
 **/
@Mapper
public interface UserRoleDao {

    List<UserRole> getUserRoleByUserId(Long userId);

    Integer addUserRole(UserRole userRole);


}
