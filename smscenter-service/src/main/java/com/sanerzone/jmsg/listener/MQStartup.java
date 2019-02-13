package com.sanerzone.jmsg.listener;

import com.aliyun.openservices.ons.api.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.sanerzone.common.modules.smscenter.utils.JmsgCacheKeys;
import com.sanerzone.common.support.config.Global;
import com.sanerzone.common.support.rocketmq.MQCustomerFactory;

@Service
@Lazy(false)
public class MQStartup {
	Logger logger = LoggerFactory.getLogger(MQStartup.class);

	@Autowired
	private SmsReportStatusMsgListener smsReportStatusMsgListener;

	@Autowired
	private SmsReportMsgListener smsReportMsgListener;

	@Autowired
	private SmsPushMsgListener smsPushMsgListener;

	@Autowired
	private SmsSubmitRespMsgListener smsSubmitRespMsgListener;

	@Autowired
	private SmsUMTListener smsUMTListener;
	
	@Autowired
	private SmsSignListener smsSignListener;
	
	@Autowired
	private SmsPushResultListener smsPushResultListener;


	@Autowired
	private SmsDeliverMsgListener smsDeliverMsgListener;

	@Autowired
	private SmsReportStatusFromSubmitListener smsReportStatusFromSubmitListener;

	@Autowired
	private SmsMTListener smsMTListener;

	@Autowired
	private OnCacheJmsgGatewayGroupListener onCacheJmsgGatewayGroupListener;

	@Autowired
	private OnCacheJmsgPhoneInfoListener onCacheJmsgPhoneInfoListener;
	
	private String appCode = Global.getConfig("appcode");

	public void init(int group) {
		smsUtConsumer(); 		// SMSURV1队列中http推送状态
		smsMOConsumer();		// SMSMOV1

		//同步
		smsSendRtConsumer();    // 更新发送表reportStatus
		smsSendRtFromSubmitConsumer(); // 更新发送表 提交网关状态

		smsMTConsumerSmsmt();	 //普通队列入库
//			smsMTHIGHConsumer(); //高优队列入库
		smsSubmitConsumer(); //网关提交状态入库
		smsRtConsumer(); 	 //网关状态回执入库-并提交到SMSURV1队列

		smsUMTConsumer();	 //发送请求处理

		smsSignConsumer();	 //发送请求处理
		smsPushResultConsumer();	 // SMSMOV1
		onCacheJmsgGatewayGroup();	//通道策略分组缓存
		onCache_jmsgPhoneInfo();  //号段缓存
//		switch (group) {
//		case 1://同步状态及推送状态
//
//			break;
//		case 2://入库
//
//			break;
//		case 3://业务处理
//
//			break;
//		case 4://签名统计
//
//			break;
//		default:
//			System.out.println("无");
//			break;
//		}
	}

	/**
	 * 号段缓存
	 * @
	 */
	private void onCache_jmsgPhoneInfo() {
		Consumer smsStatusConsumer = MQCustomerFactory
				.getPushConsumer("onCacheJmsgPhoneInfo-StorageGroup");
		try {
//			smsStatusConsumer.setConsumeMessageBatchMaxSize(32);
//			smsStatusConsumer.setInstanceName(appCode+"onCacheJmsgPhoneInfo_StorageConsumer");
			smsStatusConsumer.subscribe("onCacheJmsgPhoneInfo", "*",onCacheJmsgPhoneInfoListener);
//			smsStatusConsumer.setMessageListener(onCacheJmsgPhoneInfoListener);
			smsStatusConsumer.start();
			logger.info("号段缓存启动完成,Topic: onCacheJmsgPhoneInfo");
		} catch (Exception e) {
			logger.error("号段缓存处理程序异常,Topic: onCacheJmsgPhoneInfo", e);
		}
	}

	/**
	 * 通道策略分组缓存
	 * @
	 */
	private void onCacheJmsgGatewayGroup() {
		Consumer smsStatusConsumer = MQCustomerFactory
				.getPushConsumer("onCacheJmsgGatewayGroup-StorageGroup");
		try {
//			smsStatusConsumer.setConsumeMessageBatchMaxSize(32);
//			smsStatusConsumer.setInstanceName(appCode+"onCacheJmsgGatewayGroup_StorageConsumer");
			smsStatusConsumer.subscribe("onCacheJmsgGatewayGroup", "*",onCacheJmsgGatewayGroupListener);
//			smsStatusConsumer.setMessageListener(onCacheJmsgGatewayGroupListener);
			smsStatusConsumer.start();
			logger.info("通道策略分组缓存启动完成,Topic: onCacheJmsgGatewayGroup");
		} catch (Exception e) {
			logger.error("通道策略分组缓存处理程序异常,Topic: onCacheJmsgGatewayGroup", e);
		}
	}

