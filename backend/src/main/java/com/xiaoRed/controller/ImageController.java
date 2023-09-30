package com.xiaoRed.controller;

import com.xiaoRed.constants.Const;
import com.xiaoRed.entity.RestBean;
import com.xiaoRed.service.ImageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 专门用于处理图片的controller
 */
@Slf4j
@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Resource
    ImageService imageService;

    /**
     * 上传头像功能：利用minio客户端，将头像传到minio服务器存储，数据库中只需要存储这个头像的url即可
     * @param file 上传的头像文件
     * @param id
     * @return
     * @throws IOException
     */
    @PostMapping("/avatar")
    public RestBean<String> uploadAvatar(@RequestParam("file")MultipartFile file,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id)throws IOException {
        if (file.getSize() > 1025*1025)
            return RestBean.failure(400, "头像图片不能大于1MB");
        log.info("正在进行头像上传操作...");
        String url = imageService.uploadAvatar(file, id);
        if(url!=null){
            log.info("头像上传成功，大小为：" + file.getSize());
            return RestBean.success(url);
        }else{
            return RestBean.failure(40, "头像上传失败，请联系管理员!");
        }
    }
}
