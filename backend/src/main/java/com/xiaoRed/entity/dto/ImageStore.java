package com.xiaoRed.entity.dto;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (ImageStore)表实体类
 *
 * @author makejava
 * @since 2023-10-14 10:54:06
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_image_store")
public class ImageStore  {

    
    private Integer uid;
    
    private String name;
    
    private Date uploadTime;
}

