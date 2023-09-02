package com.xiaoRed.entity.dto;


import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.xiaoRed.entity.BaseData;
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
@TableName("db_account_privacy")
public class AccountPrivacy implements BaseData {
    @TableId(type = IdType.AUTO)
    final Integer id; //设置为final，表示构造时一定要有
    //0不开启，1开启。默认所有隐私都开启
    private boolean email = true;
    private boolean gender = true;
    private boolean phone = true;
    private boolean qq = true;
    private boolean wx = true;
}

