package com.xiaoRed.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (TopicComment)表实体类
 * @date 2023/11/23 16:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_topic_comment")
public class TopicComment {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer uid;
    Integer tid;
    String content;
    Date time;
    Integer quote; //默认-1，如果该评论引用了评论A，则为评论A的id
}
