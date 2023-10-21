package com.xiaoRed.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoRed.constants.Const;
import com.xiaoRed.entity.dto.Topic;
import com.xiaoRed.entity.dto.TopicType;
import com.xiaoRed.entity.vo.request.TopicCreateVo;
import com.xiaoRed.entity.vo.response.TopicPreviewVo;
import com.xiaoRed.entity.vo.response.TopicTopVo;
import com.xiaoRed.mapper.TopicMapper;
import com.xiaoRed.mapper.TopicTypeMapper;
import com.xiaoRed.service.TopicService;
import com.xiaoRed.utils.CacheUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Resource
    TopicTypeMapper topicTypeMapper;

    @Resource
    CacheUtil cacheUtil;

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
            //一旦有新帖子发布，有关帖子列表的所有缓存立即删除
            cacheUtil.deleteCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
            return null;
        }else{
            return "内部错误，请联系管理员";
        }
    }

    /**
     * 根据选定查第几页和选定的帖子类型展示帖子列表
     * topicList(page*10)和topicListByType(page*10, type)的start都是page*10，其实就是一页展示10个帖子
     * 展示的是第0页，则是查询到的数据的0~9条；展示的是第1页，则是查询到的数据的10~19条...
     * @param page 展示的是第几页
     * @param type 展示的帖子类型，全选则为0
     */
    @Override
    public List<TopicPreviewVo> listTopicByPage(int page, int type){
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + page + ":" + type;
        List<TopicPreviewVo> previewVoList = cacheUtil.takeListFromCache(key, TopicPreviewVo.class); //先去缓存里拿
        if (previewVoList != null) return previewVoList; //从缓存中取到，就不用再去数据库里请求了

        List<Topic> topics;
        if(type == 0)
            topics = baseMapper.topicList(page*10);
        else
            topics = baseMapper.topicListByType(page*10, type);
        if(topics.isEmpty()) return null;
        previewVoList = topics.stream().map(this::resolveToPreview).toList(); //转化为TopicPreviewVo的列表
        cacheUtil.saveListToCache(key, previewVoList, 60); //拿到要求的帖子列表后，先存到缓存中
        return previewVoList;
    }

    /**
     * 展示置顶帖子
     * 帖子的top字段：0不置顶，1置顶
     */
    @Override
    public List<TopicTopVo> listTopTopics(){
        List<Topic> topics = baseMapper.selectList(Wrappers.<Topic>query()
                .select("id", "title", "time")
                .eq("top", 1));
        return topics.stream().map(topic ->{
            TopicTopVo vo = new TopicTopVo();
            BeanUtils.copyProperties(topic, vo);
            return vo;
        }).toList();
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

    /**
     * 将Topic类型的对象转换为TopicPreviewVo类型的对象
     * 同名属性可以直接bean拷贝
     * 主要就是TopicPreviewVo的text属性和images属性
     * 帖子文本存储在Topic的Content属性的ops数组中的insert字段里，由于只是展示在列表中，只展示前300个字符即可
     * 图片存储在Topic的Content属性的ops数组中的insert字段下的image字段里
     */
    private TopicPreviewVo resolveToPreview(Topic topic){
        TopicPreviewVo vo = new TopicPreviewVo();
        BeanUtils.copyProperties(topic, vo); //先把topic中的同名属性复制给vo
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops"); //先拿到ops数组
        for(Object ob : ops){
            Object insert = JSONObject.from(ob).get("insert");
            if(insert instanceof String text){ //如果insert里直接就是文本，那其实就是帖子内容
                if(previewText.length()>=300)continue;
                previewText.append(text);
            }else if(insert instanceof Map<?, ?> map){ //如果insert里还是一个JSON(这里用Map代替，JSON本质就是一个map)，则大概率是图片
                //还是要判断一下，有image字段，才拿
                Optional.ofNullable(map.get("image"))
                        .ifPresent(obj -> images.add(obj.toString()));
            }
        }
        //这里还要切割出前300个字符，因为之前取insert时，可能很长，直接就超300了
        System.out.println(previewText);
        vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) : previewText.toString());
        vo.setImages(images);
        return vo;
    }
}

