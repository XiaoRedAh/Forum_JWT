package com.xiaoRed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoRed.entity.dto.Topic;
import org.apache.ibatis.annotations.Mapper;


/**
 * (Topic)表数据库访问层
 *
 * @author makejava
 * @since 2023-10-15 21:38:18
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {


}
