package com.TanNgee.bilibili.service;

import com.TanNgee.bilibili.domain.auth.*;
import com.TanNgee.bilibili.domain.constant.AuthRoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author TanNgee
 * @Date 2022/6/29 21:57
 **/
@Service
public class UserAuthService {

    @Autowired //用户角色服务
    private UserRoleService userRoleService;

    @Autowired // 角色权限服务
    private AuthRoleService authRoleService;

    public UserAuthorities getUserAuthorities(Long userId) {
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);  // 获取用户关联的角色

        Set<Long> roleIdSet = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet()); //获取角色的id

        List<AuthRoleMenu> authRoleMenuList = authRoleService.getAuthRoleMenusByRoleIds(roleIdSet);

        List<AuthRoleElementOperation> roleElementOperationList = authRoleService.getRoleElementOperationsByRoleIds(roleIdSet);  //查询操作权限

        UserAuthorities userAuthorities = new UserAuthorities();

        userAuthorities.setRoleElementOperationList(roleElementOperationList); //角色操作权限
        userAuthorities.setRoleMenuList(authRoleMenuList);   //菜单操作权限
        return userAuthorities;
    }

    public void addUserDefaultRole(Long id) {
        UserRole userRole = new UserRole();
        AuthRole role = authRoleService.getRoleByCode(AuthRoleConstant.ROLE_LV0);
        userRole.setUserId(id);
        userRole.setRoleId(role.getId());

        userRoleService.addUserRole(userRole);
    }
}
