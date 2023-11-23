package com.xiaoRed.service;

import com.xiaoRed.entity.vo.response.NotificationVo;

import java.util.List;


/**
 * (Notification)表服务接口
 *
 * @author makejava
 * @since 2023-11-23 18:13:11
 */
public interface NotificationService {

    List<NotificationVo> findUserNotification(int uid);
    void deleteUserNotification(int id, int uid);
    void deleteUserAllNotification(int uid);
    void addNotification(int uid, String title, String content, String type, String url);

}

