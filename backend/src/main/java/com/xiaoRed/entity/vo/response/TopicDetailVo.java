package com.xiaoRed.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * 展示帖子详情
 */
@Data
public class TopicDetailVo {

    private Integer id;
    private String title;
    private String content;
    private Integer type;
    private Date time;
    User user;

    @Data
    public static class User{
        Integer id;
        String username;
        String avatar;
        String desc;
        Integer gender;
        String qq;
        String wx;
        String phone;
        String email;
    }
}
