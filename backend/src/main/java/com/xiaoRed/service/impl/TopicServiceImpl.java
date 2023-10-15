package com.xiaoRed.service.impl;

import com.xiaoRed.entity.dto.TopicType;
import com.xiaoRed.mapper.TopicTypeMapper;
import com.xiaoRed.service.TopicService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Resource
    TopicTypeMapper topicTypeMapper;

    /**
     * 返回所有帖子类型的id，类型名，描述，颜色。即de_topic_type表中的数据
     * @return
     */
    @Override
    public List<TopicType> listTypes() {
        return topicTypeMapper.selectList(null);
    }
}

