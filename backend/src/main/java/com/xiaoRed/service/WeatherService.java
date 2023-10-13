package com.xiaoRed.service;

import com.xiaoRed.entity.vo.response.WeatherVo;

public interface WeatherService {
    WeatherVo fetchWeather(double longitude, double latitude);
}
