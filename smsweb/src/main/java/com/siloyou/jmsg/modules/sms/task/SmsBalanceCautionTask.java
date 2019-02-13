package com.siloyou.jmsg.modules.sms.task;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.sanerzone.common.support.utils.HttpRequest;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.common.utils.SignUtils;
import com.siloyou.jmsg.modules.account.dao.JmsgAccountDao;
import com.siloyou.jmsg.modules.account.entity.JmsgAccount;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsTaskService;

//定时任务
@Service
@Lazy(false)
public class SmsBalanceCautionTask
{
    private static Logger logger = LoggerFactory.getLogger(SmsBalanceCautionTask.class);
    
    private JmsgAccountDao jmsgAccountDao = SpringContextHolder.getBean(JmsgAccountDao.class);
    
    private JmsgAccountService jmsgAccountService = SpringContextHolder.getBean(JmsgAccountService.class);
    
    private JmsgSmsTaskService jmsgSmsTaskService = SpringContextHolder.getBean(JmsgSmsTaskService.class);
    
    //每1小时执行一次
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void exec()
    {
        
        String balanceCautionUserId = Global.getBalanceCautionUserId();
        if (StringUtils.isNotBlank(balanceCautionUserId))
        {
            User user = UserUtils.get(balanceCautionUserId);
            if (null == user)
            {
                logger.debug("短信用户未配置");
                return;
            }
                
            List<JmsgAccount> userList = jmsgAccountDao.getBalanceCautionUsers();
            if(null == userList || userList.isEmpty()) {
            	logger.debug("没有需要通知的用户");
                return;
            }
            
            //发送内容
            String content = null;
            for (JmsgAccount tmpUser : userList)
            {
                content = "【泛圣科技】您好" + tmpUser.getUser().getName() + "，您的余额为" + tmpUser.getMoney() + "，请及时充值";
                if (StringUtils.isNotBlank(tmpUser.getUser().getCautionMobile()))
                {
                    String ts = String.valueOf(System.currentTimeMillis());
            		String md5 = HttpRequest.md5(user.getId() + ts + user.getApikey());
            		String postBody = String.format("userid=%s&ts=%s&sign=%s&mobile=%s&msgcontent=%s", user.getId(), ts, md5, tmpUser.getUser().getCautionMobile(), content);
                    HttpRequest.sendFormPost("http://118.178.35.191:8808/api/sms/send", postBody, null, "UTF-8", 30000);
                }
                else
                {
                    logger.debug("余额提醒号码为空不符合要求");
                }
            }
        }
    }
}
