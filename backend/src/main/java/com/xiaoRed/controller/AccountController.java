package com.xiaoRed.controller;

import com.xiaoRed.constants.Const;
import com.xiaoRed.entity.RestBean;
import com.xiaoRed.entity.dto.Account;
import com.xiaoRed.entity.vo.AccountVo;
import com.xiaoRed.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 用于验证成功后，负责用户相关操作的controller
 */
@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Resource
    AccountService accountService;

    /**
     * 获取用户相关信息，将这些信息封装为一个vo返回给前端
     * @param id JwtAuthorizeFilter已经往请求域中塞进了用户id，直接从请求域里取出来就行
     * @return
     */
    @GetMapping("/info")
    public RestBean<AccountVo> info(@RequestAttribute(Const.ATTR_USER_ID) int id){
        Account account = accountService.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVo.class)); //因为已经登录进去了，肯定查得到这个用户，所以不用判断，直接返回成功
    }
}
