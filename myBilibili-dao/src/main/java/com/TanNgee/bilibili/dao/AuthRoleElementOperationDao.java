package com.TanNgee.bilibili.dao;

import com.TanNgee.bilibili.domain.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author TanNgee
 * @Date 2022/6/29 22:17
 **/

@Mapper
public interface AuthRoleElementOperationDao {//指定名称
    List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(@Param("roleIdSet") Set<Long> roleIdSet);

}
