package com.TanNgee.bilibili.service;

import com.TanNgee.bilibili.dao.UserMomentsDao;
import com.TanNgee.bilibili.domain.UserMoment;
import com.TanNgee.bilibili.domain.constant.UserMomentsConstant;
import com.TanNgee.bilibili.service.util.RocketMQUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationContext;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * @Author TanNgee
 * @Date 2022/6/28 22:30
 **/

@Service
public class UserMomentsService {


    @Autowired
    private UserMomentsDao userMomentsDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 新建用户动态
     *
     * @param userMoment
     */
    public void addUserMoments(UserMoment userMoment) throws Exception {
        userMoment.setCreateTime(new Date());
        userMomentsDao.addUserMoments(userMoment); //添加到数据库中

        // 通过队列通知粉丝
        DefaultMQProducer producer = (DefaultMQProducer) applicationContext.getBean("momentsProducer");   //获取之前注册的队列生产者

        Message msg = new Message(UserMomentsConstant.TOPIC_MOMENTS, JSONObject.toJSONString(userMoment).getBytes(StandardCharsets.UTF_8));//消息体

        RocketMQUtil.syncSendMsg(producer, msg);  //异步发送
    }

    /**
     * 获取当前用户的所有动态,从Redis中取
     *
     * @param userId
     * @return
     */
    public List<UserMoment> getUserSubscribedMoments(Long userId) {
        String key = "subscribed-" + userId;
        String listStr = redisTemplate.opsForValue().get(key);
        return JSONArray.parseArray(listStr, UserMoment.class);
    }
}
