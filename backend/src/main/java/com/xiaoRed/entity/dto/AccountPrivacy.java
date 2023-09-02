package com.xiaoRed.entity.dto;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (AccountPrivacy)表实体类
 *
 * @author makejava
 * @since 2023-09-02 11:56:44
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_account_privacy")
public class AccountPrivacy  {
    //0不开启，1开启。默认所有隐私都开启
    @TableId
    private Integer id;
    private Integer email = 1;
    private Integer gender = 1;
    private Integer phone = 1;
    private Integer qq = 1;
    private Integer wx = 1;
}

