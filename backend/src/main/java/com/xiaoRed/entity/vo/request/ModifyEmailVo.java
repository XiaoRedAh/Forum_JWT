package com.xiaoRed.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 修改账号绑定的电子邮箱功能：前端传过来的新电子邮箱以及验证码封装为vo
 */
@Data
public class ModifyEmailVo {
    @Email
    String email;
    @Length(max = 6, min = 6)
    String code;
}
