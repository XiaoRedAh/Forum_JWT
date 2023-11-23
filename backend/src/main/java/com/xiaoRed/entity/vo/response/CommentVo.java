package com.xiaoRed.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 展示评论
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVo {
    int id;
    String content;
    Date tome;
    String quote; //展示该评论引用的评论的内容
    User user; //发表该评论的用户相关信息

    @Data
    public static class User{
        Integer id;
        String username;
        String avatar;
        Integer gender;
        String qq;
        String wx;
        String phone;
        String email;
    }
}
