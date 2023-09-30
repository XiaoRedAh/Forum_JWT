package com.xiaoRed.controller;

import com.xiaoRed.entity.RestBean;
import com.xiaoRed.service.ImageService;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
public class ObjectController {

    @Resource
    ImageService imageService;

    /**
     * 获取头像
     * @param request
     * @param response
     * @throws Exception
     */
    @GetMapping("/images/avatar/**")
    public void avatarFetch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.fetchImage(request, response);
    }

    /**
     * 这个私有方法作为获取各种类型图片的统一接口
     * 设置响应头：缓存周期设置为1个月，这样的话，1月内，浏览器获取该图片都不用再向后端请求，直接从本地缓存里拿，优化性能。
     * @param request
     * @param response
     * @throws IOException
     */
    private void fetchImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String imagePath = request.getServletPath().substring(7); //minio存储的路径没有“/images”，将它去掉
        ServletOutputStream stream = response.getOutputStream();
        //路径长度小于13的一看就不对，直接报错就行
        if(imagePath.length()<=13){
            stream.println(RestBean.failure(404, "Not found").toString());
        }else{
            try{
                imageService.fetchImageFromMinio(stream, imagePath);
                //设置响应头：缓存周期设置为1个月，这样的话，1月内，浏览器获取该图片都不用再向后端请求，直接从本地缓存里拿，优化性能。
                response.setHeader("Cache-Control", "max-age-259200"); //缓存周期：1个月
            }catch(ErrorResponseException e){//注意这个异常时minio里的异常
                if(e.response().code()==404){//minio响应404，说明没有存储该图片
                    response.setStatus(404);
                    stream.println(RestBean.failure(404, "Not found").toString());
                }else{ //如果不是404，说明是其他问题，打印个日志
                    log.error("从Minio服务器获取图片出现异常：" + e.getMessage(), e);
                }
            }
        }
    }
}
