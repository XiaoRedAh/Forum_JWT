package com.xiaoRed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoRed.entity.dto.TopicComment;
import org.apache.ibatis.annotations.Mapper;


/**
 * (TopicComment)表数据库访问层
 *
 * @author makejava
 * @since 2023-11-23 16:40:21
 */
@Mapper
public interface TopicCommentMapper extends BaseMapper<TopicComment> {

}
