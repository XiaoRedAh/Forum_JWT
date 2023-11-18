package com.xiaoRed.controller;

import com.xiaoRed.constants.Const;
import com.xiaoRed.entity.RestBean;
import com.xiaoRed.entity.dto.Account;
import com.xiaoRed.entity.dto.AccountDetails;
import com.xiaoRed.entity.dto.AccountPrivacy;
import com.xiaoRed.entity.vo.response.AccountVo;
import com.xiaoRed.entity.vo.request.ChangePawVo;
import com.xiaoRed.entity.vo.request.DetailsSaveVo;
import com.xiaoRed.entity.vo.request.ModifyEmailVo;
import com.xiaoRed.entity.vo.request.SavePrivacyVo;
import com.xiaoRed.entity.vo.response.AccountDetailsVo;
import com.xiaoRed.entity.vo.response.AccountPrivacyVo;
import com.xiaoRed.service.AccountDetailsService;
import com.xiaoRed.service.AccountPrivacyService;
import com.xiaoRed.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 用于验证成功后，负责用户相关操作的controller
 */
@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Resource
    AccountService accountService;

    @Resource
    AccountDetailsService accountDetailsService;

    @Resource
    AccountPrivacyService privacyService;

    /**
     * 获取用户相关信息，将这些信息封装为一个vo返回给前端
     * @param id JwtAuthorizeFilter已经往请求域中塞进了用户id，直接从请求域里取出来就行
     */
    @GetMapping("/info")
    public RestBean<AccountVo> info(@RequestAttribute(Const.ATTR_USER_ID) int id){
        Account account = accountService.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVo.class)); //因为已经登录进去了，肯定查得到这个用户，所以不用判断，直接返回成功
    }

    /**
     * 获取用户详细信息，封装为vo对象，展示到前端
     * @param id JwtAuthorizeFilter已经往请求域中塞进了用户id，直接从请求域里取出来就行
     */
    @GetMapping("/details")
    public RestBean<AccountDetailsVo> details(@RequestAttribute(Const.ATTR_USER_ID) int id){
        //如果之前没有保存过账户详细信息，数据表中是没有对应id的数据的，这时new一个空的即可
        AccountDetails accountDetails = Optional.
                ofNullable(accountDetailsService.findAccountDetailsById(id))
                .orElseGet(AccountDetails::new);
        return RestBean.success(accountDetails.asViewObject(AccountDetailsVo.class));
    }

    /**
     * 保存账户详细信息
     * @param id JwtAuthorizeFilter已经往请求域中塞进了用户id，直接从请求域里取出来就行
     * @param vo 前端保存账户详细信息表单提交的内容封装为一个vo
     */
    @PostMapping("/save-details")
    public RestBean<Void> saveDetails(@RequestAttribute(Const.ATTR_USER_ID) int id, @RequestBody @Valid DetailsSaveVo vo){
        boolean isSuccess = accountDetailsService.saveAccountDetails(id, vo);
        return  isSuccess ? RestBean.success() : RestBean.failure(400, "此用户名已被其他用户使用，请重新设置");
    }

    /**
     * 修改账号绑定的电子邮箱
     * @param id 账号对应的id
     * @param vo 前端传过来的新电子邮箱地址和验证码封装为vo
     */
    @PostMapping("/modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute(Const.ATTR_USER_ID) int id, @RequestBody @Valid ModifyEmailVo vo){
        String message = accountService.modifyEmail(id, vo);
        return message==null ? RestBean.success() : RestBean.failure(400, message);
    }

    /**
     * 修改密码功能【与忘记密码的重置密码区分】
     * @param id 账号id
     * @param vo 前端传来的原密码，新密码封装为vo
     */
    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestAttribute(Const.ATTR_USER_ID) int id, @RequestBody @Valid ChangePawVo vo){
        String message = accountService.changePassword(id, vo);
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }

    /**
     * 返回账号的隐私设置，展示在前端
     * @param id 用户id
     */
    @GetMapping("/privacy")
    public RestBean<AccountPrivacyVo> privacy(@RequestAttribute(Const.ATTR_USER_ID) int id){
        AccountPrivacy accountPrivacy = privacyService.getAccountPrivacy(id);
        return RestBean.success(accountPrivacy.asViewObject(AccountPrivacyVo.class));
    }

    /**
     * 保存隐私设置
     */
    @PostMapping("/save-privacy")
    public RestBean<Void> savePrivacy(@RequestAttribute(Const.ATTR_USER_ID) int id, @RequestBody @Valid SavePrivacyVo vo){
        privacyService.savePrivacy(id, vo);
        return RestBean.success();
    }
}
