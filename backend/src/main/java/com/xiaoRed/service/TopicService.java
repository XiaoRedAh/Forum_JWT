package com.xiaoRed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaoRed.entity.dto.Interact;
import com.xiaoRed.entity.dto.Topic;
import com.xiaoRed.entity.dto.TopicType;
import com.xiaoRed.entity.vo.request.AddCommentVo;
import com.xiaoRed.entity.vo.request.TopicCreateVo;
import com.xiaoRed.entity.vo.request.TopicUpdateVo;
import com.xiaoRed.entity.vo.response.CommentVo;
import com.xiaoRed.entity.vo.response.TopicDetailVo;
import com.xiaoRed.entity.vo.response.TopicPreviewVo;
import com.xiaoRed.entity.vo.response.TopicTopVo;

import java.util.List;


public interface TopicService extends IService<Topic> {
    List<TopicType> listTypes();

    String createTopic(int uid, TopicCreateVo vo);

    List<TopicPreviewVo> listTopicByPage(int page, int type);

    List<TopicTopVo> listTopTopics();

    TopicDetailVo getTopic(int uid, int tid);

    void interact(Interact interact, boolean state);

    List<TopicPreviewVo> listTopicCollects(int uid);

    String updateTopic(int uid, TopicUpdateVo vo);

    String createComment(int uid, AddCommentVo vo);

    List<CommentVo> comments(int tid, int pageNum);

    void deleteComment(int id, int uid);
}

