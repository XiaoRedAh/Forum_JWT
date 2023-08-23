package com.xiaoRed.controller;

import com.xiaoRed.entity.RestBean;
import com.xiaoRed.entity.vo.request.ConfirmResetVo;
import com.xiaoRed.entity.vo.request.EmailRegisterVo;
import com.xiaoRed.entity.vo.request.ResetPawVo;
import com.xiaoRed.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用于验证相关的Controller，包含用户的注册、重置密码等操作
 */
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthorizeController {

    @Resource
    AccountService accountService;

    /**
     * 请求邮件验证码
     * @param type 类型， 必须是register或reset中的一个
     * @param email 请求邮件，必须是合法的邮箱地址才能通过校验
     * @param request 请求，用来获得请求的ip地址
     * @return 是否请求成功
     */
    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Pattern(regexp = "(register|reset)") String type, @RequestParam @Email String email,
                                        HttpServletRequest request){
        String message = accountService.sendEmailVerifyCode(type, email, request.getRemoteAddr());
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }

    /**
     * 注册功能接口
     * @param vo 前端用axios框架，默认发送json，因此用@RequestBody接收json数据，转换为vo实体类
     * @return
     */
    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Validated EmailRegisterVo vo){
        String message = accountService.registerEmailAccount(vo);
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }

    /**
     * 重置密码第一步：校验验证码，验证码通过才能进行第二步的重置密码
     * @param vo 将前端请求携带的的邮箱，验证码封装为vo
     * @return
     */
    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody @Validated ConfirmResetVo vo){
        String message = accountService.resetCodeConfirm(vo);
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }

    /**
     * 重置密码第二步：提交重置的密码，更新数据库。
     * @param vo 将前端请求携带的新密码以及上一步用到的邮箱，验证码封装为vo
     * @return
     */
    @PostMapping("/reset-password")
    public RestBean<Void> resetConfirm(@RequestBody @Validated ResetPawVo vo){
        String message = accountService.resetPassword(vo);
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }
}
