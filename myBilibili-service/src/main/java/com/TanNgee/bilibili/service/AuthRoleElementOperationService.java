package com.TanNgee.bilibili.service;

import com.TanNgee.bilibili.dao.AuthRoleElementOperationDao;
import com.TanNgee.bilibili.domain.auth.AuthRoleElementOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 角色操作权限
 * @Author TanNgee
 * @Date 2022/6/29 22:15
 **/

@Service
public class AuthRoleElementOperationService {

    @Autowired
    private AuthRoleElementOperationDao authRoleElementOperationDao;

    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationDao.getRoleElementOperationsByRoleIds(roleIdSet);

    }
}
