package com.TanNgee.bilibili.service.websocket;

import com.TanNgee.bilibili.domain.Danmu;
import com.TanNgee.bilibili.domain.constant.UserMomentsConstant;
import com.TanNgee.bilibili.service.DanmuService;
import com.TanNgee.bilibili.service.util.RocketMQUtil;
import com.TanNgee.bilibili.service.util.TokenUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.rocketmq.common.message.Message;

import java.util.Date;

import javax.websocket.*;

import org.springframework.context.ApplicationContext;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket 相关所有服务
 *
 * @Author TanNgee
 * @Date 2022/7/3 16:23
 **/
@Component
@ServerEndpoint("/imserver/{token}")
public class WebSocketService {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);  //在线用户数

    public static final ConcurrentHashMap<String, WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();  //线程安全

    private Session session;  //每一个会话

    private String sessionId;

    private Long userId;   //用户id

    //全局的上下文
    private static ApplicationContext APPLICATION_CONTEXT;

    /**
     * 因为Websocket是多线程，会启动多次，Spring只会在第一次启动的时候注入Bean，所以定义一个全局上下文，所有线程使用同一个
     *
     * @param applicationContext
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketService.APPLICATION_CONTEXT = applicationContext;
    }

    /**
     * 打开连接，会自动将客户端和服务端建立连接
     *
     * @param session
     * @param token
     */
    @OnOpen  //标记为建立连接方法
    public void openConnection(Session session, @PathParam("token") String token) {
        try {
            this.userId = TokenUtil.verifyToken(token);  //解析用户id
        } catch (Exception ignored) {
        }

        this.sessionId = session.getId();
        this.session = session;

        if (WEBSOCKET_MAP.containsKey(sessionId)) {  //如果原来有，去掉，重新加
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId, this);
        } else {  //第一次连接服务端
            WEBSOCKET_MAP.put(sessionId, this);
            ONLINE_COUNT.getAndIncrement();  //增加在线人数
        }

        logger.info("用户连接成功：" + sessionId + "，当前在线人数为：" + ONLINE_COUNT.get());

        try {
            this.sendMessage("0");
        } catch (Exception e) {
            logger.error("连接异常");
        }
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void closeConnection() {
        if (WEBSOCKET_MAP.containsKey(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            ONLINE_COUNT.getAndDecrement();
        }
        logger.info("用户退出：" + sessionId + "当前在线人数为：" + ONLINE_COUNT.get());
    }

    /**
     * 前端有消息发过来了
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        logger.info("用户信息：" + sessionId + "，报文：" + message);

        if (!StringUtil.isNullOrEmpty(message)) {
            try {
                //推送给所有客户端，群发
                for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_MAP.entrySet()) {  // 获取所有会话
                    WebSocketService webSocketService = entry.getValue();
                    DefaultMQProducer danmusProducer = (DefaultMQProducer) APPLICATION_CONTEXT.getBean("danmusProducer");  //注入生产者
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("message", message);
                    jsonObject.put("sessionId", webSocketService.getSessionId());

                    Message msg = new Message(UserMomentsConstant.TOPIC_DANMUS, jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));

                    RocketMQUtil.asyncSendMsg(danmusProducer, msg);// 异步，加到队列推送给用户
                }
                if (this.userId != null) {
                    //保存弹幕到数据库
                    Danmu danmu = JSONObject.parseObject(message, Danmu.class); //序列化

                    danmu.setUserId(userId);
                    danmu.setCreateTime(new Date());

                    DanmuService danmuService = (DanmuService) APPLICATION_CONTEXT.getBean("danmuService");

                    danmuService.asyncAddDanmu(danmu);   // 异步保存到Mysql，加到队列，使用@Async注解

                    //保存弹幕到redis
                    danmuService.addDanmusToRedis(danmu);
                }
            } catch (Exception e) {
                logger.error("弹幕接收出现问题");
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Throwable error) {
    }

    /**
     * 发送消息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 获取当前在线人数，通过websocket推送到前端
     *
     * @throws IOException
     */
    //或直接指定时间间隔，例如：5秒
    @Scheduled(fixedRate = 5000)  //spring 定时任务
    private void noticeOnlineCount() throws IOException {
        for (Map.Entry<String, WebSocketService> entry : WebSocketService.WEBSOCKET_MAP.entrySet()) {
            WebSocketService webSocketService = entry.getValue();
            if (webSocketService.session.isOpen()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("onlineCount", ONLINE_COUNT.get());
                jsonObject.put("msg", "当前在线人数为" + ONLINE_COUNT.get());
                webSocketService.sendMessage(jsonObject.toJSONString());
            }
        }
    }

    public Session getSession() {
        return session;
    }

    public String getSessionId() {
        return sessionId;
    }
}
