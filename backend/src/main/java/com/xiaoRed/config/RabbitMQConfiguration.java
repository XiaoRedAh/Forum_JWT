package com.xiaoRed.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * RabbitMQ消息队列配置类
 */
@Configuration
public class RabbitMQConfiguration {

    //注册一个Bean名称为emailQueue，名称为mail，采用持久化存储的消息队列
    @Bean("emailQueue")
    public Queue emailQueue(){
        return QueueBuilder
                .durable("mail")
                .build();
    }
}
