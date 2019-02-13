package com.sanerzone.common.modules.phone.entity;


import com.sanerzone.common.support.persistence.DataEntity;

import java.util.Date;

/**
 * @description: 内容控制策略库
 * @author: Cral
 * @create: 2019-01-07 18:18
 */
public class GatewayQueue extends DataEntity<GatewayQueue> {
        private String gateWayId;
        private String queueName;
        private String businessType;
        private int weight;
        private Date createTime;
        private String remarks;
        private String status;
        private String delFalg;

        public String getGateWayId() {
                return gateWayId;
        }

        public void setGateWayId(String gateWayId) {
                this.gateWayId = gateWayId;
        }

        public String getQueueName() {
                return queueName;
        }

        public void setQueueName(String queueName) {
                this.queueName = queueName;
        }

        public String getBusinessType() {
                return businessType;
        }

        public void setBusinessType(String businessType) {
                this.businessType = businessType;
        }

        public int getWeight() {
                return weight;
        }

        public void setWeight(int weight) {
                this.weight = weight;
        }

        public Date getCreateTime() {
                return createTime;
        }

        public void setCreateTime(Date createTime) {
                this.createTime = createTime;
        }

        @Override
        public String getRemarks() {
                return remarks;
        }

        @Override
        public void setRemarks(String remarks) {
                this.remarks = remarks;
        }

        public String getStatus() {
                return status;
        }

        public void setStatus(String status) {
                this.status = status;
        }

        public String getDelFalg() {
                return delFalg;
        }

        public void setDelFalg(String delFalg) {
                this.delFalg = delFalg;
        }
}