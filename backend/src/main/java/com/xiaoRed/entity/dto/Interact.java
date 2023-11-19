package com.xiaoRed.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 交互操作的信息
 */
@Data
@AllArgsConstructor
public class Interact {
    Integer tid;
    Integer uid;
    Date time;
    String type; //交互类型：点赞like/收藏collect/...

    /**
     * 存储在redis中的交互信息的键为type:tid:uid
     * 这里通过Interact对象生成后缀tid:uid
     */
    public String toKey(){
        return tid + ":" + uid;
    }

    /**
     * 根据外界传来的str和type，解析生成Interact对象返回
     * @param str 存在redis的键为type:tid:uid，实际saveInteract方法中传来的是切割过后的tid:uid
     * @param type 交互操作类型
     */
    public static Interact parseInteract(String str, String type){
        String[] keys = str.split(":");
        return new Interact(Integer.parseInt(keys[0]), Integer.parseInt(keys[1]), new Date(), type);
    }

}
