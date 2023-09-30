package com.xiaoRed.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置Minio客户端
 */
@Slf4j
@Configuration
public class MinioConfiguration {

    @Value("${spring.minio.endpoint}")
    String endpoint;
    @Value("${spring.minio.username}")
    String username;
    @Value("${spring.minio.password}")
    String password;

    @Bean
    public MinioClient minioClient(){
        log.info("Init Minio Client...");
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(username, password)
                .build();

    }
}
