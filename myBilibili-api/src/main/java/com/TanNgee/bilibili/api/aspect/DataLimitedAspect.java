package com.TanNgee.bilibili.api.aspect;

import com.TanNgee.bilibili.api.support.UserSupport;
import com.TanNgee.bilibili.domain.UserMoment;
import com.TanNgee.bilibili.domain.auth.UserRole;
import com.TanNgee.bilibili.domain.constant.AuthRoleConstant;
import com.TanNgee.bilibili.domain.exception.ConditionException;
import com.TanNgee.bilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据权限注解
 *
 * @Author TanNgee
 * @Date 2022/6/30 22:00
 **/

@Order(1)
@Component
@Aspect
public class DataLimitedAspect {
    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.TanNgee.bilibili.domain.annotation.DataLimited)")
    public void check() {
    }

    @Before("check()")
    public void doBefore(JoinPoint joinPoint) {
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());


        Object[] args = joinPoint.getArgs();   // 注解获取到的所有传入的参数

        for (Object arg : args) {
            if (arg instanceof UserMoment) {  // 传入的是UserMoment
                UserMoment userMoment = (UserMoment) arg;
                String type = userMoment.getType();
                if (roleCodeSet.contains(AuthRoleConstant.ROLE_LV0) && !"0".equals(type)) {
                    throw new ConditionException("参数异常");
                }
            }
        }
    }
}
