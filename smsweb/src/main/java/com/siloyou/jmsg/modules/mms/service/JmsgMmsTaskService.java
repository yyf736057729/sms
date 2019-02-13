/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.common.utils.EhCacheUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.utils.MmsUtils;
import com.siloyou.jmsg.common.utils.PhoneTypeUtils;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsDataDao;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDao;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDetailDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsData;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTask;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTaskDetail;

/**
 * 彩信发送管理Service
 * @author zhukc
 * @version 2016-05-20
 */
@Service
@Transactional(readOnly = true)
public class JmsgMmsTaskService extends CrudService<JmsgMmsTaskDao, JmsgMmsTask> {
	
	ThreadPoolTaskExecutor poolTaskExecutor = (ThreadPoolTaskExecutor)SpringContextHolder.getBean("taskExecutor");
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	@Autowired
	private JmsgMmsDataDao jmsgMmsDataDao;
	
	@Autowired
	private JmsgMmsTaskDetailDao jmsgMmsTaskDetailDao;
	
	private static final int BATCH_COMMIT_MAX_COUNT = 500;//批量提交默认值

	public JmsgMmsTask get(String id) {
		return super.get(id);
	}
	
	public List<JmsgMmsTask> findList(JmsgMmsTask jmsgMmsTask) {
		return super.findList(jmsgMmsTask);
	}
	
	public Page<JmsgMmsTask> findPage(Page<JmsgMmsTask> page, JmsgMmsTask jmsgMmsTask) {
		return super.findPage(page, jmsgMmsTask);
	}
	
	public Page<JmsgMmsTask> findMmsSendTongjiPage(Page<JmsgMmsTask> page, JmsgMmsTask jmsgMmsTask) {
		jmsgMmsTask.setPage(page);
		page.setList(dao.findMmsSendTongjiList(jmsgMmsTask));
		return page;
	}
	
	public Page<JmsgMmsTask> findXFDetailListPage(Page<JmsgMmsTask> page, JmsgMmsTask jmsgMmsTask) {
		jmsgMmsTask.setPage(page);
		page.setList(dao.findXFDetailList(jmsgMmsTask));
		return page;
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgMmsTask jmsgMmsTask) {
		super.save(jmsgMmsTask);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgMmsTask jmsgMmsTask) {
		super.delete(jmsgMmsTask);
	}
	
	
	@Transactional(readOnly = false)
	public String insertTask(String mmsId,Set<String> phoneList,int count,Date sendDatetime){
		
		JmsgMmsData jmsgMmsData = jmsgMmsDataDao.get(mmsId);
		if(jmsgMmsData == null){
			return "彩信不存在";
		}else{
			if("9".equals(jmsgMmsData.getCheckStatus())){
				return "彩信待审中，禁止发送";
			}else if("0".equals(jmsgMmsData.getCheckStatus())){
				return "彩信审核不通过，禁止发送";
			}
		}
		
		JmsgMmsTask jmsgMmsTask = new JmsgMmsTask();
		
		
		String msg = "彩信接收号码导入失败";
		long s = System.currentTimeMillis();
		String userId = UserUtils.getUser().getId();
		String taskId = "";
		try{
			jmsgMmsTask.setMmsId(mmsId);
			jmsgMmsTask.setMmsTitle(jmsgMmsData.getMmsTitle());
			jmsgMmsTask.setSendDatetime(sendDatetime);
			taskId = saveTask(jmsgMmsTask, userId);//保存任务
		}catch(Exception e){
			e.printStackTrace();
			return msg;
		}
		Map<String,String> map = null;
		try{
			map = saveTaskDetail(phoneList, taskId, userId);//保存任务明细
		}catch(Exception e){
			e.printStackTrace();
			//删除任务
			jmsgMmsTask.setId(taskId);
			dao.delete(jmsgMmsTask);
			return msg;
		}
		
		int submitCount = Integer.valueOf(map.get("submitCount"));
		int errorCount = Integer.valueOf(map.get("errorCount"));
		String countDetail = map.get("countDetail");
		try{
			makeMms(taskId, jmsgMmsTask.getMmsId(), submitCount,countDetail,"",UserUtils.getUser().getMmsfrom(),jmsgMmsData);//制作彩信
		}catch(Exception e){
			//删除任务
			jmsgMmsTask.setId(taskId);
			dao.delete(jmsgMmsTask);
			//删除任务明细
			jmsgMmsTaskDetailDao.deleteByTaskId(taskId);
			return msg;
		}
		
		jmsgAccountService.consumeMoney(userId, "02", submitCount, "mms", "自消费操作(任务ID："+taskId+")", userId, taskId);//自消费
		
		System.out.println("导入数据耗时："+(System.currentTimeMillis()-s)/1000);
		return "彩信接收号码导入："+count+"条，成功："+submitCount+"条，重复："+(count-submitCount-errorCount)+"条，异常号码："+errorCount+"条";
	}
	
