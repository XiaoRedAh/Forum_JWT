package com.xiaoRed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoRed.entity.dto.Interact;
import com.xiaoRed.entity.dto.Topic;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

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



}
