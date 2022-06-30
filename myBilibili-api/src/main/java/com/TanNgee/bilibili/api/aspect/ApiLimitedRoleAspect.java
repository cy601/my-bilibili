package com.TanNgee.bilibili.api.aspect;

import com.TanNgee.bilibili.api.support.UserSupport;
import com.TanNgee.bilibili.domain.annotation.ApiLimitedRole;
import com.TanNgee.bilibili.domain.auth.UserRole;
import com.TanNgee.bilibili.domain.exception.ConditionException;
import com.TanNgee.bilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 接口权限控制
 *
 * @Author TanNgee
 * @Date 2022/6/30 21:13
 **/
@Order(1)  //优先级
@Component
@Aspect  // 这是一个切面class
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.TanNgee.bilibili.domain.annotation.ApiLimitedRole)")  //注解的位置
    public void check() {
    }

    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole) {

        Long userId = userSupport.getCurrentUserId();

        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();  //希望限制的角色列表（在具体被添加注解的方法上写明）
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());  //转变为set

        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);  //查询用户关联到的角色
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());//转变为set

        roleCodeSet.retainAll(limitedRoleCodeSet);   //两者取交集

        if (roleCodeSet.size() > 0) {  //存在交集 不能用
            throw new ConditionException("权限不足！");
        }
    }
}
