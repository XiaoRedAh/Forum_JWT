package com.xiaoRed.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 重置密码第一步：填写账号绑定的电子邮箱，接收验证码，并填写
 * 将前端请求携带的的邮箱，验证码封装为vo
 */
@Data
@AllArgsConstructor
public class ConfirmResetVo {
    @Email
    String email;
    @Length(min = 6, max = 6)
    String code;

}
