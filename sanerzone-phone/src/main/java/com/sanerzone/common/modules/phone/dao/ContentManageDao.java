package com.sanerzone.common.modules.phone.dao;


import com.sanerzone.common.modules.phone.entity.ContentManage;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

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
    List<ContentManage> findAll();
}
