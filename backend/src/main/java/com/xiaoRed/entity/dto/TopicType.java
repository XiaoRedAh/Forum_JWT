package com.xiaoRed.entity.dto;


import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xiaoRed.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (TopicType)表实体类
 *
 * @author makejava
 * @since 2023-10-15 19:28:21
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_topic_type")
public class TopicType implements BaseData {
    @TableId
    private Integer id;

    private String name;
    @TableField(value = "`desc`")
    private String desc;
    
    private String color;
}

