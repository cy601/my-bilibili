package com.TanNgee.bilibili.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 数据权限控制注解
 *
 * @Author TanNgee
 * @Date 2022/6/30 22:00
 **/

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD}) //注解作用在方法上
@Documented
@Component
public @interface DataLimited {
}
