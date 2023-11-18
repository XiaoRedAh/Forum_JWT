package com.xiaoRed.entity.dto;


import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.xiaoRed.entity.BaseData;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (AccountPrivacy)表实体类
 *
 * @author makejava
 * @since 2023-09-02 11:56:44
 */
@Data
@TableName("db_account_privacy")
public class AccountPrivacy implements BaseData {
    @TableId(type = IdType.AUTO)
    final Integer id; //设置为final，表示构造时一定要有
    //0不开启，1开启。默认所有隐私都开启
    private boolean email = true;
    private boolean gender = true;
    private boolean phone = true;
    private boolean qq = true;
    private boolean wx = true;

    //返回需要隐藏的字段名，也就是隐私设置为false，他人不可见的属性
    public String[] hiddenFields() {
        List<String> strings = new LinkedList<>();
        Field[] fields = this.getClass().getDeclaredFields(); //用反射拿属性，更加灵活，没有写死，方便后续扩展
        for (Field field : fields) {
            try {
                if (field.getType().equals(boolean.class) && !field.getBoolean(this))
                    strings.add(field.getName());
            } catch (Exception ignored) {
            }
        }
        return strings.toArray(String[]::new);
    }
}

