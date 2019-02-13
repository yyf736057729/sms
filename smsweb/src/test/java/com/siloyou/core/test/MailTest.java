package com.siloyou.core.test;

import java.util.List;

import com.siloyou.core.common.utils.EmailUtil;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.utils.DictUtils;

public class MailTest {
	public static void main(String[] args) {
		sendEmail("网关监测：网关ID：LT5005网关名称：【模拟】联通行业通道-高斯, 5分钟失败次数：7, 连续失败次数：0<br/>");
	}
	public static void sendEmail(String content)
    {
        EmailUtil email = new EmailUtil("smtp.exmail.qq.com", 465, 0, true, "xurui@xxx.com", "2380915Xu", true);
        try
        {
//        	List<Dict> list = DictUtils.getDictList("warnmail_address");
//        	StringBuilder toAddress = new StringBuilder();
//        	for(Dict item : list) {
//        		toAddress.append(item.getValue()).append(",");
//        	}
            email.sendEmail("xurui@xxx.com",
                "泛圣互联",
                "guorui_xu@qq.com", //"guorui_xu@qq.com,7552551@qq.com,447212972@qq.com,13666672546@139.com",
                "网关告警通知",
                "<html><body><font color='red'>" + content + ",请登录<a href=\"http://118.178.87.60:10081/\">泛圣互联服务平台</a>进行处理。</font></body></html>",
                null);
            System.out.println("send out successfully");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("send fail");
        }
    }
}
