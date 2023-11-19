package com.xiaoRed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaoRed.entity.dto.Interact;
import com.xiaoRed.entity.dto.Topic;
import com.xiaoRed.entity.dto.TopicType;
import com.xiaoRed.entity.vo.request.TopicCreateVo;
import com.xiaoRed.entity.vo.response.TopicDetailVo;
import com.xiaoRed.entity.vo.response.TopicPreviewVo;
import com.xiaoRed.entity.vo.response.TopicTopVo;

import java.util.List;


public interface TopicService extends IService<Topic> {
    List<TopicType> listTypes();

    String createTopic(int uid, TopicCreateVo vo);

    List<TopicPreviewVo> listTopicByPage(int page, int type);

    List<TopicTopVo> listTopTopics();

    TopicDetailVo getTopic(int tid);

    void interact(Interact interact, boolean state);
}

