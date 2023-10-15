package com.xiaoRed.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoRed.entity.dto.Topic;
import com.xiaoRed.entity.dto.TopicType;
import com.xiaoRed.entity.vo.request.TopicCreateVo;
import com.xiaoRed.mapper.TopicMapper;
import com.xiaoRed.mapper.TopicTypeMapper;
import com.xiaoRed.service.TopicService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import net.sf.jsqlparser.statement.select.Top;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Resource
    TopicTypeMapper topicTypeMapper;

    private Set<Integer> types = null;
    @PostConstruct
    private void initTypes(){
        types = this.listTypes()
                .stream()
                .map(TopicType::getId)
                .collect(Collectors.toSet());
    }

    /**
     * 返回所有帖子类型的id，类型名，描述，颜色。即de_topic_type表中的数据
     * @return
     */
    @Override
    public List<TopicType> listTypes() {
        return topicTypeMapper.selectList(null);
    }

    @Override
    public String createTopic(int uid, TopicCreateVo vo){
        //验证content是否超出文章长度限制
        if(!contentLimitCheck(vo.getContent()))
            return "文章内容超出长度限制，发文失败！";
        //验证文章类型(用户正常操作肯定不会有问题的，因此触发这种情况的，一般都是故意来搞攻击的)
        if(!types.contains(vo.getType()))
            return "文章类型非法，发文失败";
        //todo:限制发文频率

        Topic topic = new Topic();
        BeanUtils.copyProperties(vo, topic);
        topic.setContent(vo.getContent().toJSONString());
        topic.setUid(uid);
        topic.setTime(new Date());
        if(this.save(topic)){
            return null;
        }else{
            return "内部错误，请联系管理员";
        }
    }

    private boolean contentLimitCheck(JSONObject content){
        if(content==null) return false;
        long length = 0;
        for(Object op : content.getJSONArray("ops")){
            length += JSONObject.from(op).getString("insert").length();
            if(length>20000) return false;
        }
        return true;

    }
}

