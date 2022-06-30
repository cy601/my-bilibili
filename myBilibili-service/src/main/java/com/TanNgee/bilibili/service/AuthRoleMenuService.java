package com.TanNgee.bilibili.service;

import com.TanNgee.bilibili.dao.AuthRoleMenuDao;
import com.TanNgee.bilibili.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 查询页面菜单关联权限
 *
 * @Author TanNgee
 * @Date 2022/6/29 22:16
 **/
@Service
public class AuthRoleMenuService {

    @Autowired
    private AuthRoleMenuDao authRoleMenuDao;

    public List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuDao.getAuthRoleMenusByRoleIds(roleIdSet);
    }
}
