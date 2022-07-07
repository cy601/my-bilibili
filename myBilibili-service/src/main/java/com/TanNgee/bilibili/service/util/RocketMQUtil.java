package com.TanNgee.bilibili.service.util;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * RocketMQ工具类
 *
 * @Author TanNgee
 * @Date 2022/6/28 22:19
 **/
public class RocketMQUtil {
    /**
     * 同步发送消息
     *
     * @param producer
     * @param msg
     * @throws Exception
     */
    public static void syncSendMsg(DefaultMQProducer producer, Message msg) throws Exception {
        SendResult result = producer.send(msg);
        System.out.println(result);
    }

    /**
     * 异步发送消息
     *
     * @param producer
     * @param msg
     * @throws Exception
     */
    public static void asyncSendMsg(DefaultMQProducer producer, Message msg) throws Exception {

        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                Logger logger = LoggerFactory.getLogger(RocketMQUtil.class);
                logger.info("异步发送消息成功，消息id：" + sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable e) {
                e.printStackTrace();
            }
        });
    }
//
//        producer.send(msg, new SendCallback() {
//            @Override
//            public void onSuccess(SendResult sendResult) {
//                Logger logger = LoggerFactory.getLogger(RocketMQUtil.class);
//                logger.info("异步发送消息成功，消息id：" + sendResult.getMsgId());
//            }
//
//            @Override
//            public void onException(Throwable e) {
//                e.printStackTrace();
//            }
//        });

}
