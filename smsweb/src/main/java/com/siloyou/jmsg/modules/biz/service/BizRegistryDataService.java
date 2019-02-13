/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.biz.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.mapper.JsonMapper;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.jmsg.modules.biz.dao.BizRegistryDataDao;
import com.siloyou.jmsg.modules.biz.entity.BizRegistryData;

/**
 * 活动登录短信发送记录Service
 * 
 * @author huangjie
 * @version 2017-07-20
 */
@Service
@Transactional(readOnly = true)
public class BizRegistryDataService extends CrudService<BizRegistryDataDao, BizRegistryData> {

	public BizRegistryData get(String id) {
		return super.get(id);
	}

	public List<BizRegistryData> findList(BizRegistryData bizRegistryData) {
		return super.findList(bizRegistryData);
	}

	public Page<BizRegistryData> findPage(Page<BizRegistryData> page, BizRegistryData bizRegistryData) {
		return super.findPage(page, bizRegistryData);
	}

	@Transactional(readOnly = false)
	public void save(BizRegistryData bizRegistryData) {
		super.save(bizRegistryData);
	}

	@Transactional(readOnly = false)
	public void delete(BizRegistryData bizRegistryData) {
		super.delete(bizRegistryData);
	}

	public boolean checkKeyWords(String keyWords) {
		keyWords = keyWords.toUpperCase();
		if ("KH".equals(keyWords)) {
			return true;
		} else {
			return false;
		}
	}

	@Transactional(readOnly = false)
	public void sendActitiyCode(String json) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			List<Map<String, String>> list = (List<Map<String, String>>) JsonMapper.fromJsonString(json, List.class);
			Map<String, String> map = list.get(0);
			String spNumber = map.get("srcid"); // 上行接入号
			String phone = map.get("mobile"); // 用户手机号
			String keyWords = map.get("msgcontent"); // 签名
			Date startTime = dateFormat.parse("2017-07-20");
			Date endTime = dateFormat.parse("2018-12-31");
			String currentTimeN = dateFormat.format(new Date());
			Date currentTime = dateFormat.parse(currentTimeN);
			if (currentTime.before(startTime)) {
				// 活动还未开始，发短信
				smsSend("【信息提醒】对不起，本次活动还未开始，谢谢！", phone);

			} else if (currentTime.after(endTime)) {
				// 活动已结束，发短信
				smsSend("【信息提醒】对不起，本次活动已结束，谢谢！", phone);
			} else {// 活动未过期
				if (checkKeyWords(keyWords)) { // 签名正确
					BizRegistryData param = new BizRegistryData();
					param.setPhone(phone);
					BizRegistryData bizRegistryData = getBizRegistryDataByphone(param);
					if (bizRegistryData != null && StringUtils.isNotBlank(phone)) {// 重复发送
						String code = bizRegistryData.getVerificationCode();
						smsSend("【信息提醒】对不起，您已经受理过该业务，无需重复申请，您之前的验证码是:" + code + "，谢谢！", phone);
					} else {// 生成验证码, 发送短信
						String code = createVerificationCode();
						param.setPhone(phone);
						param.setVerificationCode(code);
						smsSend("【信息提醒】受理成功，您的验证码是:" + code + "，谢谢!", phone);
						dao.insert(param); // 添加记录
					}
				} else {// 签名错误，发短信
					smsSend("【信息提醒】对不起，您发送的短信有误，正确短信指令：KH，请核对后重新发送，谢谢！", phone);
				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String createVerificationCode() {
		Random rad = new Random();
		String result = rad.nextInt(1000000) + "";
		System.out.println("长度" + result.length());
		if (result.length() != 6) {
			return createVerificationCode();
		}
		return result;
	}

	/**
	 * 短信发送
	 * 
	 * @param code
	 * @param phone
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String smsSend(String smscontent, String phone) throws UnsupportedEncodingException {

		String userid = DictUtils.getDictLabel("userid", "smssend_param", null);
		String pwd = DictUtils.getDictLabel("pwd", "smssend_param", null);
		String url = DictUtils.getDictLabel("url", "smssend_param", null);
		String md5 = HttpRequest.md5(userid + "||" + phone + "||" + pwd);
		String content = URLEncoder.encode(smscontent, "UTF-8");
		int smstype = 0;
		String sendtermid = "1";
		String sendtime = "1";

		return send(url, userid, smstype, sendtermid, sendtime, phone, content, md5);
	}

	private static String send(String url, String userid, int smstype, String sendtermid, String sendtime,
			String phones, String content, String md5) {
		return HttpRequest.sendPost(url + "/sismsapi.go?method=smssend&userid=" + userid + "&smstype=" + smstype
				+ "&sendtermid=" + sendtermid + "&sendtime=" + sendtime + "&phones=" + phones + "&content=" + content
				+ "&md5=" + md5, null, null);
	}

	@Transactional(readOnly = true)
	public BizRegistryData getBizRegistryDataByphone(BizRegistryData bizRegistryData) {
		bizRegistryData = dao.getBizRegistryDataByphone(bizRegistryData);
		return bizRegistryData;

	}

}