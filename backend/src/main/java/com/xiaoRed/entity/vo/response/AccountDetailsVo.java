package com.xiaoRed.entity.vo.response;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 数据库查询到用户详细信息，封装为vo对象返回给前端
 */
@Data
public class AccountDetailsVo {
    private Integer gender;
    private String phone;
    private String qq;
    private String wx;
    private String desc;
}
