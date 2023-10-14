package com.xiaoRed.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaoRed.entity.dto.Account;
import com.xiaoRed.entity.dto.ImageStore;
import com.xiaoRed.mapper.AccountMapper;
import com.xiaoRed.service.ImageService;
import com.xiaoRed.service.ImageStoreService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Resource
    MinioClient minioClient;

    @Resource
    AccountMapper accountMapper;

    @Resource
    ImageStoreService imageStoreService;

    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    /**
     * minio客户端将头像上传到minio服务端
     * 图片名不要直接设置为上传者的id，而是用随机的uuid
     *   1、使用浏览器本地缓存，如果采用上传者的id，那么图片被修改，不会加载新的图片
     *   2、反爬虫
     * @param file 要上传的头像文件
     * @param id
     * @return 上传成功，返回url，失败返回null
     * @throws IOException
     */
    @Override
    public String uploadAvatar(MultipartFile file, int id) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-", ""); //先用uuid随机给文件起个名
        imageName = "/avatar/" + imageName; //加个目录前缀,表示将图片存放在avatar目录下
        //设置上传参数
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("forum") //上传到forum存储桶
                .stream(file.getInputStream(), file.getSize(), -1) //分块大小默认
                .object(imageName) //存储名字
                .build();
        try{ //成功上传
            minioClient.putObject(args); //利用minio客户端以及设置好的参数进行上传
            if(accountMapper.update(null, Wrappers.<Account>update().eq("id", id).set("avatar", imageName))>0)
                return imageName;
            else return null;
        }catch(Exception e){ //上传出现异常
            log.error("图片上传失败：" + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String uploadImage(MultipartFile file, int id) throws IOException {
        //todo:限制请求频率


        String imageName = UUID.randomUUID().toString().replace("-", ""); //先用uuid随机给文件起个名
        Date date = new Date();
        imageName = "/cache/" + format.format(date) + "/" + imageName; //加个目录前缀,表示将图片存放在cache目录下,并且按日期分类存储，方便后续管理
        //设置上传参数
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("forum") //上传到forum存储桶
                .stream(file.getInputStream(), file.getSize(), -1) //分块大小默认
                .object(imageName) //存储名字
                .build();
        try{ //成功上传
            minioClient.putObject(args); //利用minio客户端以及设置好的参数进行上传
            if (imageStoreService.save(new ImageStore(id, imageName, date))){
                return imageName;
            }else{
                return null;
            }
        }catch(Exception e){ //上传出现异常
            log.error("图片上传失败：" + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从Minio服务器获取图片
     * @param outputStream
     * @param image 图片路径
     */
    @Override
    public void fetchImageFromMinio(OutputStream outputStream, String image) throws Exception {
        //设置获取参数
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket("forum")
                .object(image)
                .build();
        GetObjectResponse response = minioClient.getObject(args); //这个response是一个输入流
        //将获取到图片的这个输入流拷贝到输出流
        IOUtils.copy(response, outputStream);
    }

}