	/**
	 * 短信普通队列入库-SMSMT_NORMAL（可集群消费，分组名不变，实例名加应用标识appCode）
	 * @
	 */
	private void smsMTConsumerSmsmt() {
		Consumer smsStatusConsumer = MQCustomerFactory
				.getPushConsumer("SMSMT-StorageGroup");
		try {
//			smsStatusConsumer.setConsumeMessageBatchMaxSize(32);
//			smsStatusConsumer.setInstanceName(appCode+"SMSMT_StorageConsumer");
			smsStatusConsumer.subscribe("SMSMT", "*",smsMTListener);
//			smsStatusConsumer.setMessageListener(smsMTListener);
			smsStatusConsumer.start();
			logger.info("短信普通队列入库处理程序已启动,Topic: SMSMT");
		} catch (Exception e) {
			logger.error("短信普通队列入库处理程序异常,Topic: SMSMT", e);
		}
	}


	/**
	 * 提交网关状态入库处理-SMSSRV1（可集群消费，分组名不变，实例名加应用标识appCode）
	 */
	private void smsSubmitConsumer() {
		Consumer smsStatusConsumer = MQCustomerFactory
				.getPushConsumer("SMSSRV1-StorageGroup");
		try {
//			smsStatusConsumer.setConsumeMessageBatchMaxSize(32);
//			smsStatusConsumer.setInstanceName(appCode+"SMSSRV1StorageConsumer");
			smsStatusConsumer.subscribe(JmsgCacheKeys.getSmsSubmit(), "*",smsSubmitRespMsgListener);
//			smsStatusConsumer.setMessageListener(smsSubmitRespMsgListener);
			smsStatusConsumer.start();
			logger.info("提交网关状态入库程序已启动,Topic:{}", JmsgCacheKeys.getSmsSubmit());
		} catch (Exception e) {
			logger.error("提交网关状态入库程序异常,Topic:" + JmsgCacheKeys.getSmsSubmit(), e);
		}
	}
	
	/**
	 * 网关状态报告入库-SMSRTV1
	 */
	private void smsRtConsumer() {
		Consumer smsRtConsumer = MQCustomerFactory
				.getPushConsumer("SMSRTV1-StorageGroup");
		try {
//			smsRtConsumer.setConsumeMessageBatchMaxSize(32);
//			smsRtConsumer.setInstanceName(appCode+"SMSRTV1StorageConsumer");
			//设置广播消费
//			smsRtConsumer.setMessageModel(MessageModel.BROADCASTING);
			smsRtConsumer.subscribe(JmsgCacheKeys.getReportTopic(), "*",smsReportMsgListener);
//			smsRtConsumer.setMessageListener(smsReportMsgListener);
			smsRtConsumer.start();
			logger.info("网关状态报告入库程序已启动,Topic: {}", JmsgCacheKeys.getReportTopic());
		} catch (Exception e) {
			logger.error("网关状态报告入库程序异常,Topic:" + JmsgCacheKeys.getReportTopic(), e);
		}
	}

	/**
	 * 提交网关失败状态同步到sms_send表,成功状态丢弃，避免多次操作数据库-SMSSRV1
	 */
	private void smsSendRtFromSubmitConsumer() {
		Consumer smsStatusConsumer = MQCustomerFactory
				.getPushConsumer("SMSSRV1-SyncGroup");
		try {
//			smsStatusConsumer.setConsumeMessageBatchMaxSize(32);
//			smsStatusConsumer.setInstanceName(appCode+"SMSSRV1SyncConsumer");
			smsStatusConsumer.subscribe(JmsgCacheKeys.getSmsSubmit(), "*",smsReportStatusFromSubmitListener);
//			smsStatusConsumer
//					.setMessageListener(smsReportStatusFromSubmitListener);
			smsStatusConsumer.start();
			logger.info("网关提交状态同步程序已启动,Topic:{}", JmsgCacheKeys.getSmsSubmit());
		} catch (Exception e) {
			logger.error("网关提交同步程序异常,Topic:" + JmsgCacheKeys.getSmsSubmit(), e);
		}
	}
	

	/**
	 * 网关状态报告同步到sms_send表-SMSRTV1
	 */
	private void smsSendRtConsumer() {
		Consumer smsSentRtConsumer = MQCustomerFactory
				.getPushConsumer("SMSRTV1-SyncGroup");
		try {
//			smsSentRtConsumer.setInstanceName(appCode+"SMSRTV1SyncConsumer");
			smsSentRtConsumer.subscribe(JmsgCacheKeys.getReportTopic(), "*",smsReportStatusMsgListener);
			//设置广播消费
//			smsSentRtConsumer.setMessageModel(MessageModel.BROADCASTING);
//			smsSentRtConsumer.setMessageListener(smsReportStatusMsgListener);
			smsSentRtConsumer.start();
			logger.info("网关状态报告同步程序已启动,Topic:{}", JmsgCacheKeys.getReportTopic());
		} catch (Exception e) {
			logger.error("网关状态报告同步程序异常,Topic:" + JmsgCacheKeys.getReportTopic(), e);
		}
	}

