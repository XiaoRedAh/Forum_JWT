package com.xiaoRed.config;

import com.xiaoRed.entity.vo.response.WeatherVo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfiguration {
    //创建一个BCryptPasswordEncoder注入容器
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //这个RestTemplate可以方便地发送Rest请求
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
