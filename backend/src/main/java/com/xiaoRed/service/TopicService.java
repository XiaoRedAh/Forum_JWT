package com.xiaoRed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaoRed.entity.dto.Topic;
import com.xiaoRed.entity.dto.TopicType;
import com.xiaoRed.entity.vo.request.TopicCreateVo;

import java.util.List;


public interface TopicService extends IService<Topic> {
    List<TopicType> listTypes();

    String createTopic(int uid, TopicCreateVo vo);
}

