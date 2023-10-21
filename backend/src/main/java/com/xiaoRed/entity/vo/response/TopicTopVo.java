package com.xiaoRed.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * 将置顶帖子需要的三个属性封装为vo返回给前端
 */
@Data
public class TopicTopVo {
    int id;
    String title;
    Date time;
}
