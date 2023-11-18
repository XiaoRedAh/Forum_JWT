package com.xiaoRed.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * 用户相关信息粉状为vo，前端需要的时候给过去
 */
@Data
public class AccountVo {

    private String username;
    private String email;
    private String role;
    private String avatar;
    private Date registerTime;
}
