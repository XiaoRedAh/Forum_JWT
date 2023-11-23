package com.xiaoRed.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 前端传来的添加评论的参数封装为vo
 */
@Data
public class AddCommentVo {
    @Min(1)
    int tid;
    String content;
    @Min(-1)
    int quote;
}
