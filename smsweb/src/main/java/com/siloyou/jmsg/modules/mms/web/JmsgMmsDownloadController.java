/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.siloyou.core.common.utils.CacheUtils;
import com.siloyou.core.common.utils.EhCacheUtils;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.common.utils.MmsUtils;
import com.siloyou.jmsg.common.utils.SignUtils;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsDownload;
import com.siloyou.jmsg.modules.mms.service.JmsgMmsDownloadService;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;

/**
 * 状态报告Controller
 * @author zhukc
 * @version 2016-05-28
 */
@Controller
@RequestMapping(value = "/")
public class JmsgMmsDownloadController extends BaseController
{
    @Autowired
    private JmsgMmsTaskDao jmsgMmsTaskDao;
    
    @Autowired
    private JmsgMmsDownloadService jmsgMmsDownloadService;
    
    @Autowired
    private MQUtils mQUtils;
    
    @Autowired
    private JmsgSmsSendDao jmsgSmsSendDao;
    
    @RequestMapping(value = "/m{mms}_{id}")
    public String Report(@PathVariable String mms, @PathVariable String id, HttpServletRequest request,
        HttpServletResponse response)
        throws IOException
    {
        //byte[] mmsByte =JedisClusterUtils.getJedisInstance().get(mms.getBytes());
        OutputStream os = response.getOutputStream();
        try
        {
            //String detailId = String.valueOf(Integer.parseInt(id, 16));//16进制转换成10进制 任务明细ID
            //String taskId = String.valueOf(Integer.parseInt(mms, 16));//16进制转换成10进制 任务ID
            String detailId = id;
            String taskId = mms;
            String deviceType = request.getHeader("user-agent");//设备
            if (StringUtils.isNotBlank(deviceType) && deviceType.length() > 64)
            {
                deviceType = deviceType.substring(0, 64);
            }
            /*String msgid =
                MmsSendService.sendMmsDownload(detailId, "{\"id\":\"" + detailId + "\", \"deviceType\":\"" + deviceType
                    + "\"}");
            
            logger.info("mms download msgid:{}, key:{}, taskid:{}, user-agent:{}",
                msgid,
                detailId,
                taskId,
                request.getHeader("user-agent"));*/
            
            JmsgMmsDownload jmsgMmsDownload = new JmsgMmsDownload();
            jmsgMmsDownload.setTaskid(taskId);
            jmsgMmsDownload.setBizid(detailId);
            jmsgMmsDownload.setDeviceType(deviceType);
            jmsgMmsDownload.setReceiveDatetime(new Date());
            jmsgMmsDownload.setCreateDatetime(new Date());
            
            mQUtils.sendMmsDownloadMQ(detailId, FstObjectSerializeUtil.write(jmsgMmsDownload));
            
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + mms);
            byte[] mmsByte = (byte[])CacheUtils.get("mmsFileCache", mms);
            if (mmsByte == null)
            {
                JmsgSmsSend jmsgSmsSend = jmsgSmsSendDao.get(detailId);
                if (jmsgSmsSend != null)
                {
                    String mmsFrom  = "106";
                    List<Dict> dictList = DictUtils.getDictList("mmsfrom");
                    if (null != dictList && dictList.size() > 0)
                    {
                        Collections.shuffle(dictList);
                        mmsFrom = dictList.get(0).getValue();
                    }
                    
                    String content = MmsUtils.mmsInstance.makeMmsPdu(taskId, SignUtils.get(jmsgSmsSend.getSmsContent()), mmsFrom, "<div>"+SignUtils.getContent(jmsgSmsSend.getSmsContent()))+"</div>";//制作彩信
                    if(content != null){
                        EhCacheUtils.put("mmsFileCache", taskId, content);
                    }
                }
            }
            os.write(mmsByte);
            os.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (os != null)
            {
                os.close();
            }
        }
        return null;
    }
}