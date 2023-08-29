package com.xiaoRed.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoRed.entity.dto.Account;
import com.xiaoRed.entity.dto.AccountDetails;
import com.xiaoRed.entity.vo.request.DetailsSaveVo;
import com.xiaoRed.mapper.AccountDetailsMapper;
import com.xiaoRed.service.AccountDetailsService;
import com.xiaoRed.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * (AccountDetails)表服务实现类
 *
 * @author makejava
 * @since 2023-08-29 15:52:12
 */
@Service("accountDetailsService")
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService {

    @Resource
    AccountService accountService;
    /**
     * 通过用户id从表中找到该条记录并返回给前端显示
     * @param id 用户id
     * @return
     */
    @Override
    public AccountDetails findAccountDetailsById(int id) {
        return this.getById(id);
    }

    /**
     * 保存账户详细信息，涉及到改两张表
     * 改用户名涉及到db_account表，其他的涉及db_account_details表
     * @param id 用户id
     * @param vo 前端提交的账户详细信息表单
     * @return 是否保存成功
     */
    @Override
    public boolean saveAccountDetails(int id, DetailsSaveVo vo) {
        Account account = accountService.findAccountByNameOrEmail(vo.getUsername()); //从用户表中查有无同名的用户
        if(account == null || account.getId() == id){ //如果没有同名用户或同名用户就是自己，才可以进一步地修改
            //db_account表中修改用户名
            accountService.update()
                    .eq("id", id)
                    .set("username", vo.getUsername())
                    .update();
            //db_account_details表中修改用户详细信息。如果表中无对应id数据，则插入，否则更新
            this.saveOrUpdate(new AccountDetails(id, vo.getGender(), vo.getPhone(), vo.getQq(),
                    vo.getWx(), vo.getDesc()));
            return true;
        }
        return false;
    }
}

