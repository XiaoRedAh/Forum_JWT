package com.xiaoRed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoRed.entity.dto.Notification;
import org.apache.ibatis.annotations.Mapper;


/**
 * (Notification)表数据库访问层
 *
 * @author makejava
 * @since 2023-11-23 18:13:08
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

}
