package com.siloyou.jmsg.common.mq;

import java.io.Serializable;

import com.alibaba.rocketmq.common.message.MessageQueue;


/**
 * @author shijia.wxr
 */
public class SMSMessageQueue implements Comparable<SMSMessageQueue>, Serializable {
    private static final long serialVersionUID = 6191200464116433425L;
    private String topic;
    private String tag;
    private String brokerName;
    private int queueId;
    
    public SMSMessageQueue() {

    }


    public SMSMessageQueue(String topic, String tag, String brokerName, int queueId) {
        this.topic = topic;
        this.tag = tag;
        this.brokerName = brokerName;
        this.queueId = queueId;
    }


    public String getTopic() {
        return topic;
    }


    public void setTopic(String topic) {
        this.topic = topic;
    }

    
    public String getTag() {
		return tag;
	}


	public void setTag(String tag) {
		this.tag = tag;
	}


	public String getBrokerName() {
        return brokerName;
    }


    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }


    public int getQueueId() {
        return queueId;
    }


    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((brokerName == null) ? 0 : brokerName.hashCode());
        result = prime * result + queueId;
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SMSMessageQueue other = (SMSMessageQueue) obj;
        if (brokerName == null) {
            if (other.brokerName != null)
                return false;
        }
        else if (!brokerName.equals(other.brokerName))
            return false;
        if (queueId != other.queueId)
            return false;
        if (topic == null) {
            if (other.topic != null)
                return false;
        }
        else if (!topic.equals(other.topic))
            return false;
        
        if (tag == null) {
            if (other.tag != null)
                return false;
        }
        else if (!tag.equals(other.tag))
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "MessageQueue [topic=" + topic + "tag="+ tag +", brokerName=" + brokerName + ", queueId=" + queueId + "]";
    }


    @Override
    public int compareTo(SMSMessageQueue o) {
        {
            int result = this.topic.compareTo(o.topic);
            if (result != 0) {
                return result;
            }
        }
        
        {
            int result = this.tag.compareTo(o.tag);
            if (result != 0) {
                return result;
            }
        }

        {
            int result = this.brokerName.compareTo(o.brokerName);
            if (result != 0) {
                return result;
            }
        }

        return this.queueId - o.queueId;
    }
}
