package com.TanNgee.bilibili.dao;

import com.TanNgee.bilibili.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author TanNgee
 * @Date 2022/6/29 22:15
 **/

@Mapper
public interface AuthRoleDao {
    AuthRole getRoleByCode(String code);
}
