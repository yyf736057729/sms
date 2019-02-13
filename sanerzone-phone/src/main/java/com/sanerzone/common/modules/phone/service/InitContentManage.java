package com.sanerzone.common.modules.phone.service;

import com.sanerzone.common.modules.phone.dao.ContentManageDao;
import com.sanerzone.common.modules.phone.entity.ContentManage;
import com.sanerzone.common.support.utils.EhCacheUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

import java.util.List;

/**
 * @program: smscenter
 * @description: 初始化内容策略
 * @author: Cral
 * @create: 2019-01-08 16:30
 */
public class InitContentManage {
    private static ContentManageDao contentManageDao = SpringContextHolder.getBean(ContentManageDao.class);
    //初始化内容策略
    public void initContentManage(){
        List<ContentManage> list = contentManageDao.findAll();
        if(list != null && list.size() >0){
            for(ContentManage contentManage:list){
                EhCacheUtils.put("content",contentManage.getId(),contentManage);
            }
        }
    }
}