	/**
	 * 用户状态通知-SMSURV1
	 */
	private void smsUtConsumer() {
		Consumer smsUtConsumer = MQCustomerFactory
				.getPushConsumer("SMSURV1-UserNotifyGroup");
		try {
//			smsUtConsumer.setConsumeMessageBatchMaxSize(32);
//			smsUtConsumer.setInstanceName(appCode+"SMSURV1UserNotifyConsumer");// 只推送Http接口
			smsUtConsumer.subscribe(JmsgCacheKeys.getPushTopic(), "HTTP",smsPushMsgListener);
//			smsUtConsumer.setMessageListener(smsPushMsgListener);
			smsUtConsumer.start();
			logger.info("用户状态通知程序已启动,Topic:{}", JmsgCacheKeys.getPushTopic());
		} catch (Exception e) {
			logger.error("用户状态通知程序异常,Topic:" + JmsgCacheKeys.getPushTopic(), e);
		}
	}

	/**
	 * 用户下行处理程序-SMSUMTV1
	 */
	public void smsUMTConsumer() {
		Consumer smsUMTConsumer = MQCustomerFactory
				.getPushConsumer("SMSUMTV1-ServiceGroup");
		try {
//			smsUMTConsumer.setConsumeMessageBatchMaxSize(32);
//			smsUMTConsumer.setInstanceName(appCode+"SMSUMTV1ServiceConsumer");
			smsUMTConsumer.subscribe(JmsgCacheKeys.getSmsUMTTopic(), "*",smsUMTListener);
//			smsUMTConsumer.setMessageModel(MessageModel.CLUSTERING);
//			smsUMTConsumer.setMessageListener(smsUMTListener);
			smsUMTConsumer.start();
			logger.info("短信UMT消费启动成功, Topic:{}", JmsgCacheKeys.getSmsUMTTopic());
		} catch (Exception e) {
			logger.error("短信UMT消费启动异常, Topic:" + JmsgCacheKeys.getSmsUMTTopic(), e);
		}
	}

	/**
	 * 用户上行处理程序-SMSMOV1
	 */
	public void smsMOConsumer() {
		Consumer smsMOConsumer = MQCustomerFactory
				.getPushConsumer("SMSMOV1-ServiceGroup");
		try {
//			smsMOConsumer.setInstanceName(appCode+"SMSMOV1ServiceConsumer");
			smsMOConsumer.subscribe(JmsgCacheKeys.getMOTopic(), "*",smsDeliverMsgListener);
//			smsMOConsumer.setMessageListener(smsDeliverMsgListener);
			smsMOConsumer.start();
			logger.info("短信上行服务启动成功, Topic:{}", JmsgCacheKeys.getMOTopic());
		} catch (Exception e) {
			logger.error("短信上行服务启动异常, Topic:" + JmsgCacheKeys.getMOTopic(), e);
		}
	}
	
	/**
	 * 用户签名处理程序-SMSUMTV1
	 */
	public void smsSignConsumer() {
		Consumer smsSignConsumer = MQCustomerFactory
				.getPushConsumer("SMSUMTV1-SignGroup");
		try {
//			smsSignConsumer.setConsumeMessageBatchMaxSize(32);
//			smsSignConsumer.setInstanceName(appCode+"SMSUMTV1SignConsumer");
			smsSignConsumer.subscribe(JmsgCacheKeys.getSmsUMTTopic(), "*",smsSignListener);
//			smsSignConsumer.setMessageModel(MessageModel.CLUSTERING);
//			smsSignConsumer.setMessageListener(smsSignListener);
			smsSignConsumer.start();
			logger.info("用户签名统计启动成功, Topic:{}", JmsgCacheKeys.getSmsUMTTopic());
		} catch (Exception e) {
			logger.error("用户签名统计启动异常, Topic:" + JmsgCacheKeys.getSmsUMTTopic(), e);
		}
	}
	
	/**
	 * 推送结果-SMSPRV1
	 */
	public void smsPushResultConsumer() {
		Consumer smsPushResultConsumer = MQCustomerFactory
				.getPushConsumer("SMSPRV1-pushResult");
		try {
//			smsPushResultConsumer.setConsumeMessageBatchMaxSize(32);
//			smsPushResultConsumer.setInstanceName(appCode+"SMSPRV1PushResultConsumer");
			smsPushResultConsumer.subscribe("SMSPRV1", "*",smsPushResultListener);
//			smsPushResultConsumer.setMessageModel(MessageModel.CLUSTERING);
//			smsPushResultConsumer.setMessageListener(smsPushResultListener);
			smsPushResultConsumer.start();
			logger.info("用户推送结果监听启动成功, Topic:{}", "SMSPRV1");
		} catch (Exception e) {
			logger.error("用户推送结果监听启动异常, Topic:" + "SMSPRV1", e);
		}
	}

}
