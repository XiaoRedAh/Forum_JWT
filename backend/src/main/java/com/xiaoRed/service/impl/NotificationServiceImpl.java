package com.xiaoRed.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoRed.entity.dto.Notification;
import com.xiaoRed.entity.vo.response.NotificationVo;
import com.xiaoRed.mapper.NotificationMapper;
import com.xiaoRed.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (Notification)表服务实现类
 *
 * @author makejava
 * @since 2023-11-23 18:13:11
 */
@Service("notificationService")
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    /**
     * 获取当前用户所有的消息通知
     * @param uid 当前用户id
     */
    @Override
    public List<NotificationVo> findUserNotification(int uid) {
        return this.list(Wrappers.<Notification>query().eq("uid", uid))
                .stream()
                .map(notification -> notification.asViewObject(NotificationVo.class))
                .toList();
    }

    /**
     * 删除用户指定的消息通知
     * @param id 通知id
     * @param uid 用户id
     */
    @Override
    public void deleteUserNotification(int id, int uid) {
        this.remove(Wrappers.<Notification>query().eq("id", id).eq("uid", uid));
    }

    /**
     * 删除用户所有的通知
     * @param uid 用户id
     */
    @Override
    public void deleteUserAllNotification(int uid) {
        this.remove(Wrappers.<Notification>query().eq("uid", uid));
    }

    /**
     * 给用户添加通知
     * @param uid 用户id
     * @param title 通知标题
     * @param content 通知内容
     * @param type 通知类型
     * @param url 点击跳转到通知指引的地方
     */
    @Override
    public void addNotification(int uid, String title, String content, String type, String url) {
        Notification notification = new Notification();
        notification.setUid(uid);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setUrl(url);
        this.save(notification);
    }
}

