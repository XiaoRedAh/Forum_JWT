package com.xiaoRed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoRed.entity.dto.Account;
import org.apache.ibatis.annotations.Mapper;


/**
 * (Account)表数据库访问层
 *
 * @author makejava
 * @since 2023-08-09 10:12:40
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

}
