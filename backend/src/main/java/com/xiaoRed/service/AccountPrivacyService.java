package com.xiaoRed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaoRed.entity.dto.AccountPrivacy;
import com.xiaoRed.entity.vo.request.SavePrivacyVo;


/**
 * (AccountPrivacy)表服务接口
 *
 * @author makejava
 * @since 2023-09-02 11:56:47
 */
public interface AccountPrivacyService extends IService<AccountPrivacy> {

    void savePrivacy(int id, SavePrivacyVo vo);

    AccountPrivacy getAccountPrivacy(int id);
}

