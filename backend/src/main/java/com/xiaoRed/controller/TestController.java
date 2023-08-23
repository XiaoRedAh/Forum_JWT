package com.xiaoRed.controller;

import com.xiaoRed.entity.RestBean;
import jakarta.annotation.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Resource
    JavaMailSender sender;

    @GetMapping("/hello")
    public String test(){
        return "hello";
    }

    @PostMapping("/send-mail")
    public RestBean<String> sendMail(@RequestParam String email){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("【xxx平台】请接收您的用户注册验证码码");
        //生成6位验证码
        Random random =new Random();
        int code = random.nextInt(899999) + 100000;//这样保证生成的code一定是6位数
        mailMessage.setText("尊敬的用户：" + "\n" + "我们收到了您提交的绑定邮箱申请，请凭验证码：" + code + "，完成邮箱注册(5分钟内有效，请勿泄露该验证码)");
        mailMessage.setTo(email);
        mailMessage.setFrom("m19925651773@163.com");
        try{
            sender.send(mailMessage);
            return RestBean.success();
        }catch (MailException e){
            return RestBean.failure(400, e.getMessage());
        }
    }
}
