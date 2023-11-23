package com.xiaoRed.entity.dto;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.xiaoRed.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (Notification)表实体类
 *
 * @author makejava
 * @since 2023-11-23 18:13:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_notification")
public class Notification implements BaseData {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid; //给哪个用户发提醒
    private String title;
    private String content;
    private String type; //通知类型，比如success，warning
    private String url;  //点击跳转到通知的位置
    private Date time;
}

