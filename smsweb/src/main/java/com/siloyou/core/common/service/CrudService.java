/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.core.common.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.common.persistence.Page;

/**
 * Service基类
 * @author ThinkGem
 * @version 2014-05-16
 */
@Transactional(readOnly = true)
public abstract class CrudService<D extends CrudDao<T>, T extends DataEntity<T>> extends BaseService {
	
	/**
	 * 持久层对象
	 */
	@Autowired
	protected D dao;

	public Class clazz;
	public String xmlName = "";

	public CrudService() {
		Type superclass = getClass().getGenericSuperclass();
		ParameterizedType parameterizedType = null;
		if (superclass instanceof ParameterizedType) {
			parameterizedType = (ParameterizedType) superclass;
			Type[] typeArray = parameterizedType.getActualTypeArguments();
			if (typeArray != null && typeArray.length > 0) {
				clazz=(Class)typeArray[0];
				//System.out.println(clazz.getName());
				xmlName = clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1, clazz.getName().length()) + ".xml";
			}
		}
	}

	/**
	 * 获取单条数据
	 * @param id
	 * @return
	 */
	public T get(String id) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.get(id);
	}
	
	/**
	 * 获取单条数据
	 * @param entity
	 * @return
	 */
	public T get(T entity) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.get(entity);
	}
	
	/**
	 * 查询列表数据
	 * @param entity
	 * @return
	 */
	public List<T> findList(T entity) {
		//System.out.println(clazz.getName());
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findList(entity);
	}
	
	/**
	 * 查询分页数据
	 * @param page 分页对象
	 * @param entity
	 * @return
	 */
	public Page<T> findPage(Page<T> page, T entity) {
		entity.setPage(page);
		logger.info(xmlName + "===>>>findList");
		page.setList(dao.findList(entity));
		return page;
	}

	/**
	 * 保存数据（插入或更新）
	 * @param entity
	 */
	@Transactional(readOnly = false)
	public void save(T entity) {
		if (entity.getIsNewRecord()){
			entity.preInsert();
			String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
			logger.info(xmlName + "===>>>insert");
			dao.insert(entity);
		}else{
			entity.preUpdate();
			String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
			System.out.println(xmlName + "===>>>update");
			dao.update(entity);
		}
	}
	
	/**
	 * 删除数据
	 * @param entity
	 */
	@Transactional(readOnly = false)
	public void delete(T entity) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		System.out.println(xmlName + "===>>>" + method);
		dao.delete(entity);
	}

}
