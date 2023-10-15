package com.xiaoRed.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class TopicCreateVo {
    @Min(1)
    @Max(5)
    int type; //到service会再进行次校验
    @Length(min = 1, max = 20)
    String title;
    JSONObject content; //JSONObject没办法校验，到service层再校验
}
