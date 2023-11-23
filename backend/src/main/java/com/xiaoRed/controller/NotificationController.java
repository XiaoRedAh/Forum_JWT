package com.xiaoRed.controller;

import com.xiaoRed.constants.Const;
import com.xiaoRed.entity.RestBean;
import com.xiaoRed.entity.vo.response.NotificationVo;
import com.xiaoRed.service.NotificationService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 消息通知提醒
 */
@RestController
@RequestMapping("api/notification")
public class NotificationController {
    @Resource
    NotificationService notificationService;

    /**
     * 展示当前用户全部未读通知
     * @param uid 当前用户id
     */
    @GetMapping("/list")
    public RestBean<List<NotificationVo>> listNotification(@RequestParam(Const.ATTR_USER_ID) int uid){
        return RestBean.success(notificationService.findUserNotification(uid));
    }

    /**
     * 删除当前用户的指定通知
     * 调用时机是用户查看到该未读通知，点击它跳转的同时，删除该通知
     * @param id 通知id
     * @param uid 当前用户id
     */
    @GetMapping("/delete")
    public RestBean<Void> deleteNotification(@RequestParam @Min(0) int id,
                                             @RequestParam(Const.ATTR_USER_ID) int uid){
        notificationService.deleteUserNotification(id, uid);
        return RestBean.success();
    }

    /**
     * 删除当前用户所有未读通知
     * @param uid 当前用户id
     */
    @GetMapping("delete-all")
    public RestBean<Void> deleteNotification(@RequestParam(Const.ATTR_USER_ID) int uid){
        notificationService.deleteUserAllNotification(uid);
        return RestBean.success();
    }

}
