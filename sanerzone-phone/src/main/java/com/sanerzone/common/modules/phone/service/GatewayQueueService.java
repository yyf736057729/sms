///**
// * Copyright &copy; 2015-2016 SanerZone All rights reserved.
// */
//package com.sanerzone.common.modules.phone.service;
//
//import com.sanerzone.common.modules.phone.dao.GatewayQueueDao;
//import com.sanerzone.common.modules.phone.dao.SmsPhoneDynamicDao;
//import com.sanerzone.common.modules.phone.entity.GatewayQueue;
//import com.sanerzone.common.modules.phone.entity.SmsPhoneDynamic;
//import com.sanerzone.common.modules.phone.utils.BlacklistUtils;
//import com.sanerzone.common.support.persistence.CrudService;
//import com.sanerzone.common.support.persistence.Page;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
///**
// * 通道队列查询Service
// * @author Carl
// * @version 2019-1-11
// */
//@Service
//@Transactional(readOnly = true)
//public class GatewayQueueService extends CrudService<GatewayQueueDao, GatewayQueue> {
//	/**
//	 * 查询所有通道队列查询
//	 * @return
//	 */
//	public List<GatewayQueue> findAll(){
//		return dao.findAll();
//	}
//
//
//
//}