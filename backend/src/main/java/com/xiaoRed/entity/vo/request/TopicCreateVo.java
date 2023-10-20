package com.xiaoRed.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 前端发送来的发表帖子参数包装为一个TopicCreateVo对象
 */
@Data
public class TopicCreateVo {
    @Min(1)
    @Max(5)
    int type; //到service会再进行次校验
    @Length(min = 1, max = 20)
    String title;
    //前端富文本采用delta模式，文本内容是JSON格式的，真正的文本存储在ops这个JSON数组中的insert字段里
    JSONObject content;
}
