package com.sanerzone.jmsg.listener;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.google.common.collect.Maps;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.modules.phone.entity.ContentManage;
import com.sanerzone.common.modules.smscenter.service.JmsgUserGatewayService;
import com.sanerzone.common.support.storedMap.BDBStoredMapFactoryImpl;
import com.sanerzone.common.support.utils.*;
import com.sanerzone.jmsg.entity.JmsgSmsSend;
import com.sanerzone.jmsg.util.BDBContentManageMapUtils;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import org.apache.commons.codec.binary.Base64;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.marre.sms.SmsAlphabet;
import org.marre.sms.SmsMsgClass;
import org.marre.sms.SmsTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.modules.phone.utils.BlacklistUtils;
import com.sanerzone.common.modules.phone.utils.PhoneUtils;
import com.sanerzone.common.modules.phone.utils.WhitelistUtils;
import com.sanerzone.common.modules.smscenter.entity.GatewayResult;
import com.sanerzone.common.modules.smscenter.utils.GatewayUtils;
import com.sanerzone.common.modules.smscenter.utils.KeywordsUtils;
import com.sanerzone.common.modules.smscenter.utils.RuleUtils;
import com.sanerzone.common.modules.smscenter.utils.SignUtils;
import com.sanerzone.common.support.mapper.JsonMapper;
import com.sanerzone.common.support.sequence.MsgId;
import com.sanerzone.jmsg.util.MessageExtUtil;
import com.sanerzone.smscenter.utils.MQUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;

@Service
public class SmsUMTListener implements /*MessageListenerConcurrently*/ MessageListener {

    private Logger logger = LoggerFactory.getLogger(SmsUMTListener.class);

    @Autowired
    public MQUtils mQUtils;


    @Autowired
    JmsgUserGatewayService jmsgUserGatewayService;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    public int findPayCount(String smsContent) {
        SmsTextMessage sms = null;
        if (com.sanerzone.common.support.utils.StringUtils.haswidthChar(smsContent)) {
            sms = new SmsTextMessage(smsContent, SmsAlphabet.UCS2, SmsMsgClass.CLASS_UNKNOWN);
        } else {
            sms = new SmsTextMessage(smsContent, SmsAlphabet.LATIN1, SmsMsgClass.CLASS_UNKNOWN);
        }
        return sms.getPdus().length;
    }

//	private GatewayResult gatewayMap(int signFlag, String groupId, String phoneType, String provinceId, String sign,
//			String userId, boolean smsYzmFlag) {
//		if (1 == signFlag) {
//			return GatewayUtils.getGateway(userId, groupId, phoneType, provinceId, sign, smsYzmFlag);
//		} else {
//			return GatewayUtils.getGateway(userId, groupId, phoneType, provinceId, smsYzmFlag);
//		}
//	}

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        SmsMtMessage smsRtMessage;
        JmsgSmsSend jmsgSmsSend;
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        String spNumber = "";

