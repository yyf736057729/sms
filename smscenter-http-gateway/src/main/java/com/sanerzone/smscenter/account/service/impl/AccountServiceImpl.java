package com.sanerzone.smscenter.account.service.impl;

import com.sanerzone.smscenter.account.entity.Account;
import com.sanerzone.smscenter.account.mapper.AccountMapper;
import com.sanerzone.smscenter.account.service.IAccountService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 资金账户信息表 服务实现类
 * </p>
 *
 * @author XuRui
 * @since 2017-06-12
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements IAccountService {
	public List<Account> selectByCustom() {
		return baseMapper.selectByCustom();
	}
	
	public List<Map<String,String>> findAccountList() {
		return baseMapper.findAccountList();
	}
}
