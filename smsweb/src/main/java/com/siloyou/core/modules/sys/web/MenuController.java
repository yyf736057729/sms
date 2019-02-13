/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.core.modules.sys.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.utils.DictUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.Menu;
import com.siloyou.core.modules.sys.service.SystemService;
import com.siloyou.core.modules.sys.utils.UserUtils;

/**
 * 菜单Controller
 * @author ThinkGem
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/menu")
public class MenuController extends BaseController {

	@Autowired
	private SystemService systemService;
	
	@ModelAttribute("menu")
	public Menu get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return systemService.getMenu(id);
		}else{
			return new Menu();
		}
	}

	@RequiresPermissions("sys:menu:view")
	@RequestMapping(value = {"list", ""})
	public String list(Model model) {
		List<Menu> list = Lists.newArrayList();
		List<Menu> sourcelist = systemService.findAllMenu();
		Menu.sortList(list, sourcelist, Menu.getRootId(), true);
        model.addAttribute("list", list);
		return "modules/sys/menuList";
	}

	@RequiresPermissions("sys:menu:view")
	@RequestMapping(value = "form")
	public String form(Menu menu, Model model) {
		if (menu.getParent()==null||menu.getParent().getId()==null){
			menu.setParent(new Menu(Menu.getRootId()));
		}
		menu.setParent(systemService.getMenu(menu.getParent().getId()));
		// 获取排序号，最末节点排序号+30
		if (StringUtils.isBlank(menu.getId())){
			List<Menu> list = Lists.newArrayList();
			List<Menu> sourcelist = systemService.findAllMenu();
			Menu.sortList(list, sourcelist, menu.getParentId(), false);
			if (list.size() > 0){
				menu.setSort(list.get(list.size()-1).getSort() + 30);
			}
		}
		model.addAttribute("menu", menu);
		return "modules/sys/menuForm";
	}
	
	@RequiresPermissions("sys:menu:edit")
	@RequestMapping(value = "save")
	public String save(Menu menu, Model model, RedirectAttributes redirectAttributes) {
		if(!UserUtils.getUser().isAdmin()){
			addMessage(redirectAttributes, "越权操作，只有超级管理员才能添加或修改数据！");
			return "redirect:" + adminPath + "/sys/role/?repage";
		}
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/menu/";
		}
		if (!beanValidator(model, menu)){
			return form(menu, model);
		}
		systemService.saveMenu(menu);
		addMessage(redirectAttributes, "保存菜单'" + menu.getName() + "'成功");
		return "redirect:" + adminPath + "/sys/menu/";
	}
	
	@RequiresPermissions("sys:menu:edit")
	@RequestMapping(value = "delete")
	public String delete(Menu menu, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/menu/";
		}
//		if (Menu.isRoot(id)){
//			addMessage(redirectAttributes, "删除菜单失败, 不允许删除顶级菜单或编号为空");
//		}else{
			systemService.deleteMenu(menu);
			addMessage(redirectAttributes, "删除菜单成功");
//		}
		return "redirect:" + adminPath + "/sys/menu/";
	}

	@RequiresPermissions("user")
	@RequestMapping(value = "tree")
	public String tree() {
		return "modules/sys/menuTree";
	}

	@RequiresPermissions("user")
	@RequestMapping(value = "treeselect")
	public String treeselect(String parentId, Model model) {
		model.addAttribute("parentId", parentId);
		return "modules/sys/menuTreeselect";
	}
	
	/**
	 * 批量修改菜单排序
	 */
	@RequiresPermissions("sys:menu:edit")
	@RequestMapping(value = "updateSort")
	public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/menu/";
		}
    	for (int i = 0; i < ids.length; i++) {
    		Menu menu = new Menu(ids[i]);
    		menu.setSort(sorts[i]);
    		systemService.updateMenuSort(menu);
    	}
    	addMessage(redirectAttributes, "保存菜单排序成功!");
		return "redirect:" + adminPath + "/sys/menu/";
	}
	
	/**
	 * isShowHide是否显示隐藏菜单
	 * @param extId
	 * @param isShowHidden
	 * @param response
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId,@RequestParam(required=false) String isShowHide, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Menu> list = systemService.findAllMenu();
		for (int i=0; i<list.size(); i++){
			Menu e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				if(isShowHide != null && isShowHide.equals("0") && e.getIsShow().equals("0")){
					continue;
				}
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}

	/**
	 * @Description: 展示错误码对照表1
	 * @param: model
	 * @return: String
	 * @author: zhanghui
	 * @Date: 2019-01-23
	 */
	@RequestMapping(value = {"showErrorCodeTable1"})
	public String showErrorCodeTable1(Model model) {
	    //HTTP协议提交短信过程中常见错误代码
        List<Dict> httpProtocolSubmittingShortMessageCommonErrorCodeList = DictUtils.getDictList("http_protocol_submitting_short_message_common_error_code");
        model.addAttribute("httpProtocolSubmittingShortMessageCommonErrorCodeList", httpProtocolSubmittingShortMessageCommonErrorCodeList);

	    //CMPP协议登录提交过程中常见错误代码
        List<Dict> cmppProtocolLoginSubmissionCommonErrorCodeList = DictUtils.getDictList("cmpp_protocol_login_submission_common_error_code");
        model.addAttribute("cmppProtocolLoginSubmissionCommonErrorCodeList", cmppProtocolLoginSubmissionCommonErrorCodeList);

	    //状态回执报告成功提交短信后常见错误代码
        List<Dict> statusReceiptReportSuccessfulSubmissionCommonErrorCodeList = DictUtils.getDictList("status_receipt_report_successful_submission_common_error_code");
        model.addAttribute("statusReceiptReportSuccessfulSubmissionCommonErrorCodeList", statusReceiptReportSuccessfulSubmissionCommonErrorCodeList);
        return "modules/sys/errorCodeTable1";
	}

	/**
	 * @Description: 展示错误码对照表2
	 * @param: model
	 * @return: String
	 * @author: zhanghui
	 * @Date: 2019-01-23
	 */
	@RequestMapping(value = {"showErrorCodeTable2"})
	public String showErrorCodeTable2(Model model) {
        //中国移动错误代码
        List<Dict> chinaMobileErrorCodeList = DictUtils.getDictList("china_mobile_error_code");
        model.addAttribute("chinaMobileErrorCodeList", chinaMobileErrorCodeList);
		return "modules/sys/errorCodeTable2";
	}

	/**
	 * @Description: 展示错误码对照表3
	 * @param: model
	 * @return: String
	 * @author: zhanghui
	 * @Date: 2019-01-23
	 */
	@RequestMapping(value = {"showErrorCodeTable3"})
	public String showErrorCodeTable3(Model model) {
        //中国联通错误代码
        List<Dict> chinaUnicomErrorCodeList = DictUtils.getDictList("china_unicom_error_code");
        model.addAttribute("chinaUnicomErrorCodeList", chinaUnicomErrorCodeList);
		return "modules/sys/errorCodeTable3";
	}

	/**
	 * @Description: 展示错误码对照表4
	 * @param: model
	 * @return: String
	 * @author: zhanghui
	 * @Date: 2019-01-23
	 */
	@RequestMapping(value = {"showErrorCodeTable4"})
	public String showErrorCodeTable4(Model model) {
        //中国电信SMGP 3.0 协议的结果码
        List<Dict> chinaTelecomSMGPThreePointZeroProtocolResultCodeList = DictUtils.getDictList("china_telecom_smgp_three_point_zero_protocol_result_code");
        model.addAttribute("chinaTelecomSMGPThreePointZeroProtocolResultCodeList", chinaTelecomSMGPThreePointZeroProtocolResultCodeList);

        //中国电信ISMG返回码
        List<Dict> chinaTelecomISMGResultCodeList = DictUtils.getDictList("china_telecom_ismg_result_code");
        model.addAttribute("chinaTelecomISMGResultCodeList", chinaTelecomISMGResultCodeList);

        //中国电信计费结果返回码
        List<Dict> chinaTelecomBillingReturnCodeList = DictUtils.getDictList("china_telecom_billing_return_code");
        model.addAttribute("chinaTelecomBillingReturnCodeList", chinaTelecomBillingReturnCodeList);
		return "modules/sys/errorCodeTable4";
	}

}
