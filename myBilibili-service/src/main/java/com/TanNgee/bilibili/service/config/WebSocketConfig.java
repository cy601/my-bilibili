package com.TanNgee.bilibili.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类
 *
 * @Author TanNgee
 * @Date 2022/7/3 16:21
 **/
@Configuration
public class WebSocketConfig {
    @Bean
    /**
     * 用于发现websocket服务
     */
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
