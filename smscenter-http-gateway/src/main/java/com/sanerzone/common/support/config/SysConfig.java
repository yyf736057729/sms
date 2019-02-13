package com.sanerzone.common.support.config;

import com.xiaoleilu.hutool.setting.dialect.Props;

public class SysConfig {
	
	private static Props progs = new Props("config.properties");
	
	/**
	 * 获取当前对象实例
	 */
	public static Props getConfig() {
		return progs;
	}
	
	
}
