package com.TanNgee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @Author TanNgee
 * @Date 2022/6/20 23:06
 **/

@SpringBootApplication
public class MyBilibiliApp {
    public static void main(String[] args) {
        // 启动入口
        ApplicationContext app = SpringApplication.run(MyBilibiliApp.class, args);
    }
}
