package com.xiaoRed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoRed.entity.dto.AccountDetails;
import org.apache.ibatis.annotations.Mapper;


/**
 * (AccountDetails)表数据库访问层
 *
 * @author makejava
 * @since 2023-08-29 15:52:07
 */
@Mapper
public interface AccountDetailsMapper extends BaseMapper<AccountDetails> {

}
