package com.TanNgee.bilibili.service;

import com.TanNgee.bilibili.dao.UserRoleDao;
import com.TanNgee.bilibili.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author TanNgee
 * @Date 2022/6/29 22:00
 **/
@Service
public class UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;
    /**
     * 获取用户关联的角色
     *
     * @param userId
     * @return
     */
    public List<UserRole> getUserRoleByUserId(Long userId) {
        return userRoleDao.getUserRoleByUserId(userId);

    }

    public void addUserRole(UserRole userRole) {
        userRole.setCreateTime(new Date());
        userRoleDao.addUserRole(userRole);
    }
}
