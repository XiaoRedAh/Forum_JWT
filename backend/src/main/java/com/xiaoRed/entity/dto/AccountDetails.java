package com.xiaoRed.entity.dto;


import java.io.Serializable;

import com.xiaoRed.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (AccountDetails)表实体类
 *
 * @author makejava
 * @since 2023-08-29 15:52:08
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_account_details")
public class AccountDetails implements BaseData {
    @TableId
    private Integer id;

    private Integer gender;
    
    private String phone;
    
    private String qq;
    
    private String wx;
    
    private String desc;
}

