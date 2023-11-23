package com.xiaoRed.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 消息通知响应给前端
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationVo {
    private Integer id;
    private String title;
    private String content;
    private String type;
    private String url;
    private Date time;
}
