package com.xiaoRed.entity.vo.response;

import lombok.Data;

/**
 * 响应给前端的隐私设置信息
 */
@Data
public class AccountPrivacyVo {
    private boolean email;
    private boolean gender;
    private boolean phone;
    private boolean qq;
    private boolean wx;
}
