package com.siloyou.core.modules.cms.entity;

import com.siloyou.core.common.persistence.DataEntity;

import java.util.Date;

/**
 * @description: 内容控制策略库
 * @author: Cral
 * @create: 2019-01-07 18:18
 */
public class ContentManage extends DataEntity<ContentManage> {
        private String contentManage;
        private Date create;	// 时间
        private String status;//内容策略1、2
        private Long time;
        private Long count;

        public String getStatus() {
                return status;
        }

        public void setStatus(String status) {
                this.status = status;
        }

        public Long getTime() {
                return time;
        }

        public void setTime(Long time) {
                this.time = time;
        }

        public Long getCount() {
                return count;
        }

        public void setCount(Long count) {
                this.count = count;
        }

        public String getContentManage() {
                return contentManage;
        }

        public void setContentManage(String contentManage) {
                this.contentManage = contentManage;
        }

        public Date getCreate() {
                return create;
        }

        public void setCreate(Date create) {
                this.create = create;
        }
}