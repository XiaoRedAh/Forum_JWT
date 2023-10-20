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

    //service调用之前就先获取到所有帖子类型的id，方便后续的校验工作（比如createTopic方法，校验类型id是否合法）
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

    /**
     * 发表帖子功能
     * 前置工作：对帖子类型id，长度做校验，并对请求做限流，限制发文频率
     * @param uid 帖子作者的id
     * @param vo 前端发送来的发表帖子参数包装为一个TopicCreateVo对象
     * @return 返回空表示创建成功，否则返回错误提示
     */
    @Override
    public String createTopic(int uid, TopicCreateVo vo){
        //验证帖子长度是否超出文章长度限制
        if(!contentLimitCheck(vo.getContent()))
            return "文章内容超出长度限制，发文失败！";
        //验证文章类型(用户正常操作肯定不会有问题的，因此触发这种情况的，一般都是故意来搞攻击的)
        if(!types.contains(vo.getType()))
            return "文章类型非法，发文失败";
        //todo:限制发文频率


        Topic topic = new Topic();
        BeanUtils.copyProperties(vo, topic); //将vo的属性拷贝给topic对应的同名属性
        topic.setContent(vo.getContent().toJSONString()); //content要先把json格式转换为字符串
        topic.setUid(uid);
        topic.setTime(new Date());
        if(this.save(topic)){
            return null;
        }else{
            return "内部错误，请联系管理员";
        }
    }

    /**
     * 校验帖子长度是否超出限制
     * @param content 前端发送过来的帖子内容，其实帖子真正的文本存储在ops这个JSON数组下的insert字段
     * @return 返回true表示通过校验，返回false表示超出长度限制，校验不通过
     */
    private boolean contentLimitCheck(JSONObject content){
        if(content==null) return false;
        long length = 0;
        for(Object op : content.getJSONArray("ops")){ //取出ops这个JSON数组
            length += JSONObject.from(op).getString("insert").length(); //从这个数组中拿到insert字段的内容，记录长度
            if(length>20000) return false;
        }
        return true;
    }
}

