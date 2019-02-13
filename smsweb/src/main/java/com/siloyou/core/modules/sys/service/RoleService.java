package com.siloyou.core.modules.sys.service;

import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.modules.sys.dao.RoleDao;
import com.siloyou.core.modules.sys.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName: RoleService
 * @Description: 角色业务逻辑接口类
 * @author: zhanghui
 * @Date: 2019-01-08
 */
@Service
@Transactional(readOnly = true)
public class RoleService extends CrudService<RoleDao, Role> {

	@Autowired
	RoleDao roleDao;

	/**
	 * @Description: 查询普通用户
	 * @param: role
	 * @return: List<Role>
	 * @author: zhanghui
	 * @Date: 2019-01-08
	 */
	public List<Role> findListForCommonUser(Role role){
		return roleDao.findListForCommonUser(role);
	}
	
}
