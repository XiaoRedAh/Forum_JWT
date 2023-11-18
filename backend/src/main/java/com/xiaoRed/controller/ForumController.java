package com.xiaoRed.controller;

import com.xiaoRed.constants.Const;
import com.xiaoRed.entity.RestBean;
import com.xiaoRed.entity.vo.response.*;
import com.xiaoRed.entity.vo.request.TopicCreateVo;
import com.xiaoRed.service.TopicService;
import com.xiaoRed.service.WeatherService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 论坛主体功能相关的controller
 */
@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Resource
    WeatherService weatherService;

    @Resource
    TopicService topicService;

    /**
     * 获取天气信息，包括实时天气和未来5小时的天气预报
     * @param longitude 经度
     * @param latitude 维度
     */
    @GetMapping("/weather")
    public RestBean<WeatherVo> weather(double longitude, double latitude){
        WeatherVo vo = weatherService.fetchWeather(longitude, latitude);
        return vo == null ? RestBean.failure(400, "获取地理位置信息与天气失败，请联系管理员！") : RestBean.success(vo);
    }

    /**
     * 返回所有帖子类型的id，类型名，描述，颜色。即de_topic_type表中的数据
     */
    @GetMapping("/types")
    public RestBean<List<TopicTypeVo>> listTypes(){
        return RestBean.success(topicService.listTypes()
                .stream()
                .map(topicType -> topicType.asViewObject(TopicTypeVo.class))
                .toList());
    }

    /**
     * 发表帖子功能
     * @param vo 前端发送来的发表帖子参数包装为一个TopicCreateVo对象
     * @param uid 帖子作者的id，直接从缓存里拿
     */
    @PostMapping("/create-topic")
    public  RestBean<Void> createTopic(@Valid @RequestBody TopicCreateVo vo,
                                       @RequestAttribute(Const.ATTR_USER_ID) int uid){
        String message = topicService.createTopic(uid, vo);
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }

    /**
     * 根据选定查第几页和选定的帖子类型展示帖子列表
     * @param page 展示的是第几页,由于mybatis-plus分页器的页号从1开始，因此实际要+1
     * @param type 展示的帖子类型，全选则为0
     */
    @GetMapping("/list-topic")
    public RestBean<List<TopicPreviewVo>> listTopic(@RequestParam @Min(0) int page,
                                              @RequestParam @Min(0) int type){
        return RestBean.success(topicService.listTopicByPage(page + 1, type));
    }

    /**
     * 展示置顶帖子
     */
    @GetMapping("/top-topic")
    public RestBean<List<TopicTopVo>> TopTopic(){
        return RestBean.success(topicService.listTopTopics());
    }

    /**
     * 帖子详情
     * @param tid 帖子id
     */
    @GetMapping("/topic")
    public RestBean<TopicDetailVo> Topic(@RequestParam @Min(0) int tid){
        return RestBean.success(topicService.getTopic(tid));
    }

}
