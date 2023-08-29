package com.xiaoRed.entity.vo.request;

import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * 账户信息设置提交的内容封装为一个vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailsSaveVo {
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")//不允许包含特殊字符
    @Length(min = 1, max = 10)
    private String username;
    //0表示男，1表示女
    @Max(1)
    @Min(0)
    private Integer gender;
    @Length(max = 11)
    private String phone;
    @Length(max = 13)
    private String qq;
    @Length(max = 20)
    private String wx;
    @Length(max = 200)
    private String desc;
}
