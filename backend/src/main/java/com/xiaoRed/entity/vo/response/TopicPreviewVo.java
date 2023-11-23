package com.xiaoRed.entity.vo.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 帖子列表中，每一个展示的帖子需要的属性封装为一个vo
 */
@Data
public class TopicPreviewVo {
    int id;
    int type;
    String title;
    String text;
    List<String> images;
    Date time;
    Integer uid;
    String username;
    String avatar;
    int like; //展示点赞量
    int collect; //展示收藏量
}
