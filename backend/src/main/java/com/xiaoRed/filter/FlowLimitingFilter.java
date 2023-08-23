package com.xiaoRed.filter;

import com.xiaoRed.constants.Const;
import com.xiaoRed.entity.RestBean;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 限流控制过滤器
 * 防止用户高频访问接口，借用redis限流
 */
@Slf4j
@Component
@Order(Const.ORDER_FLOW_LIMIT)
public class FlowLimitingFilter extends HttpFilter {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ip = request.getRemoteAddr();
        if(!requestCount(ip)){//如果没有通过限流检查，则就拒绝请求，响应错误提示
            this.writeBlockMessage(response);
        }
        //如果通过限流检查，直接放行给下一个过滤器
        else chain.doFilter(request, response);
    }

    /**
     * 尝试对指定IP地址请求计数，如果被限制则无法继续访问
     * @param ip 请求的ip地址
     * @return 返回true表示请求频率正常，无需封禁；返回false表示请求频繁，对ip封禁
     */
    private boolean requestCount(String ip) {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.FLOW_LIMIT_BLOCK + ip)))
            return false;
        else return limitPeriodCheck(ip);
    }

    /**
     * 针对于在时间段内多次请求限制，如3秒内限制请求20次，超出频率则封禁一段时间
     * @param ip 请求的ip地址
     * @return 返回true表示请求频率正常，无需封禁；返回false表示请求频繁，对ip封禁
     */
    private boolean limitPeriodCheck(String ip) {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.FLOW_LIMIT_COUNTER + ip))){//如果该ip已在redis中
            //redis中对应ip的请求计数自增1，然后拿出来（有可能这一瞬间该键值对刚好失效，因此要有判断null的情况，为null返回0）
            long count = Optional.ofNullable(stringRedisTemplate.opsForValue().increment(Const.FLOW_LIMIT_COUNTER + ip)).orElse(0L);
            if (count >20){//如果请求计数超过20次，则将ip拉入封禁名单，封禁时间30秒
                stringRedisTemplate.opsForValue().set(Const.FLOW_LIMIT_BLOCK + ip, "", 30, TimeUnit.SECONDS);
                return false;
            }
        }else{//如果该ip没有在redis中，说明这三秒内该ip第一次发起请求，因此存入redis计数，初值为1
            stringRedisTemplate.opsForValue().set(Const.FLOW_LIMIT_COUNTER + ip, "1", 3, TimeUnit.SECONDS);
        }
        return true;
    }

    /**
     * 为响应编写拦截内容，提示用户操作频繁
     * @param response
     * @throws IOException
     */
    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset = utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(RestBean.forbidden("操作频繁，请稍后再试").asJsonString());
    }
}
