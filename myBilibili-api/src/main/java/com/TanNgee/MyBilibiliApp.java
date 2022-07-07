package com.TanNgee;

import com.TanNgee.bilibili.service.websocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author TanNgee
 * @Date 2022/6/20 23:06
 **/

@SpringBootApplication
@EnableTransactionManagement //s事务
@EnableAsync //异步执行
@EnableScheduling //定时任务
public class MyBilibiliApp {
    public static void main(String[] args) {
        // 启动入口
        ApplicationContext app = SpringApplication.run(MyBilibiliApp.class, args);
        WebSocketService.setApplicationContext(app);   //设置全局上下文

    }
}
