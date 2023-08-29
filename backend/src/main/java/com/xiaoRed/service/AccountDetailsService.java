package com.xiaoRed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaoRed.entity.dto.AccountDetails;
import com.xiaoRed.entity.vo.request.DetailsSaveVo;


/**
 * (AccountDetails)表服务接口
 *
 * @author makejava
 * @since 2023-08-29 15:52:10
 */
public interface AccountDetailsService extends IService<AccountDetails> {

    AccountDetails findAccountDetailsById(int id);
    boolean saveAccountDetails(int id, DetailsSaveVo vo);

}

