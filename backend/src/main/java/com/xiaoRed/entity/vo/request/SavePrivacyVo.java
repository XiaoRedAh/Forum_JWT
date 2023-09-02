package com.xiaoRed.entity.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 前端修改隐私设置传来的修改种类 + 是否开启封装为一个vo【一次只改一种隐私】
 */
@Data
public class SavePrivacyVo {
    @Pattern(regexp = "email|gender|qq|wx|phone")
    String type;
    boolean status;
}