        try {
            logger.info("umt listener recv message: topic:{}, tags:{}, msgid:{}, key:{}", message.getTopic(), message.getTag(),
                    message.getMsgID(), message.getKey());

            long startTime = SystemClock.now();
            try {
                smsRtMessage = (SmsMtMessage) FstObjectSerializeUtil.read(message.getBody());
//                smsRtMessage = MessageExtUtil.convertMessageExt(SmsMtMessage.class, message);
                if (smsRtMessage == null) {
                    return Action.CommitMessage;
                }

                logger.info("CMPP模拟网关,开始处理放库发送: 任务ID:{}, 用户:{}, 手机号码：{}, 接收时间:{} ", smsRtMessage.getTaskid(),
                        smsRtMessage.getUserid(), smsRtMessage.getPhone(), startTime);

                String taskId = smsRtMessage.getTaskid();
                String userId = smsRtMessage.getUserid();
                String phones = smsRtMessage.getPhone();
                String smsContent = smsRtMessage.getMsgContent();
                String reportGaetewayId = smsRtMessage.getUserReportGateWayID();
                spNumber = smsRtMessage.getSpNumber();
                if (spNumber.isEmpty()) {
                    jmsgUserGatewayService.findAll();
                    spNumber = jmsgUserGatewayService.getUserGatewayByUserid(userId).getSpnumber();
                }
                System.out.println("扩展号:" + spNumber);
                String pushFlag = smsRtMessage.getUserReportNotify();

                String sendStatus = "T000";
                int usedFlag = AccountCacheUtils.getIntegerValue(userId, "usedFlag", 1);
                if (usedFlag == 0) { // 账户禁用发送功能
                    sendStatus = "F001";
                }


                String sign = SignUtils.get(smsContent);
                boolean usedSign = AccountCacheUtils.getIntegerValue(userId, "usedSign", 0) == 0;

                //签名策略为0且签名为空，则进行强制签名
                if (usedSign && StringUtils.isBlank(sign)) {
                    sign = AccountCacheUtils.getStringValue(userId, "forceSign", ""); // 强制签名
                    if (StringUtils.isNotBlank(sign)) {
                        smsContent = "【" + sign + "】" + smsContent;
                    } else {
                        sendStatus = "F0070";// 短信签名为空
                    }
                }

                // 如果有签名机制，则签名前置
                if (usedSign && StringUtils.equals(sendStatus, "T000")) {
                    smsContent = SignUtils.formatContent(smsContent);
                }

                int payCount = findPayCount(smsContent);// 扣费条数

                try {
                    Map<String, String> filResult = RuleUtils.filtrate(userId, smsContent);
                    if (!StringUtils.equals(filResult.get("result"), "T")) {
                        logger.info(filResult.get("result"));
                        // 内容匹配规格失败
                        if (StringUtils.isNotBlank(filResult.get("filCode"))) {
                            sendStatus = filResult.get("filCode");
                        } else {
                            sendStatus = "F009";
                        }
                    }
                } catch (Exception e) {
                    logger.error("匹配内容规则异常", e);
                    e.printStackTrace();
                }
                if ("1".equals(AccountCacheUtils.getStringValue(userId, "filterWordFlag", ""))) {// 过滤敏感词
                    String keywords = KeywordsUtils.keywords(smsContent.trim());
                    if (StringUtils.isNotBlank(keywords)) {
                        sendStatus = "F020";
                    }
                }

                // 发送通道
                String msgLevel = "NORMAL"; //默认通道
                String wapUrl = smsRtMessage.getWapUrl();
                String sendTag = StringUtils.equals(AccountCacheUtils.getStringValue(userId, "sendTag", "S"), "S") ? "S" : "B";
                // 用户发送属性 S单发 B群发
                boolean smsYzmFlag = SignUtils.isSecurityCode(smsContent); // 验证码
                if (smsYzmFlag) {
                    msgLevel = "HIGH";
                    sendTag = "S";
                } else if (StringUtils.equals(sendTag, "B")) { //不是S，就只能是B
                    msgLevel = "LOW";
                    wapUrl = "LOW";
                }

                String payType = AccountCacheUtils.getStringValue(userId, "payMode", "");// 扣费方式
                String[] phoneList = phones.split(",");
                //内容策略
                String contentManageOne = AccountCacheUtils.getStringValue(userId, "contentMgIdOne", "");// 内容控制策略1
                String contentManageTwo = AccountCacheUtils.getStringValue(userId, "contentMgIdTwo", "");// 内容控制策略2
                ContentManage contentManage1 = null;
                ContentManage contentManage2 = null;
                if (!contentManageOne.equals("0") && !contentManageOne.equals("")) {
                    contentManage1 = (ContentManage) EhCacheUtils.get("content", contentManageOne);
                }
                if (!contentManageTwo.equals("0") && !contentManageTwo.equals("")) {
                    contentManage2 = (ContentManage) EhCacheUtils.get("content", contentManageTwo);
                }
                SmsMtMessage mtMsg = null;
                for (String phone : phoneList) {
                    mtMsg = new SmsMtMessage();
                    if (!contentManageOne.equals("0") && !contentManageOne.equals("")) {
                        //内容控制策略1
                        String s = hashCodePhoneNumber(phone, smsContent, contentManage1.getTime(), contentManage1.getCount());
                        if (!s.equals("")) {
                            mtMsg.setSendStatus(s);
                        }
                    }
                    if (!contentManageTwo.equals("0") && !contentManageTwo.equals("")) {
                        //内容控制策略2
                        String s = hashCodePhoneNumber("", smsContent, contentManage2.getTime(), contentManage2.getCount());
                        if (!s.equals("")) {
                            mtMsg.setSendStatus(s);
                        }
                    }

                    mtMsg.setSendTime(System.currentTimeMillis());
                    mtMsg.setSendStatus(validateBlacklist(phone, userId, msgLevel, sendStatus));//校验黑名单
                    // 匹配号段
                    Map<String, String> phoneMap = PhoneUtils.get(phone);
                    if ((phoneMap == null) || (phoneMap.size() < 2)) {
                        mtMsg.setSendStatus("F0170");
                    } else {
                        mtMsg.setPhoneType(PhoneUtils.getPhoneType(phoneMap));
                        mtMsg.setCityCode(PhoneUtils.getCityCode(phoneMap));
                    }

                    // 匹配通道
                    if ((StringUtils.isBlank(mtMsg.getCityCode())) || (StringUtils.isBlank(mtMsg.getPhoneType()))) {
                        mtMsg.setSendStatus("F0170");
                    } else {

                        // 增加判断条件，如果发送状态为失败，则不需要匹配通道
                        if (!StringUtils.startsWith(mtMsg.getSendStatus(), "F")) {

                            int signFlag = AccountCacheUtils.getIntegerValue(userId, "signFlag", 0);
                            if (!usedSign) {//无签名机制
                                signFlag = 0;
                            }

                            logger.info(
                                    "网关匹配，参数 ：signFlag : {}  groupId : {}  phoneType : {}  cityCode : {}  sign : {}  userId : {}, phone:{}",
                                    signFlag, AccountCacheUtils.getStringValue(userId, "groupId", ""), mtMsg.getPhoneType(),
                                    mtMsg.getCityCode(), sign, userId, phone);
                            //用户id：userId
                            // AccountCacheUtils.getStringValue(userId, "groupId", "")：获取通道分组id
                            //mtMsg.getPhoneType()  运营商类型
                            //mtMsg.getCityCode().substring(0, 2)  省份代码
                            //sign 签名
                            //signFlag  是否有签名
                            //smsYzmFlag 是否是验证码短信
                            //sendTag 单发或者群发  B  S
                            GatewayResult gatewayResult = GatewayUtils.getGateway(userId, AccountCacheUtils.getStringValue(userId, "groupId", ""),
                                    mtMsg.getPhoneType(), mtMsg.getCityCode().substring(0, 2),
                                    sign, (1 == signFlag), smsYzmFlag, sendTag, smsContent);
                            //指定通道id ①
                            if (gatewayResult.isExists(mtMsg.getSendStatus())) {
                                mtMsg.setGateWayID(gatewayResult.getGatewayId());

                                String tdSpNumber = gatewayResult.getSpNumber();

                                // HTTP接口过来，传的是扩展号
                                if (StringUtils.startsWith(reportGaetewayId, "HTTP")) {
                                    if (AccountCacheUtils.getIntegerValue(userId, "signFlag", 0) == 0) {
                                        tdSpNumber = tdSpNumber + userId;
                                    }
                                    tdSpNumber = tdSpNumber + spNumber;
                                } else {
                                    // 扩展号
                                    if (AccountCacheUtils.getIntegerValue(userId, "cmppUserType", -1) == 0) {
                                        // 不校验签名且非签名优先
                                        if (signFlag == 0 && gatewayResult.getPolicy() != 2) {
                                            if ("YD".equals(mtMsg.getPhoneType())) {
                                                tdSpNumber = tdSpNumber
                                                        + AccountCacheUtils.getStringValue(userId, "extnumYd", "");
                                            } else if ("LT".equals(mtMsg.getPhoneType())) {
                                                tdSpNumber = tdSpNumber
                                                        + AccountCacheUtils.getStringValue(userId, "extnumLt", "");
                                            } else if ("DX".equals(mtMsg.getPhoneType())) {
                                                tdSpNumber = tdSpNumber
                                                        + AccountCacheUtils.getStringValue(userId, "extnumDx", "");
                                            }
                                        }

                                        tdSpNumber = tdSpNumber + spNumber;
                                        System.out.println("tdspnmber:" + tdSpNumber);

                                    } else {
                                        // 全号
                                        if ((StringUtils.isNotBlank(spNumber)) && (spNumber.startsWith(tdSpNumber))) {
                                            tdSpNumber = spNumber;
                                        }
                                    }
                                }

                                if (tdSpNumber.length() > 20) {
                                    tdSpNumber = tdSpNumber.substring(0, 20);
                                }

                                mtMsg.setSpNumber(tdSpNumber);
                            } else {
                                mtMsg.setSendStatus(gatewayResult.getErrorCode());
                            }
                        }
                    }

                    //校验发送频次
                    if (!StringUtils.startsWith(mtMsg.getSendStatus(), "F")) {
                        try {
                            // 可发送判断频次 0不过滤 1过滤
                            // 不在号码白名单 并且 当前用户需要过滤发送频次，则进行判断
                            if (!WhitelistUtils.isExist(phone)) {// 过滤发送频次

                                String pKey = encodePhoneNumber(phone);
                                //行业频控,一天50次,一个月300次
                                boolean sendLimitFlag = AccountCacheUtils.getIntegerValue(userId, "sendLimit", 1) == 1;
                                if (sendLimitFlag) {
                                    long dayNum = JedisClusterUtils.incr("d" + pKey);// 一天内发送次数+1
                                    if (dayNum == 1) {
                                        JedisClusterUtils.getJedisInstance().expire("d" + pKey, 86400);//+1 设置时间
                                    }
                                    if (dayNum > 50) {// 判断号码是否一天内发送50次以上 F3
                                        mtMsg.setSendStatus("F003");
                                    } else {
                                        long monNum = JedisClusterUtils.incr("m" + pKey);// 一月内发送次数+1
                                        if (monNum == 1) {
                                            JedisClusterUtils.getJedisInstance().expire("m" + pKey, 30 * 86400);//30天
                                        }
                                        if (monNum > 300) {// 判断号码是否一个月内发送300次以上 F4
                                            mtMsg.setSendStatus("F004");
                                        }
                                    }
                                }

                                //营销频控
                                boolean sendSemLimitFlag = AccountCacheUtils.getIntegerValue(userId, "marketingControl", 0) == 1;
                                if (sendSemLimitFlag) {
                                    long semNum = JedisClusterUtils.incr("sm" + pKey);// 一天内发送次数+1
                                    if (semNum == 1) {
                                        JedisClusterUtils.getJedisInstance().expire("sm" + pKey, 86400);//1天
                                    }
                                    if (semNum > 1) {// 判断号码10天内1次
                                        mtMsg.setSendStatus("F0032");
                                    }
                                }

                                //验证码频控
                                if (smsYzmFlag && (sendLimitFlag || sendSemLimitFlag) && !StringUtils.startsWith(mtMsg.getSendStatus(), "F")) {
                                    int yzmSendCount = AccountCacheUtils.getIntegerValue(userId, "yzmSendCount", 0);
                                    if (yzmSendCount > 0) {
                                        long yzmNum = JedisClusterUtils.incr(userId + "" + pKey);// 一天内发送次数+1
                                        if (yzmNum == 1) {
                                            JedisClusterUtils.getJedisInstance().expire(userId + "" + pKey, 86400);
                                        }
                                        if (yzmNum > yzmSendCount) {
                                            mtMsg.setSendStatus("F0031");
                                        }
                                    }
                                }

                            }
                        } catch (Exception e) {
                            logger.error("发送频次限制校验失败!", e);
                        }
                    }

                    // 组装发送消息
                    mtMsg.setId(new MsgId().toString());
                    mtMsg.setTaskid(taskId);
                    mtMsg.setUserid(userId);
                    mtMsg.setPayType(payType);
                    mtMsg.setCstmOrderID(smsRtMessage.getCstmOrderID());
                    mtMsg.setUserReportNotify(pushFlag);
                    mtMsg.setUserReportGateWayID(reportGaetewayId);
                    mtMsg.setMsgContent(smsContent);
                    mtMsg.setPhone(phone);
                    mtMsg.setSmsType("sms");
                    mtMsg.setContentSize(payCount);
                    mtMsg.setWapUrl(wapUrl);

                    Map<String, String> sMap = Maps.newHashMap();
                    //营销短信超限拦截进入审核区
                    if (mtMsg.getSendStatus().equals("F0032") || mtMsg.getSendStatus().equals("F0000")) {
                        sMap.put("status", "-1");
                        sMap.put("id", taskId);
                        sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsTaskDao.updateJmsgSmsTask", sMap);
                    }
                    // 判断通道优先级
                    String topic = "SMSMT";
                    //                    String appCode = "8900";// 应用代码 ①
                    String appCode = GatewayUtils.getAppCode(mtMsg.getGateWayID());// 应用代码
                    if (!StringUtils.startsWith(mtMsg.getSendStatus(), "F")) {
                        // 用户发送属性 0单发 1群发
                        boolean notBlank = StringUtils.isNotBlank(appCode);
                        if (notBlank) {
                            topic = topic + appCode + sendTag;
                        } else {
                            mtMsg.setSendStatus("F0074"); // 通道组不存在
                        }
                    }
                    if (!"SMSMT".equals(topic)) {
                        //-----------------------------
                        jmsgSmsSend = new JmsgSmsSend();
                        jmsgSmsSend.setId(mtMsg.getId());
                        jmsgSmsSend.setDataId("99");
                        jmsgSmsSend.setTaskId(mtMsg.getTaskid());//任务我ID
                        jmsgSmsSend.setPhone(mtMsg.getPhone());//手机号码
                        jmsgSmsSend.setSmsContent(mtMsg.getMsgContent());//短信内容
                        jmsgSmsSend.setSmsType(mtMsg.getContentSize() > 1 ? "2" : "1");//短信类型
                        jmsgSmsSend.setPayCount((int) mtMsg.getContentSize());//扣费条数
                        jmsgSmsSend.setUserId(mtMsg.getUserid());
                        jmsgSmsSend.setChannelCode(mtMsg.getGateWayID());//通道代码
                        jmsgSmsSend.setSpNumber(mtMsg.getSpNumber());//接入号--要写用户接入号
                        jmsgSmsSend.setPhoneType(mtMsg.getPhoneType());//运营商
                        jmsgSmsSend.setAreaCode(mtMsg.getCityCode());//省市代码
                        jmsgSmsSend.setPayType(mtMsg.getPayType());//扣费方式
                        jmsgSmsSend.setPayStatus("1");//扣费状态
                        jmsgSmsSend.setPushFlag(mtMsg.getUserReportNotify());//推送标识
                        jmsgSmsSend.setCompanyId("11");
                        jmsgSmsSend.setSendStatus(mtMsg.getSendStatus());//发送状态
                        jmsgSmsSend.setSubmitMode(mtMsg.getUserReportGateWayID());//提交方式 WEB,API
                        jmsgSmsSend.setTopic(message.getTag());//发送队列
                        jmsgSmsSend.setReportGatewayId(mtMsg.getUserReportGateWayID());
                        jmsgSmsSend.setMsgid(message.getMsgID());
                        jmsgSmsSend.setSendDatetime(new Date());
                        jmsgSmsSend.setCustomerOrderId(mtMsg.getCstmOrderID());
                        jmsgSmsSend.setTableName("jmsg_sms_send_" + TableNameUtil.getTableIndex(mtMsg.getId()));
                        sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
                        //-------------------------------
                    }


                    long startSendTime = SystemClock.now();
                    //mtMsg.setGateWayID("10113");//①
                    String msgid = mQUtils.sendSmsMT(topic, mtMsg.getGateWayID() + "_" + msgLevel, mtMsg.getId(), FstObjectSerializeUtil.write(mtMsg));
                    logger.info("topic:{},tag:{},appCode :{}, gateway：{}, phone:{}, key:{}, msgid:{}, sendTime:{}ms ", topic, mtMsg.getGateWayID() + "_" + msgLevel, appCode,
                            mtMsg.getGateWayID(), mtMsg.getPhone(), mtMsg.getId(), msgid,
                            (SystemClock.now() - startSendTime));
                    if (StringUtils.equals(msgid, "-1")) {
                        logger.error("网关：{}, appCode :{}, body:{}", mtMsg.getGateWayID(), appCode,
                                JsonMapper.toJsonString(mtMsg));
                    }
                }
                logger.info("UMT 模拟网关,发送结束: 任务ID:{}, 耗时:{}ms", smsRtMessage.getTaskid(), SystemClock.now() - startTime);
            } catch (Exception e) {
                logger.error("UMT异常", e);
            }
        } catch (Exception e) {
            logger.error("{}", e);
        } finally {
            if (sqlSession != null) {
                sqlSession.commit();
                sqlSession.close();
            }
        }
        return Action.CommitMessage;
    }

