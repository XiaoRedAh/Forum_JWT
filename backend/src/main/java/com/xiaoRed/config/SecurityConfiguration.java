package com.xiaoRed.config;

import com.xiaoRed.entity.RestBean;
import com.xiaoRed.entity.dto.Account;
import com.xiaoRed.entity.vo.response.AuthorizeVo;
import com.xiaoRed.filter.JwtAuthorizeFilter;
import com.xiaoRed.service.AccountService;
import com.xiaoRed.utils.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * SpringSecurity相关配置
 */
@Configuration
public class SecurityConfiguration {
    @Resource
    JwtUtil jwtUtil;

    @Resource
    AccountService accountService;

    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       return http
               .authorizeHttpRequests(conf ->conf
                       .requestMatchers("/api/auth/**").permitAll()
                       .requestMatchers("/images/**").permitAll()
                       .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                       .anyRequest().authenticated())
               .formLogin(conf -> conf
                       .loginProcessingUrl("/api/auth/login")
                       .successHandler(this::onAuthenticationSuccess)
                       .failureHandler(this::onAuthenticationFailure))
               .logout(conf -> conf
                       .logoutUrl("/api/auth/logout")
                       .logoutSuccessHandler(this::onLogoutSuccess))
               //配置异常处理
               .exceptionHandling(conf -> conf
                       //JWT校验不通过时触发
                       .authenticationEntryPoint(this::onUnAuthorized)
                       //JWT校验通过，但访问某资源没有对应权限时触发
                       .accessDeniedHandler(this::onAccessDeny) )
               .csrf(AbstractHttpConfigurer::disable)
               //采用JWT方案，不用session了
               .sessionManagement(conf -> conf
                       .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               //把JWT校验过滤器添加在UsernamePasswordAuthenticationFilter之前
               .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
               .build();
    }

    //登录成功处理器
    private void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        //从这里拿到loadUserByUsername方法返回的用户信息
        User details = (User) authentication.getPrincipal();
        /*在实现的loadUserByUsername方法中，放在User的username里的可能是Account的username，也可能是email
        因此要想得到Account的username，不能用details.getUsername()，这里有两种方案：
        方案一： 再用findAccountByNameOrEmail方法查一次用户，通过它来获得username【这样的话，登录就要查两次数据库，而且是一样的结果】
        方案二： 采用ThreadLocal
        这里采用方案一
         */
        Account account = accountService.findAccountByNameOrEmail(details.getUsername());
        String token = jwtUtil.createJWT(details, account.getId(), account.getUsername());
        //使用对象转换工具的方法快速将Account对象转换为AuthorizeVo对象
        AuthorizeVo authorizeVo = account.asViewObject(AuthorizeVo.class, v -> {
            v.setExpire(jwtUtil.generateExpirationDate());
            v.setToken(token);
        });
        response.getWriter().write(RestBean.success(authorizeVo).asJsonString());
    }

    //登录失败处理器
    private void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                         AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.failure(401, exception.getMessage()).asJsonString());
    }

    //退出登录成功处理器
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        String authorization = request.getHeader("Authorization");
        //如果将token拉入黑名单成功，则退出登录成功
        if(jwtUtil.inValidateJwt(authorization))response.getWriter().write(RestBean.success().asJsonString());
        else response.getWriter().write(RestBean.failure(400, "退出登录失败").asJsonString());
    }

    //JWT校验不通过时触发
    public void onUnAuthorized(HttpServletRequest request, HttpServletResponse response,
                               AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.unauthorized(authException.getMessage()).asJsonString());
    }

    //JWT校验通过，但访问某资源没有对应权限时触发
    private void onAccessDeny(HttpServletRequest request, HttpServletResponse response,
                              AccessDeniedException e) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.forbidden(e.getMessage()).asJsonString());
    }
}
