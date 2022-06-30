package com.TanNgee.bilibili.dao;

import com.TanNgee.bilibili.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @Author TanNgee
 * @Date 2022/6/29 22:30
 **/

@Mapper
public interface AuthRoleMenuDao {
    List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIdSet);

}
