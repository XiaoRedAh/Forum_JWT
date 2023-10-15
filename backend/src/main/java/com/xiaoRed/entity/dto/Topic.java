package com.xiaoRed.entity.dto;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (Topic)表实体类
 *
 * @author makejava
 * @since 2023-10-15 21:38:20
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_topic")
public class Topic  {
    @TableId
    private Integer id;

    
    private String title;
    
    private String content;
    
    private Integer uid;
    
    private Integer type;
    
    private Date time;
}

