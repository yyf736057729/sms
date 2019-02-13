/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.core.modules.sys.dao;

import java.util.List;
import java.util.Map;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.entity.Role;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 用户DAO接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@MyBatisDao
public interface UserDao extends CrudDao<User> {
	
	/**
	 * 根据登录名称查询用户
	 * @param loginName
	 * @return
	 */
	public User getByLoginName(User user);

	/**
	 * 通过OfficeId获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param user
	 * @return
	 */
	public List<User> findUserByOfficeId(User user);
	
	/**
	 * 查询全部用户数目
	 * @return
	 */
	public long findAllCount(User user);
	
	/**
	 * 更新用户密码
	 * @param user
	 * @return
	 */
	public int updatePasswordById(User user);
	
	/**
	 * 更新登录信息，如：登录IP、登录时间
	 * @param user
	 * @return
	 */
	public int updateLoginInfo(User user);

	/**
	 * 删除用户角色关联数据
	 * @param user
	 * @return
	 */
	public int deleteUserRole(User user);
	
	/**
	 * 插入用户角色关联数据
	 * @param user
	 * @return
	 */
	public int insertUserRole(User user);

	/**
	 * 插入用户角色关联数据fix
	 * @param user
	 * @return
	 */
	public int insertUserRoleFix(User user);
	
	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int updateUserInfo(User user);
	
	public List<Dict> findAppTypeByUserId(String userId);
	
	public void deleteByAgency(User user);
	
	public User getAgency(String companyId);
	
	public User getAgencyByUserId(String id);
	
	/**
	 * 批量更新用户通道组信息
	 * @param companyId
	 * @param groupId
	 * @see [类、类#方法、类#成员]
	 */
	public void batchUpdateUserGroup(Map<String, Object> map);
	
	/**
     * 修改用户通道组信息
     * @param companyId
     * @param groupId
     * @see [类、类#方法、类#成员]
     */
    public void updateUserGroupById(Map<String, Object> map);
}
