package com.TanNgee.bilibili.service.config;

import com.TanNgee.bilibili.domain.UserFollowing;
import com.TanNgee.bilibili.domain.UserMoment;
import com.TanNgee.bilibili.domain.constant.UserMomentsConstant;
import com.TanNgee.bilibili.service.UserFollowingService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * RocketMQ 配置
 *
 * @Author TanNgee
 * @Date 2022/6/28 22:02
 **/

@Configuration
public class RocketMQConfig {


    @Value("${rocketmq.name.server.address}")   //使用SpringBoot注解引入
    private String nameServerAddr;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Autowired
    private UserFollowingService userFollowingService;

    /**
     * 生产者实例创建
     *
     * @return
     * @throws Exception
     */
    @Bean("momentsProducer")  // 用户动态相关的生产者
    public DefaultMQProducer momentsProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);   //新建一个字符串分组

        producer.setNamesrvAddr(nameServerAddr);  //名称服务器地址

        producer.start();
        return producer;
    }

    /**
     * 实现对动态的监听(粉丝获取关注者的动态)
     *
     * @return
     * @throws Exception
     */
    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);

        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS, "*");  //要订阅的内容

        consumer.registerMessageListener(new MessageListenerConcurrently() {  //注册消息监听器
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                MessageExt msg = msgs.get(0);
                if (msg == null) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }

                String bodyStr = new String(msg.getBody());
                UserMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), UserMoment.class);// 对动态进行反序列化

                Long userId = userMoment.getUserId();

                List<UserFollowing> fanList = userFollowingService.getUserFans(userId);//获取粉丝列表

                for (UserFollowing fan : fanList) {
                    String key = "subscribed-" + fan.getUserId();   //具体哪个用户订阅的动态列表
                    String subscribedListStr = redisTemplate.opsForValue().get(key);

                    List<UserMoment> subscribedList;   //用户动态

                    if (StringUtil.isNullOrEmpty(subscribedListStr)) {
                        subscribedList = new ArrayList<>();
                    } else {
                        subscribedList = JSONArray.parseArray(subscribedListStr, UserMoment.class);
                    }

                    subscribedList.add(userMoment);
                    redisTemplate.opsForValue().set(key, JSONObject.toJSONString(subscribedList));   // 更新用户的动态列表
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }
}
