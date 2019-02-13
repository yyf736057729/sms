package com.sanerzone.common.modules.account.dao;

import java.util.List;
import java.util.Map;

import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface BaseAccountDao{
	public List<Map<String,String>> findAccountList();
}
