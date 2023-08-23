package com.xiaoRed.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 限流通用工具
 * 针对于不同的情况进行限流操作，支持限流升级
 */
@Component
public class FlowUtil {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 针对于单次频率限制，请求成功后，在冷却时间内不得再次进行请求，如3秒内不能再次发起请求
     * @param key 位于redis冷却名单中的key ，即verify:email:limit:
     * @param blockTime 冷却时间
     * @return 是否通过限流检查，false表示已经被限流，true表示尚未被限流
     */
    public boolean limitOnceCheck(String key, int blockTime){
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            return false;
        }else{
            //尚未被限流的，这次请求后就要被加入到限流名单中
            stringRedisTemplate.opsForValue().set(key, "", blockTime, TimeUnit.SECONDS);
            return true;
        }
    }
}
