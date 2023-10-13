package com.xiaoRed.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xiaoRed.entity.vo.response.WeatherVo;
import com.xiaoRed.service.WeatherService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Service
public class WeatherServiceImpl implements WeatherService {

    //这个RestTemplate可以发送Rest请求，不用从头开始写
    @Resource
    RestTemplate restTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    //用于“和风天气”api开发的用户认证key
    @Value("${spring.weather.key}")
    String key;

    /**
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @return
     */
    @Override
    public WeatherVo fetchWeather(double longitude, double latitude){
        return fetchFromCache(longitude, latitude);
    }

    /**
     * 同一地区，一个小时内的天气变化一般不大，因此可以做缓存，减少对api接口的调用(省着点免费次数)，也起到了一定的防刷保护作用
     * 1、通过经纬度，调用城市搜索api，拿到响应数据，提取出地区id
     * 2、以地区id为key去redis里找，如果找得到，直接将天气信息返回
     * 3、如果缓存里没有，只能老老实实调用获取天气信息的api，拿到数据后，先存入redis做缓存，再返回
     * @param longitude 经度
     * @param latitude 纬度
     * @return
     */

    private WeatherVo fetchFromCache(double longitude, double latitude){
        //先通过经纬度，拿到此次请求的地区id
        //涉及到“和风天气”的城市搜索api，文档地址：https://dev.qweather.com/docs/api/geoapi/city-lookup/
        byte[] data = restTemplate.getForObject(
                "https://geoapi.qweather.com/v2/city/lookup?location="+longitude+","+latitude+"&key="+key,byte[].class);
        JSONObject geo = this.decompressStringToJson(data);
        if(geo==null)return null;
        JSONObject location = geo.getJSONArray("location").getJSONObject(0); //将相应数据中的location字段拿出来
        int id = location.getInteger("id"); //拿到地区id

        //然后尝试从缓存中取，看此次请求的地区有没有在缓存里
        String key = "weather:" + id;
        String cache = stringRedisTemplate.opsForValue().get(key);
        if(cache!=null) //如果此次请求的地区在缓存里，那就把缓存数据转换为WeatherVo，然后直接返回
            return JSONObject.parseObject(cache).to(WeatherVo.class);
        //如果缓存中没有此次请求地区的天气信息，那就只能去请求一次获取天气信息的api
        WeatherVo vo = this.fetchFromAPI(id, location);
        if(vo == null)return null;
        //拿到地区的天气信息，存入redis缓存，设置1小时过期时间
        stringRedisTemplate.opsForValue().set(key, JSONObject.from(vo).toJSONString(), 1, TimeUnit.HOURS);
        return vo;
    }

    /**
     * 获取实时天气和未来5小时的天气预报
     * @param id 地区id
     * @param location 为了封装WeatherVo的字段
     * @return
     */

    private WeatherVo fetchFromAPI(int id, JSONObject location){
        WeatherVo vo = new WeatherVo();
        vo.setLocation(location);
        //涉及到“和风天气”的实时天气api，文档地址：https://dev.qweather.com/docs/api/weather/weather-now/，这里使用的是免费的那个请求地址
        JSONObject now = this.decompressStringToJson(restTemplate.getForObject(
                "https://devapi.qweather.com/v7/weather/now?location="+id+"&key="+key, byte[].class));
        if(now == null) return null;
        vo.setNow_weather(now.getJSONObject("now"));

        //涉及到“和风天气”的逐小时天气预报api，文档地址：https://dev.qweather.com/docs/api/weather/weather-hourly-forecast/，这里使用的是免费的那个请求地址
        JSONObject hourly = this.decompressStringToJson(restTemplate.getForObject(
                "https://devapi.qweather.com/v7/weather/24h?location="+id+"&key="+key, byte[].class));
        if(hourly == null) return null;
        //请求获得的是未来24小时的天气预报，项目中只用未来5小时，因此这里做个限制，只拿前5个
        vo.setHourly(new JSONArray(hourly.getJSONArray("hourly").stream().limit(5).toList()));
        return vo;
    }

    /**
     * 解压拿到的响应数据
     * 文档中有提到，请求返回的数据是JSON格式，在这前提下进行了Gzip压缩。因此要真正拿到这里面的JSON数据，要先进行解压
     * @param data 拿到的被Gzip压缩的响应数据
     * @return 解压后的JSON格式的数据
     */
    private JSONObject decompressStringToJson(byte[] data){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] buffer = new byte[1024]; //作为输入流和输出流的传输媒介
            int read;
            while((read = gzip.read(buffer)) != -1) //把gzip中的数据读到buffer，直到读完为止
                outputStream.write(buffer, 0, read); //把buffer中的数据(已被解压)写入到这个输出流
            gzip.close();
            outputStream.close();
            return JSONObject.parseObject(outputStream.toString()); //转换为JSON text的形式
        }catch(IOException e){
            return null;
        }
    }

}
