package com.xiaoRed.filter;

import com.xiaoRed.constants.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 跨域配置过滤器，仅处理跨域，添加跨域响应头
 */
@Component
//SpringSecurity过滤器的优先级-100，限流过滤器的优先级-101，跨域处理要在它们之前
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.addCorsHeader(request, response);
        chain.doFilter(request, response);
    }

    /**
     * 添加所有跨域相关响应头
     * @param request 请求
     * @param response 响应
     */
    private void addCorsHeader(HttpServletRequest request, HttpServletResponse response) {
        //设置允许哪个地址的请求可以跨域，这里是允许所有请求地址（只要向我们后端发送请求，都允许跨域）
        //也可以将第二个参数指定为唯一的那个前端地址，这样更加安全
        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        //设置哪些请求方法允许跨域
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        //设置允许携带的请求头
        response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
    }
}
