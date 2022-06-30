package com.TanNgee.bilibili.service;

import com.TanNgee.bilibili.dao.AuthRoleDao;
import com.TanNgee.bilibili.domain.auth.AuthRole;
import com.TanNgee.bilibili.domain.auth.AuthRoleElementOperation;
import com.TanNgee.bilibili.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author TanNgee
 * @Date 2022/6/29 22:00
 **/
@Service
public class AuthRoleService {
    @Autowired
    private AuthRoleDao authRoleDao;

    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;

    @Autowired
    private AuthRoleMenuService authRoleMenuService;


    /**
     * 查询角色id对应的操作权限
     *
     * @param roleIdSet
     * @return
     */
    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationService.getRoleElementOperationsByRoleIds(roleIdSet);
    }

    public List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuService.getAuthRoleMenusByRoleIds(roleIdSet);
    }

    public AuthRole getRoleByCode(String code) {
        return authRoleDao.getRoleByCode(code);
    }

}
