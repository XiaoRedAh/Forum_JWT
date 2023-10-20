package com.xiaoRed.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类：封装常用的缓存操作
 */
@Component
public class CacheUtil {
    @Resource
    StringRedisTemplate template;

    //从缓存中取key对应的value，这个value是单个值
    public <T> T takeFromCache(String key, Class<T> dataType){
        String s = template.opsForValue().get(key);
        if(s == null) return null;
        return JSONObject.parseObject(s).to(dataType);
    }

    //从缓存中取key对应的value，这个value是个列表
    public <T> List<T> takeListFromCache(String key, Class<T> itemType){
        String s = template.opsForValue().get(key);
        if(s == null) return null;
        return JSONArray.parseArray(s).toList(itemType);
    }

    //向缓存中存入一个键值对，其中value是单个值
    public <T> void saveToCache(String key, T data, long expire){
        template.opsForValue().set(key, JSONObject.from(data).toJSONString(), expire, TimeUnit.SECONDS);
    }

    //向缓存中存入一个键值对，其中value是一个列表
    public <T> void saveListToCache(String key, List<T> list, long expire){
        template.opsForValue().set(key, JSONArray.from(list).toJSONString(), expire, TimeUnit.SECONDS);
    }

    //手动使缓存失效
    public void deleteCache(String key){
        template.delete(key);
    }
}
