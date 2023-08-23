package com.xiaoRed.entity;


import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

/**
 * 响应实体类封装，Rest风格
 * @param code 状态码
 * @param data 响应数据
 * @param message 其他消息
 * @param <T> 响应数据类型
 */
public record RestBean<T>(int code, T data, String message) {

    //成功响应，携带数据返回
    public static <T> RestBean<T> success(T data){
        return new RestBean<>(200, data, "请求成功");
    }

    //成功响应，不携带数据返回
    public static <T> RestBean<T> success(){
        return new RestBean<>(200, null, "请求成功");
    }

    //响应失败，需要返回错误码和错误信息
    public static <T> RestBean<T> failure(int code, String message){
        return new RestBean<>(code, null, message);
    }

    //未授权(没有登录),响应失败，固定401错误码，需指定错误信息
    public static <T> RestBean<T> unauthorized(String message){
        return failure(401, message);
    }

    //拒绝访问(登录但没有对应访问权限),响应失败，固定403错误码，需指定错误信息
    public static <T> RestBean<T> forbidden(String message){
        return failure(403, message);
    }

    //将响应对象转换为Json格式
    public String asJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }

}