//    @Override
//    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//        SmsMtMessage smsRtMessage;
//        JmsgSmsSend jmsgSmsSend;
//        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
//        String spNumber="";
//
//        try {
//            for (MessageExt msg : msgs) {
//                logger.info("umt listener recv message: topic:{}, tags:{}, msgid:{}, key:{}", msg.getTopic(), msg.getTags(),
//                        msg.getMsgId(), msg.getKeys());
//
//                long startTime = SystemClock.now();
//                try {
//                    smsRtMessage = MessageExtUtil.convertMessageExt(SmsMtMessage.class, msg);
//                    if (smsRtMessage == null) {
//                        continue;
//                    }
//
//                    logger.info("CMPP模拟网关,开始处理放库发送: 任务ID:{}, 用户:{}, 手机号码：{}, 接收时间:{} ", smsRtMessage.getTaskid(),
//                            smsRtMessage.getUserid(), smsRtMessage.getPhone(), startTime);
//
//                    String taskId = smsRtMessage.getTaskid();
//                    String userId = smsRtMessage.getUserid();
//                    String phones = smsRtMessage.getPhone();
//                    String smsContent = smsRtMessage.getMsgContent();
//                    String reportGaetewayId = smsRtMessage.getUserReportGateWayID();
//                    spNumber = smsRtMessage.getSpNumber();
//					if(spNumber.isEmpty()){
//                        jmsgUserGatewayService.findAll();
//                        spNumber=jmsgUserGatewayService.getUserGatewayByUserid(userId).getSpnumber();
//                    }
//                    System.out.println("扩展号:"+spNumber);
//                    String pushFlag = smsRtMessage.getUserReportNotify();
//
//                    String sendStatus = "T000";
//                    int usedFlag = AccountCacheUtils.getIntegerValue(userId, "usedFlag", 1);
//                    if (usedFlag == 0) { // 账户禁用发送功能
//                        sendStatus = "F001";
//                    }
//
//
//                    String sign = SignUtils.get(smsContent);
//                    boolean usedSign = AccountCacheUtils.getIntegerValue(userId, "usedSign", 0) == 0;
//
//                    //签名策略为0且签名为空，则进行强制签名
//                    if (usedSign && StringUtils.isBlank(sign)) {
//                        sign = AccountCacheUtils.getStringValue(userId, "forceSign", ""); // 强制签名
//                        if (StringUtils.isNotBlank(sign)) {
//                            smsContent = "【" + sign + "】" + smsContent;
//                        } else {
//                            sendStatus = "F0070";// 短信签名为空
//                        }
//                    }
//
//                    // 如果有签名机制，则签名前置
//                    if (usedSign && StringUtils.equals(sendStatus, "T000")) {
//                        smsContent = SignUtils.formatContent(smsContent);
//                    }
//
//                    int payCount = findPayCount(smsContent);// 扣费条数
//
//                    try {
//                        Map<String, String> filResult = RuleUtils.filtrate(userId, smsContent);
//                        if (!StringUtils.equals(filResult.get("result"), "T")) {
//                            logger.info(filResult.get("result"));
//                            // 内容匹配规格失败
//                            if (StringUtils.isNotBlank(filResult.get("filCode"))) {
//                                sendStatus = filResult.get("filCode");
//                            } else {
//                                sendStatus = "F009";
//                            }
//                        }
//                    } catch (Exception e) {
//                        logger.error("匹配内容规则异常", e);
//                        e.printStackTrace();
//                    }
//                    if ("1".equals(AccountCacheUtils.getStringValue(userId, "filterWordFlag", ""))) {// 过滤敏感词
//                        String keywords = KeywordsUtils.keywords(smsContent.trim());
//                        if (StringUtils.isNotBlank(keywords)) {
//                            sendStatus = "F020";
//                        }
//                    }
//
//                    // 发送通道
//                    String msgLevel = "NORMAL"; //默认通道
//                    String wapUrl = smsRtMessage.getWapUrl();
//                    String sendTag = StringUtils.equals(AccountCacheUtils.getStringValue(userId, "sendTag", "S"), "S") ? "S" : "B";
//                    // 用户发送属性 S单发 B群发
//                    boolean smsYzmFlag = SignUtils.isSecurityCode(smsContent); // 验证码
//                    if (smsYzmFlag) {
//                        msgLevel = "HIGH";
//                        sendTag = "S";
//                    } else if (StringUtils.equals(sendTag, "B")) { //不是S，就只能是B
//                        msgLevel = "LOW";
//                        wapUrl = "LOW";
//                    }
//
//                    String payType = AccountCacheUtils.getStringValue(userId, "payMode", "");// 扣费方式
//                    String[] phoneList = phones.split(",");
//                    //内容策略
//                    String contentManageOne = AccountCacheUtils.getStringValue(userId, "contentMgIdOne", "");// 内容控制策略1
//                    String contentManageTwo = AccountCacheUtils.getStringValue(userId, "contentMgIdTwo", "");// 内容控制策略2
//                    ContentManage contentManage1 = null;
//                    ContentManage contentManage2 = null;
//                    if(!contentManageOne.equals("0") && !contentManageOne.equals("")){
//                        contentManage1 = (ContentManage)EhCacheUtils.get("content", contentManageOne);
//                    }
//                    if(!contentManageTwo.equals("0") && !contentManageTwo.equals("")){
//                        contentManage2 = (ContentManage)EhCacheUtils.get("content", contentManageTwo);
//                    }
//                    SmsMtMessage mtMsg = null;
//                    for (String phone : phoneList) {
//                        mtMsg = new SmsMtMessage();
//                        if(!contentManageOne.equals("0") && !contentManageOne.equals("")){
//                            //内容控制策略1
//                            String s = hashCodePhoneNumber(phone, smsContent,contentManage1.getTime(),contentManage1.getCount());
//                            if(!s.equals("")){
//                                mtMsg.setSendStatus(s);
//                            }
//                        }
//                        if(!contentManageTwo.equals("0") && !contentManageTwo.equals("")){
//                            //内容控制策略2
//                            String s = hashCodePhoneNumber("", smsContent,contentManage2.getTime(),contentManage2.getCount());
//                            if(!s.equals("")){
//                                mtMsg.setSendStatus(s);
//                            }
//                        }
//
//                        mtMsg.setSendTime(System.currentTimeMillis());
//                        mtMsg.setSendStatus(validateBlacklist(phone, userId, msgLevel, sendStatus));//校验黑名单
//                        // 匹配号段
//                        Map<String, String> phoneMap = PhoneUtils.get(phone);
//                        if ((phoneMap == null) || (phoneMap.size() < 2)) {
//                            mtMsg.setSendStatus("F0170");
//                        } else {
//                            mtMsg.setPhoneType(PhoneUtils.getPhoneType(phoneMap));
//                            mtMsg.setCityCode(PhoneUtils.getCityCode(phoneMap));
//                        }
//
//                        // 匹配通道
//                        if ((StringUtils.isBlank(mtMsg.getCityCode())) || (StringUtils.isBlank(mtMsg.getPhoneType()))) {
//                            mtMsg.setSendStatus("F0170");
//                        } else {
//
//                            // 增加判断条件，如果发送状态为失败，则不需要匹配通道
//                            if (!StringUtils.startsWith(mtMsg.getSendStatus(), "F")) {
//
//                                int signFlag = AccountCacheUtils.getIntegerValue(userId, "signFlag", 0);
//                                if (!usedSign) {//无签名机制
//                                    signFlag = 0;
//                                }
//
//                                logger.info(
//                                        "网关匹配，参数 ：signFlag : {}  groupId : {}  phoneType : {}  cityCode : {}  sign : {}  userId : {}, phone:{}",
//                                        signFlag, AccountCacheUtils.getStringValue(userId, "groupId", ""), mtMsg.getPhoneType(),
//                                        mtMsg.getCityCode(), sign, userId, phone);
//                                //用户id：userId
//                                // AccountCacheUtils.getStringValue(userId, "groupId", "")：获取通道分组id
//                                //mtMsg.getPhoneType()  运营商类型
//                                //mtMsg.getCityCode().substring(0, 2)  省份代码
//                                //sign 签名
//                                //signFlag  是否有签名
//                                //smsYzmFlag 是否是验证码短信
//                                //sendTag 单发或者群发  B  S
//                                GatewayResult gatewayResult = GatewayUtils.getGateway(userId, AccountCacheUtils.getStringValue(userId, "groupId", ""),
//                                        mtMsg.getPhoneType(), mtMsg.getCityCode().substring(0, 2),
//                                        sign, (1 == signFlag), smsYzmFlag,sendTag,smsContent);
//                                //指定通道id ①
//                                if (gatewayResult.isExists(mtMsg.getSendStatus())) {
//                                    mtMsg.setGateWayID(gatewayResult.getGatewayId());
//
//                                    String tdSpNumber = gatewayResult.getSpNumber();
//
//                                    // HTTP接口过来，传的是扩展号
//                                    if (StringUtils.startsWith(reportGaetewayId, "HTTP")) {
//                                        if (AccountCacheUtils.getIntegerValue(userId, "signFlag", 0) == 0) {
//                                            tdSpNumber = tdSpNumber + userId;
//                                        }
//                                        tdSpNumber = tdSpNumber + spNumber;
//                                    } else {
//                                        // 扩展号
//                                        if (AccountCacheUtils.getIntegerValue(userId, "cmppUserType", -1) == 0) {
//                                            // 不校验签名且非签名优先
//                                            if (signFlag == 0 && gatewayResult.getPolicy() != 2) {
//                                                if ("YD".equals(mtMsg.getPhoneType())) {
//                                                    tdSpNumber = tdSpNumber
//                                                            + AccountCacheUtils.getStringValue(userId, "extnumYd", "");
//                                                } else if ("LT".equals(mtMsg.getPhoneType())) {
//                                                    tdSpNumber = tdSpNumber
//                                                            + AccountCacheUtils.getStringValue(userId, "extnumLt", "");
//                                                } else if ("DX".equals(mtMsg.getPhoneType())) {
//                                                    tdSpNumber = tdSpNumber
//                                                            + AccountCacheUtils.getStringValue(userId, "extnumDx", "");
//                                                }
//                                            }
//
//                                            tdSpNumber = tdSpNumber + spNumber;
//                                            System.out.println("tdspnmber:"+tdSpNumber);
//
//                                        } else {
//                                            // 全号
//                                            if ((StringUtils.isNotBlank(spNumber)) && (spNumber.startsWith(tdSpNumber))) {
//                                                tdSpNumber = spNumber;
//                                            }
//                                        }
//                                    }
//
//                                    if (tdSpNumber.length() > 20) {
//                                        tdSpNumber = tdSpNumber.substring(0, 20);
//                                    }
//
//                                    mtMsg.setSpNumber(tdSpNumber);
//                                } else {
//                                    mtMsg.setSendStatus(gatewayResult.getErrorCode());
//                                }
//                            }
//                        }
//
//                        //校验发送频次
//                        if (!StringUtils.startsWith(mtMsg.getSendStatus(), "F")) {
//                            try {
//                                // 可发送判断频次 0不过滤 1过滤
//                                // 不在号码白名单 并且 当前用户需要过滤发送频次，则进行判断
//                                if (!WhitelistUtils.isExist(phone)) {// 过滤发送频次
//
//                                    String pKey = encodePhoneNumber(phone);
//                                    //行业频控,一天50次,一个月300次
//                                    boolean sendLimitFlag = AccountCacheUtils.getIntegerValue(userId, "sendLimit", 1) == 1;
//                                    if (sendLimitFlag) {
//                                        long dayNum = JedisClusterUtils.incr("d" + pKey);// 一天内发送次数+1
//                                        if (dayNum == 1) {
//                                            JedisClusterUtils.getJedisInstance().expire("d" + pKey, 86400);//+1 设置时间
//                                        }
//                                        if (dayNum > 50) {// 判断号码是否一天内发送50次以上 F3
//                                            mtMsg.setSendStatus("F003");
//                                        } else {
//                                            long monNum = JedisClusterUtils.incr("m" + pKey);// 一月内发送次数+1
//                                            if (monNum == 1) {
//                                                JedisClusterUtils.getJedisInstance().expire("m" + pKey, 30 * 86400);//30天
//                                            }
//                                            if (monNum > 300) {// 判断号码是否一个月内发送300次以上 F4
//                                                mtMsg.setSendStatus("F004");
//                                            }
//                                        }
//                                    }
//
//                                    //营销频控
//                                    boolean sendSemLimitFlag = AccountCacheUtils.getIntegerValue(userId, "marketingControl", 0) == 1;
//                                    if (sendSemLimitFlag) {
//                                        long semNum = JedisClusterUtils.incr("sm" + pKey);// 一天内发送次数+1
//                                        if (semNum == 1) {
//                                            JedisClusterUtils.getJedisInstance().expire("sm" + pKey,  86400);//1天
//                                        }
//                                        if (semNum > 1) {// 判断号码10天内1次
//                                            mtMsg.setSendStatus("F0032");
//                                        }
//                                    }
//
//                                    //验证码频控
//                                    if (smsYzmFlag && (sendLimitFlag || sendSemLimitFlag) && !StringUtils.startsWith(mtMsg.getSendStatus(), "F")) {
//                                        int yzmSendCount = AccountCacheUtils.getIntegerValue(userId, "yzmSendCount", 0);
//                                        if (yzmSendCount > 0) {
//                                            long yzmNum = JedisClusterUtils.incr(userId + "" + pKey);// 一天内发送次数+1
//                                            if (yzmNum == 1) {
//                                                JedisClusterUtils.getJedisInstance().expire(userId + "" + pKey, 86400);
//                                            }
//                                            if (yzmNum > yzmSendCount) {
//                                                mtMsg.setSendStatus("F0031");
//                                            }
//                                        }
//                                    }
//
//                                }
//                            } catch (Exception e) {
//                                logger.error("发送频次限制校验失败!", e);
//                            }
//                        }
//
//                        // 组装发送消息
//                        mtMsg.setId(new MsgId().toString());
//                        mtMsg.setTaskid(taskId);
//                        mtMsg.setUserid(userId);
//                        mtMsg.setPayType(payType);
//                        mtMsg.setCstmOrderID(smsRtMessage.getCstmOrderID());
//                        mtMsg.setUserReportNotify(pushFlag);
//                        mtMsg.setUserReportGateWayID(reportGaetewayId);
//                        mtMsg.setMsgContent(smsContent);
//                        mtMsg.setPhone(phone);
//                        mtMsg.setSmsType("sms");
//                        mtMsg.setContentSize(payCount);
//                        mtMsg.setWapUrl(wapUrl);
//
//                        Map<String, String> sMap = Maps.newHashMap();
//                        //营销短信超限拦截进入审核区
//                        if(mtMsg.getSendStatus().equals("F0032") || mtMsg.getSendStatus().equals("F0000")){
//                            sMap.put("status", "-1");
//                            sMap.put("id", taskId);
//                            sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsTaskDao.updateJmsgSmsTask", sMap);
//                        }
//                        // 判断通道优先级
//                        String topic = "SMSMT";
//                        //                    String appCode = "8900";// 应用代码 ①
//                        String appCode = GatewayUtils.getAppCode(mtMsg.getGateWayID());// 应用代码
//                        if (!StringUtils.startsWith(mtMsg.getSendStatus(), "F")) {
//                            // 用户发送属性 0单发 1群发
//                            boolean notBlank = StringUtils.isNotBlank(appCode);
//                            if (notBlank) {
//                                topic = topic + appCode + sendTag;
//                            } else {
//                                mtMsg.setSendStatus("F0074"); // 通道组不存在
//                            }
//                        }
//                        if(!"SMSMT".equals(topic)){
//                            //-----------------------------
//                            jmsgSmsSend = new JmsgSmsSend();
//                            jmsgSmsSend.setId(mtMsg.getId());
//                            jmsgSmsSend.setDataId("99");
//                            jmsgSmsSend.setTaskId(mtMsg.getTaskid());//任务我ID
//                            jmsgSmsSend.setPhone(mtMsg.getPhone());//手机号码
//                            jmsgSmsSend.setSmsContent(mtMsg.getMsgContent());//短信内容
//                            jmsgSmsSend.setSmsType(mtMsg.getContentSize() > 1 ? "2" : "1");//短信类型
//                            jmsgSmsSend.setPayCount((int) mtMsg.getContentSize());//扣费条数
//                            jmsgSmsSend.setUserId(mtMsg.getUserid());
//                            jmsgSmsSend.setChannelCode(mtMsg.getGateWayID());//通道代码
//                            jmsgSmsSend.setSpNumber(mtMsg.getSpNumber());//接入号--要写用户接入号
//                            jmsgSmsSend.setPhoneType(mtMsg.getPhoneType());//运营商
//                            jmsgSmsSend.setAreaCode(mtMsg.getCityCode());//省市代码
//                            jmsgSmsSend.setPayType(mtMsg.getPayType());//扣费方式
//                            jmsgSmsSend.setPayStatus("1");//扣费状态
//                            jmsgSmsSend.setPushFlag(mtMsg.getUserReportNotify());//推送标识
//                            jmsgSmsSend.setCompanyId("11");
//                            jmsgSmsSend.setSendStatus(mtMsg.getSendStatus());//发送状态
//                            jmsgSmsSend.setSubmitMode(mtMsg.getUserReportGateWayID());//提交方式 WEB,API
//                            jmsgSmsSend.setTopic(msg.getTags());//发送队列
//                            jmsgSmsSend.setReportGatewayId(mtMsg.getUserReportGateWayID());
//                            jmsgSmsSend.setMsgid(msg.getMsgId());
//                            jmsgSmsSend.setSendDatetime(new Date());
//                            jmsgSmsSend.setCustomerOrderId(mtMsg.getCstmOrderID());
//                            jmsgSmsSend.setTableName("jmsg_sms_send_" + TableNameUtil.getTableIndex(mtMsg.getId()));
//                            sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
//                            sqlSession.commit();
//                            //-------------------------------
//                        }
//
//
//                        long startSendTime = SystemClock.now();
//                        //mtMsg.setGateWayID("10113");//①
//                        String msgid = mQUtils.sendSmsMT(topic, mtMsg.getGateWayID() + "_" + msgLevel, mtMsg.getId(), FstObjectSerializeUtil.write(mtMsg));
//                        logger.info("topic:{},tag:{},appCode :{}, gateway：{}, phone:{}, key:{}, msgid:{}, sendTime:{}ms ", topic, mtMsg.getGateWayID() + "_" + msgLevel,appCode,
//                                mtMsg.getGateWayID(), mtMsg.getPhone(), mtMsg.getId(), msgid,
//                                (SystemClock.now() - startSendTime));
//                        if (StringUtils.equals(msgid, "-1")) {
//                            logger.error("网关：{}, appCode :{}, body:{}", mtMsg.getGateWayID(), appCode,
//                                    JsonMapper.toJsonString(mtMsg));
//                        }
//                    }
//                    logger.info("UMT 模拟网关,发送结束: 任务ID:{}, 耗时:{}ms", smsRtMessage.getTaskid(), SystemClock.now() - startTime);
//                } catch (Exception e) {
//                    logger.error("UMT异常", e);
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error("{}", e);
//        } finally {
//            if (sqlSession != null) {
//                sqlSession.close();
//            }
//        }
//        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//
//    }

    public static String encodePhoneNumber(String number) {
        int encnum = Integer.parseInt(number.substring(2));
        int prefix = (number.charAt(1) - '2') / 2;   // '3'=>0, '5'=>1, '[67]'=>2, '8'=>3
        encnum |= (prefix <<= 30);
        final byte[] raw = new byte[4];
        raw[0] = (byte) (encnum >>> 24);
        raw[1] = (byte) (encnum >>> 16);
        raw[2] = (byte) (encnum >>> 8);
        raw[3] = (byte) encnum;
        return Base64.encodeBase64URLSafeString(raw);
    }

    /**
     * 校验黑名单
     * 1:校验 0:不校验
     *
     * @param phone      手机号码
     * @param userId     用户ID
     * @param level      通道级别
     * @param sendStatus 发送状态
     * @return
     */
    private String validateBlacklist(String phone, String userId, String level, String sendStatus) {

        if (StringUtils.startsWith(sendStatus, "T")) {
            //校验验证码黑名单
            if (AccountCacheUtils.getIntegerValue(userId, "yzmBlacklistFlag", 0) == 1
                    && BlacklistUtils.isExistYzmBlackList(phone)) {
                return "F002";
            }

            // 校验黑名单，验证码不需要校验
            if (!StringUtils.equals(level, "HIGH")) {
                if (AccountCacheUtils.getIntegerValue(userId, "sysBlacklistFlag", 0) == 1
                        && BlacklistUtils.isExistSysBlackList(phone)) {
                    // 判断是否系统黑名单
                    return "F0081";
                }

                if (AccountCacheUtils.getIntegerValue(userId, "userBlacklistFlag", 0) == 1
                        && BlacklistUtils.isExistBlackList(phone)) {
                    // 判断是否群发黑名单
                    return "F0082";
                }

                if (AccountCacheUtils.getIntegerValue(userId, "marketBlacklistFlag", 0) == 1
                        && BlacklistUtils.isExistMarketBlackList(phone)) {
                    // 判断是否营销黑名单
                    return "F008";
                }
            }
        }
        return sendStatus;
    }


    /**
     * 手机号和短信内容的hash值存储计数
     * @return
     */
    public String hashCodePhoneNumber(String phone,String smsContent,Long time,Long count){
        String str ="";
        int hash;
        if(phone.equals("")){
            hash = Objects.hash(smsContent);
        }else{
            hash = Objects.hash(phone, smsContent);
        }
        Serializable serializable = BDBContentManageMapUtils.map.get("hash_" + hash);
        if(serializable != null){
            if(serializable instanceof String){
                String phoneNumbers = (String)serializable;
                String[] s = phoneNumbers.split("_");
                long cache_time =  Long.parseLong(s[1]);//要存在的分钟数
                long current = Long.parseLong(s[2]);//当前时间的毫秒数
                long c = System.currentTimeMillis();
                boolean diff = (cache_time*1000*60)>(c-current);
                if(diff){
                    int phoneNumber = Integer.parseInt(s[0]);
                    if(phoneNumber>=count){
                        str="F0000";//暂时使用F0000
                    }
                }else{
                    BDBContentManageMapUtils.map.remove("hash_" + hash);
                }
            }
        }
        boolean b = BDBContentManageMapUtils.map.containsKey("hash_" + hash);
        if(b){
            Serializable sy = BDBContentManageMapUtils.map.get("hash_" + hash);
            if(serializable instanceof String){
                //生成value策略
                String s = (String) sy;
                String[] s1 = s.split("_");
                int coun = Integer.parseInt(s1[0])+1;
                //要存在的时间
                long cache_time = time*60000;
                //当前时间
                long current = System.currentTimeMillis();
                BDBContentManageMapUtils.map.put("hash_" + hash,coun+"_"+cache_time+"_"+current);
            }
        }else{
            //生成value策略
            int coun = 1;
            //要存在的时间
            long cache_time = time*60000;
            //当前时间
            long current = System.currentTimeMillis();
            BDBContentManageMapUtils.map.put("hash_" + hash,coun+"_"+cache_time+"_"+current);
        }
        return str;
    }

    public static void main(String[] args) {
        for(int i = 0;i<1000000;i++){
            System.out.println(new MsgId().toString());
        }
    }

}
