package com.xiaoRed.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 重置密码第二步：提交重置的密码
 * 将前端请求携带的新密码以及上一步用到的邮箱，验证码封装为vo
 */
@Data
public class ResetPawVo {
    @Email
    String email;
    @Length(min = 6, max = 6)
    String code;
    @Length(min = 5, max = 20)
    String password;
}
