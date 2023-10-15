package com.xiaoRed.controller;

import com.xiaoRed.constants.Const;
import com.xiaoRed.entity.RestBean;
import com.xiaoRed.entity.vo.TopicTypeVo;
import com.xiaoRed.entity.vo.request.TopicCreateVo;
import com.xiaoRed.entity.vo.response.WeatherVo;
import com.xiaoRed.service.TopicService;
import com.xiaoRed.service.WeatherService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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
     * @return
     */
    @GetMapping("/weather")
    public RestBean<WeatherVo> weather(double longitude, double latitude){
        WeatherVo vo = weatherService.fetchWeather(longitude, latitude);
        return vo == null ? RestBean.failure(400, "获取地理位置信息与天气失败，请联系管理员！") : RestBean.success(vo);
    }

    /**
     * 返回所有帖子类型的id，类型名，描述，颜色。即de_topic_type表中的数据
     * @return
     */
    @GetMapping("/types")
    public RestBean<List<TopicTypeVo>> listTypes(){
        return RestBean.success(topicService.listTypes()
                .stream()
                .map(topicType -> topicType.asViewObject(TopicTypeVo.class))
                .toList());
    }

    @PostMapping("/create-topic")
    public  RestBean<Void> createTopic(@Valid @RequestBody TopicCreateVo vo,
                                       @RequestAttribute(Const.ATTR_USER_ID) int uid){
        String message = topicService.createTopic(uid, vo);
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }
}
