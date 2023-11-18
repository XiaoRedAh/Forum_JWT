package com.xiaoRed.entity.vo.response;

import lombok.Data;

/**
 * 向前端返回帖子类型的一切信息
 */
@Data
public class TopicTypeVo {
    private Integer id;

    private String name;

    private String desc;

    private String color;
}
