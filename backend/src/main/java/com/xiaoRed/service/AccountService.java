package com.xiaoRed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaoRed.entity.dto.Account;
import com.xiaoRed.entity.vo.request.ConfirmResetVo;
import com.xiaoRed.entity.vo.request.EmailRegisterVo;
import com.xiaoRed.entity.vo.request.ResetPawVo;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * (Account)表服务接口
 *
 * @author makejava
 * @since 2023-08-09 10:12:43
 */
public interface AccountService extends IService<Account>, UserDetailsService {
    public Account findAccountByNameOrEmail(String text);
    String sendEmailVerifyCode(String type, String email, String ip);
    String registerEmailAccount(EmailRegisterVo emailRegisterVo);
    String resetCodeConfirm(ConfirmResetVo confirmResetVo);
    String resetPassword(ResetPawVo resetPawVo);

}

