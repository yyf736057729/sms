package com.siloyou.core.modules.cms.dao;

import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.core.modules.cms.entity.ContentManage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容控制策略库
 * @author Carl
 * @version 2019-1-7
 */
@MyBatisDao
public interface ContentManageDao {
    /**
     * 查询内容控制策略库
     * @return
     */
    List<ContentManage> getContentMg(@Param(value="status")String status);
}
