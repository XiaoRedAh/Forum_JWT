package com.xiaoRed.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoRed.entity.dto.AccountPrivacy;
import com.xiaoRed.entity.vo.request.SavePrivacyVo;
import com.xiaoRed.mapper.AccountPrivacyMapper;
import com.xiaoRed.service.AccountPrivacyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * (AccountPrivacy)表服务实现类
 *
 * @author makejava
 * @since 2023-09-02 11:56:49
 */
@Service("accountPrivacyService")
public class AccountPrivacyServiceImpl extends ServiceImpl<AccountPrivacyMapper, AccountPrivacy> implements AccountPrivacyService {

    /**
     * 保存隐私设置：一次只能修改一中隐私
     * @param id
     * @param vo 前端传来的隐私种类 + 是否公开 分装为一个vo
     */
    @Override
    @Transactional
    public void savePrivacy(int id, SavePrivacyVo vo){
        //从数据库查找对应的隐私，如果没有记录，返回默认的隐私对象【权限全开】
        AccountPrivacy privacy = Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
        //按照这次传来的隐私种类，修改对应的隐私
        boolean status = vo.isStatus();
        switch (vo.getType()){
            case "phone" -> privacy.setPhone(status);
            case "qq" -> privacy.setQq(status);
            case "wx" -> privacy.setWx(status);
            case "gender" -> privacy.setGender(status);
            case "email" -> privacy.setEmail(status);
        }
        //把改好的隐私再存回数据库
        this.saveOrUpdate(privacy);
    }

    /**
     * 获得对应账号的隐私权限信息，如果数据库中没有对应的记录，则返回一个默认值（权限全开）
     * @param id
     * @return
     */
    @Override
    public AccountPrivacy getAccountPrivacy(int id){
        return Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
    }

}

