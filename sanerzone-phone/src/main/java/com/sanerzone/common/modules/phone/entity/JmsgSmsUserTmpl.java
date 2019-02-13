package com.sanerzone.common.modules.phone.entity;


import com.sanerzone.common.support.persistence.DataEntity;

import java.util.Date;
import java.util.List;

/**
 * @author yuyunfeng
 * @create_time 2019/1/11
 * @describe ${用户模板管理}
 */
public class JmsgSmsUserTmpl extends DataEntity<JmsgSmsUserTmpl> {

private static final long serialVersionUID = 1L;

    public String userId;    //用户id
    public String templateId;   //模板id
    public Date createTime;     //创建时间

    public String templateName; //模板名称
    public String joinNumber;   //接入号

    public String userName;   //账号名称

    public Date dateTimeQ;

    public Date dateTimeZ;

    public Date getDateTimeQ() {
        return dateTimeQ;
    }

    public void setDateTimeQ(Date dateTimeQ) {
        this.dateTimeQ = dateTimeQ;
    }

    public Date getDateTimeZ() {
        return dateTimeZ;
    }

    public void setDateTimeZ(Date dateTimeZ) {
        this.dateTimeZ = dateTimeZ;
    }

    private List<JmsgSmsUserTmpl> jmsgSmsUserTmplList;

    public List<JmsgSmsUserTmpl> getJmsgSmsUserTmplList() {
        return jmsgSmsUserTmplList;
    }

    public void setJmsgSmsUserTmplList(List<JmsgSmsUserTmpl> jmsgSmsUserTmplList) {
        this.jmsgSmsUserTmplList = jmsgSmsUserTmplList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getJoinNumber() {
        return joinNumber;
    }

    public void setJoinNumber(String joinNumber) {
        this.joinNumber = joinNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
