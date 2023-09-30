package com.xiaoRed.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaoRed.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (Account)表实体类
 *
 * @author makejava
 * @since 2023-08-09 10:12:41
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_account")
public class Account implements BaseData {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;
    
    private String password;
    
    private String email;
    
    private String role;

    private String avatar;
    
    private Date registerTime;
}

