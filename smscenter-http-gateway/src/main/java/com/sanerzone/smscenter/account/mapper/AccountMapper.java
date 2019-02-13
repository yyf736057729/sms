package com.sanerzone.smscenter.account.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sanerzone.smscenter.account.entity.Account;

/**
 * <p>
  * 资金账户信息表 Mapper 接口
 * </p>
 *
 * @author XuRui
 * @since 2017-06-12
 */
public interface AccountMapper extends BaseMapper<Account> {

	public List<Account> selectByCustom();
	public List<Map<String,String>> findAccountList();
}