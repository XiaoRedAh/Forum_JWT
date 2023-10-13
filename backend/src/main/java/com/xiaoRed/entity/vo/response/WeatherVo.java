package com.xiaoRed.entity.vo.response;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * 响应给前端的天气信息
 */
@Data
public class WeatherVo {
    JSONObject location; //位置信息
    JSONObject now_weather; //实时天气
    JSONArray hourly; //天气预报
}