	@Transactional(readOnly = false)
	public String insertTaskByInterface(Set<String> phoneList,String userId,String pMmsFrom,JmsgMmsData jmsgMmsData){
		JmsgMmsTask jmsgMmsTask = new JmsgMmsTask();
		String taskId = "";
		try{
			jmsgMmsTask.setMmsId(jmsgMmsData.getId());
			jmsgMmsTask.setMmsTitle(jmsgMmsData.getMmsTitle());
			taskId = saveTask(jmsgMmsTask, userId);//保存任务
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
		Map<String,String> map = null;
		try{
			map = saveTaskDetail(phoneList, taskId, userId);//保存任务明细
		}catch(Exception e){
			e.printStackTrace();
			//删除任务
			jmsgMmsTask.setId(taskId);
			dao.delete(jmsgMmsTask);
			return "";
		}
		
		int submitCount = Integer.valueOf(map.get("submitCount"));
		String countDetail = map.get("countDetail");
		try{
			makeMms(taskId, jmsgMmsTask.getMmsId(), submitCount,countDetail,pMmsFrom,UserUtils.getByNow(userId).getMmsfrom(),jmsgMmsData);//制作彩信
		}catch(Exception e){
			//删除任务
			jmsgMmsTask.setId(taskId);
			dao.delete(jmsgMmsTask);
			//删除任务明细
			jmsgMmsTaskDetailDao.deleteByTaskId(taskId);
			return "";
		}
		
		jmsgAccountService.consumeMoney(userId, "02", submitCount, "mms", "自消费操作(任务ID："+taskId+")", userId, taskId);//自消费
		
		return taskId;
	}

	
	public String saveTask(JmsgMmsTask jmsgMmsTask,String userId) throws Exception{
		if(jmsgMmsTask.getSendDatetime() == null){
			jmsgMmsTask.setSendDatetime(new Date());
		}
		jmsgMmsTask.setStatus("-1");//不发送
		jmsgMmsTask.setCreateUserId(userId);
		dao.insert(jmsgMmsTask);
		return jmsgMmsTask.getId();
	}
	
	public Map<String,String> saveTaskDetail(Set<String> phoneList,String taskId,String userId)
	throws Exception{
		Integer submitCount=0,errorCount=0,ydCount=0,ltCount=0,dxCount=0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		try{
			JmsgMmsTaskDetail detailEntity;
			for(String phone: phoneList){
				detailEntity = new JmsgMmsTaskDetail();
				detailEntity.setTaskId(taskId);
				detailEntity.setPhone(phone);
				detailEntity.setCreateUserId(userId);
				String phoneType = PhoneTypeUtils.get(phone);
				String sendStatus = "F1";//号码不是移动
				if(StringUtils.isBlank(phoneType)){
					errorCount++;
					continue;
				}else{
					if("YD".equals(phoneType)){
						ydCount++;
						sendStatus = "P0";
					}else if("LT".equals(phoneType)){
						ltCount++;
					}else if("DX".equals(phoneType)){
						dxCount++;
					}
				}
				detailEntity.setPhoneType(phoneType);
				detailEntity.setSendStatus(sendStatus);//发送状态
				sqlSession.insert("com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDetailDao.insert", detailEntity);
				submitCount++;
				if(submitCount % BATCH_COMMIT_MAX_COUNT == 0) {
					sqlSession.commit();
				}
			}
			sqlSession.commit();
		}catch(Exception e){
			throw e;
		}finally{
			if(sqlSession != null){
				sqlSession.close();
			}
		}
		Map<String,String> map = Maps.newHashMap();
		map.put("submitCount", String.valueOf(submitCount));
		map.put("errorCount", String.valueOf(errorCount));
		map.put("countDetail", ydCount+"|"+ltCount+"|"+dxCount);
		return map;
	}
	
	public void makeMms(String taskId,String mmsId,int submitCount,String countDetail,String pMmsFrom,String mmsFrom,JmsgMmsData jmsgMmsData) throws Exception{
		String mmsBody = "";
		String mmsTitle="";
		//JmsgMmsData jmsgMmsData = jmsgMmsDataDao.get(mmsId);
		if(jmsgMmsData !=null){
			mmsBody = jmsgMmsData.getContent();
			mmsTitle = jmsgMmsData.getMmsTitle();
		}
		//创建URL
		String prefix= Global.getConfig("mms.download.url");//地址前缀
		String mms = Integer.toHexString(Integer.parseInt(taskId));
		if(StringUtils.isBlank(mmsFrom)){
			if(StringUtils.isBlank(pMmsFrom)){
				List<Dict> mmsfromList = DictUtils.getDictList("mms_from");
				if(mmsfromList == null || mmsfromList.isEmpty()) {
					mmsFrom = "10690324" + taskId;
				} else {
					int index=(int)(Math.random()*mmsfromList.size());
					mmsFrom = mmsfromList.get(index).getValue() + taskId;
				}
			}else{
				mmsFrom = pMmsFrom;
			}
		}
		int mmsSize = 0;
		byte[] content = MmsUtils.mmsInstance.makeMmsPdu(taskId, mmsTitle,  mmsFrom, mmsBody);//制作彩信
		if(content != null){
			mmsSize = content.length;
			EhCacheUtils.put("mmsFileCache", mms, content);
		}
		JmsgMmsTask jmsgMmsTask = new JmsgMmsTask();
		jmsgMmsTask.setMmsSize(mmsSize);
		jmsgMmsTask.setMmsUrl(prefix+"/m"+mms);
		jmsgMmsTask.setSendCount(submitCount);
		jmsgMmsTask.setStatus("1");//待发送
		jmsgMmsTask.setCountDetail(countDetail);
		jmsgMmsTask.setId(taskId);
		jmsgMmsTask.setMmsBody(content);//彩信内容
		dao.updateTaskInfo(jmsgMmsTask);//修改任务信息
		jmsgMmsDataDao.updateUseFlag(mmsId);//修改素材使用次数
	}
	
	
	
	
	
	@Transactional(readOnly = false)
	public String insertTaskNew(JmsgMmsTask jmsgMmsTask,Set<String> phoneList,int count){
		long s = System.currentTimeMillis();
		int ydCnt=0,ltCnt=0,dxCnt=0,errCnt=0;//移动、联通、电信、异常号码数
		String phones="";
		for (String phone : phoneList) {
			String phoneType = PhoneTypeUtils.get(phone);//手机类型
			if(StringUtils.isBlank(phoneType)){
				errCnt++;
				continue;
			}else{
				if("YD".equals(phoneType)){
					ydCnt++;
				}else if("LT".equals(phoneType)){
					ltCnt++;
				}else if("DX".equals(phoneType)){
					dxCnt++;
				}
				phones=phones+","+phone;
			}
		}
		
		String userId = UserUtils.getUser().getId();
		int phoneSize = phoneList.size();
		int sendCount =  phoneSize-errCnt;
		if(jmsgMmsTask.getSendDatetime() == null){
			jmsgMmsTask.setSendDatetime(new Date());
		}
		jmsgMmsTask.setStatus("1");//待发送
		jmsgMmsTask.setCreateUserId(userId);
		jmsgMmsTask.setSendCount(sendCount);
		jmsgMmsTask.setCountDetail(ydCnt+"|"+ltCnt+"|"+dxCnt);
		jmsgMmsTask.setPhonelist(phones.substring(1));
		dao.insert(jmsgMmsTask);//保存任务
		
		String taskId = jmsgMmsTask.getId();
		
		String mmsBody = "";
		JmsgMmsData jmsgMmsData = jmsgMmsDataDao.get(jmsgMmsTask.getMmsId());
		if(jmsgMmsData !=null){
			mmsBody = jmsgMmsData.getContent();
		}
		//创建URL
		String prefix= Global.getConfig("mms.download.url");//地址前缀
		String mms = Integer.toHexString(Integer.parseInt(taskId));
		List<Dict> mmsfrom = DictUtils.getDictList("mms_from");
		String mmsFrom = "";
		if(mmsfrom == null || mmsfrom.isEmpty()) {
			mmsFrom = "10690324" + taskId;
		} else {
			int index=(int)(Math.random()*mmsfrom.size());
			mmsFrom = mmsfrom.get(index).getValue() + taskId;
		}
		byte[] content = MmsUtils.mmsInstance.makeMmsPdu(taskId, jmsgMmsTask.getMmsTitle(),  mmsFrom, mmsBody);
		int mmsSize = content.length;//制作彩信
		jmsgMmsTask.setMmsSize(mmsSize);
		jmsgMmsTask.setMmsUrl(prefix+"/m"+mms);
		dao.updateTaskInfo(jmsgMmsTask);
		jmsgMmsDataDao.updateUseFlag(jmsgMmsTask.getMmsId());//修改素材使用次数
		System.out.println("导入数据耗时："+(System.currentTimeMillis()-s)/1000);
		return "彩信接收号码导入："+count+"条，成功："+sendCount+"条，重复："+(count-sendCount-errCnt)+"条，异常号码："+errCnt+"条";
	}
	
	
	@Transactional(readOnly = false)
	public void updateStatus(JmsgMmsTask jmsgMmsTask){
		Map<String,String> pMap = Maps.newHashMap();
		pMap.put("status", jmsgMmsTask.getStatus());
		pMap.put("updateBy",UserUtils.getUser().getId());
		pMap.put("taskId",jmsgMmsTask.getId());
		dao.updateStatus(pMap);
	}
	
}