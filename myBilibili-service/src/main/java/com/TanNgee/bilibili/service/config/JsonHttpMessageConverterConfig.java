package com.TanNgee.bilibili.service.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author TanNgee
 * @Date 2022/6/22 21:33
 **/

@Configuration
public class JsonHttpMessageConverterConfig {

    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        Object o = new Object();
        list.add(o);
        list.add(o);

        System.out.println(list.size());
        System.out.println(JSONObject.toJSONString(list));
        System.out.println(JSONObject.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect));   //禁止循环引用
    }

    @Bean
    @Primary  //要注入的类有比较高的优先级
    public HttpMessageConverters fastJsonHttpMessageConvertes() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteMapNullValue, SerializerFeature.MapSortField, // 对键值对排序
                SerializerFeature.DisableCircularReferenceDetect // 禁止循环引用
        );

        fastConverter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastConverter);
    }

}
