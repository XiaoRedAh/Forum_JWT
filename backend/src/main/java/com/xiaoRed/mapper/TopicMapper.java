package com.xiaoRed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoRed.entity.dto.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * (Topic)表数据库访问层
 *
 * @author makejava
 * @since 2023-10-15 21:38:18
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

    /**
     * 从所有帖子的第start个开始，获取10个帖子
     * @param start 从查询结果的第start个开始，一共查10条数据【实际上就是1页展示10个帖子】
     */
    @Select("select * from db_topic left join db_account on uid = db_account.id " +
            "order by `time` desc limit ${start}, 10")
    List<Topic> topicList(int start);

    /**
     * 在type类型的帖子中，从第start条数据开始，获取10个帖子
     * @param start 从查询结果的第start条数据开始，一共查10条数据【实际上就是1页展示10个帖子】
     * @param type 帖子类型
     */
    @Select("select * from db_topic left join db_account on uid = db_account.id " +
            "where type = #{type} " +
            "order by `time` desc limit ${start}, 10")
    List<Topic> topicListByType(int start, int type);
}
