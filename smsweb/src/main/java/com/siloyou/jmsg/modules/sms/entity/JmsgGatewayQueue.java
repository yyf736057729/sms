package com.siloyou.jmsg.modules.sms.entity;

import com.siloyou.core.common.persistence.DataEntity;

import java.util.Date;

/**
 * @author yuyunfeng
 * @create_time 2019/1/9
 * @describe ${通道队列}
 */
public class JmsgGatewayQueue extends DataEntity<JmsgGatewayQueue> {

    private static final long serialVersionUID = 1L;

    private String gatewayId;          // 网关id
    private String queueName;		    // 队列名称
    private String businessType;		// 业务类型（1.验证码 2.单发 3.群发）
    private String weight;			    // 权重
    private String status;              // 状态
    private Date createTime;			// 创建时间

    private String gatewayName;         //网关名称
    private String gatewayState;        //网关状态

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getGatewayState() {
        return gatewayState;
    }

    public void setGatewayState(String gatewayState) {
        this.gatewayState = gatewayState;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getWeight() {
        return weight;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
