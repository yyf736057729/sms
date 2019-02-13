package com.sanerzone.smscenter.account.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import java.util.Date;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 资金账户信息表
 * </p>
 *
 * @author XuRui
 * @since 2017-06-12
 */
@TableName("jmsg_account")
public class Account extends Model<Account> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 用户ID
     */
	@TableField("user_id")
	private String userId;
    /**
     * 账号类型：短信sms、彩信mms、流量flow、话费telfree
     */
	@TableField("app_type")
	private String appType;
    /**
     * 可用余额
     */
	private Long money;
    /**
     * 创建时间
     */
	@TableField("create_date")
	private Date createDate;
    /**
     * 创建人
     */
	@TableField("create_by")
	private String createBy;
    /**
     * 扣费方式
     */
	@TableField("pay_mode")
	private String payMode;
    /**
     * 启用标识 1:启用 0：禁用
     */
	@TableField("used_flag")
	private String usedFlag;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public Long getMoney() {
		return money;
	}

	public void setMoney(Long money) {
		this.money = money;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getUsedFlag() {
		return usedFlag;
	}

	public void setUsedFlag(String usedFlag) {
		this.usedFlag = usedFlag;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
