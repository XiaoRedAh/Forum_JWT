package com.xiaoRed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoRed.entity.dto.Interact;
import com.xiaoRed.entity.dto.Topic;
import org.apache.ibatis.annotations.Insert;
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
     * 将多条交互信息一次性入库
     * 普通的sql处理不了列表，需要用到mybatis动态sql的foreach进行遍历
     * ignore：遇到相同的数据，就忽略
     * @param interacts 多条交互信息
     * @param type 本次入库的交互信息的类型
     */
    @Insert("""
            <script>
                insert ignore into db_topic_interact_${type} values
                <foreach collection = "interacts" item="item" separator = ",">
                    (#{item.tid}, #{item.uid}, #{item.time})
                </foreach>
            </script>
            """)
    void  addInteract(List<Interact> interacts, String type);

    /**
     * 将多条交互信息一次性删除
     * 普通的sql处理不了列表，需要用到mybatis动态sql的foreach进行遍历
     * @param interacts 多条交互信息
     * @param type 本次入库的交互信息的类型
     */
    @Insert("""
            <script>
                delete from db_topic_interact_${type} where
                <foreach collection = "interacts" item="item" separator = " or ">
                    (tid = #{item.tid} and uid = #{item.uid})
                </foreach>
            </script>
            """)
    void  deleteInteract(List<Interact> interacts, String type);

    /**
     * 返回该帖子对应于交互操作的数量点赞量多少，收藏量多少...)
     * @param tid 帖子id
     * @param type 交互操作类型
     */
    @Select("select count(*) from db_topic_interact_${type} where tid = #{tid}")
    int interactCount(int tid, String type);

    /**
     * 返回1，说明用户对该帖子点赞/收藏；否则说明没有
     * @param tid 帖子id
     * @param uid 用户id
     * @param type 交互操作的类型
     */
    @Select("select count(*) from db_topic_interact_${type} where tid = #{tid} and uid = #{uid}")
    int userInteractCount(int tid, int uid, String type);

    @Select("""
            select * from db_topic_interact_collect left join db_topic on tid = db_topic.id
            where db_topic_interact_collect.uid = #{uid}
            """)
    List<Topic> collectTopics(int uid);

}
