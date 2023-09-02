package com.xiaoRed.entity.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 修改密码功能：前端传来的原密码，新密码封装为vo
 */
@Data
public class ChangePawVo {
    @Length(min = 5, max = 20)
    String password_new;
    @Length(min = 5, max = 20)
    String password_old;
}
