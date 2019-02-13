package com.sanerzone.common.modules.phone.entity;



import com.sanerzone.common.support.persistence.DataEntity;

import java.util.Date;

/**
 * @author yuyunfeng
 * @create_time 2019/1/11
 * @describe ${通道模板管理}
 */
public class JmsgSmsGatewayTmpl extends DataEntity<JmsgSmsGatewayTmpl> {

    private static final long serialVersionUID = 1L;

    public String gatewayId;    //网关id
    public String joinNumber;   //接入号
    public String templateId;   //目标模板id
    public String templateName; //模板名称
    public String templateContent;//模板内容
    public Date createTime;     //创建时间

    public String gatewayName; //网关名称

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

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getJoinNumber() {
        return joinNumber;
    }

    public void setJoinNumber(String joinNumber) {
        this.joinNumber = joinNumber;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
