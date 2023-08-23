package com.xiaoRed.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.xiaoRed.constants.Const;
import com.xiaoRed.utils.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

    @Resource
    JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");//拿到Authorization请求头中的内容
        DecodedJWT jwt = jwtUtil.resolveJwt(authorization);
        if(jwt != null){//解析出来的jwt不为空，说明验证通过了
            //把令牌中的用户信息拿出来
            UserDetails user = jwtUtil.toUser(jwt);
            //用户相关信息封装为Authentication
            //使用UsernamePasswordAuthenticationToken作为实体
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            //把配置好的Authentication塞给SecurityContext表示已经完成验证
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        //jwt验证没通过，直接放行（因为后面还有一系列过滤器对其进行认证分析）
        filterChain.doFilter(request, response);
    }
}
