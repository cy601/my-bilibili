package com.TanNgee.bilibili.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 接口的权限控制注解
 *
 * @Author TanNgee
 * @Date 2022/6/30 21:10
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})   //注解作用在方法上
@Documented
@Component
public @interface ApiLimitedRole {
    String[] limitedRoleCodeList() default {};

}
