package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.constant.Constants;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao.MenuDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.MenuInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MenuService extends BaseController {
    @Autowired
    private MenuDao dao;

    @Autowired
    private BaseDao basedao;
    @Autowired
    private RoleService roleService;

    public List<MenuInfo> getChildMenusById(String menuId) {
        MenuInfo menuInfo = new MenuInfo();
        menuInfo.setParentId(menuId);
        menuInfo.setDeleted("0");
        List<QueryCondition> queryConditionList = new ArrayList<>();
        queryConditionList.add(new QueryCondition("deleted", "0"));
        queryConditionList.add(new QueryCondition("parentId",menuId));
        List<MenuInfo> list = this.basedao.get(MenuInfo.class, queryConditionList,"order_num");
        return list;
    }

    public JSONArray getChildMenus(MenuInfo menuInfo, String ownMenuId) {
        JSONArray jsonArray = new JSONArray();
        List<MenuInfo> list = this.getChildMenusById(menuInfo.getObjId());
        for (MenuInfo menuInfo0 : list) {
            if (ownMenuId.contains(menuInfo0.getObjId())) {
                JSONObject menuPath = JSONObject.parseObject(menuInfo0.getMenuPath());
                if ("1".equals(menuInfo0.getIsparent())) {
                    JSONArray childArr = this.getChildMenus(menuInfo0, ownMenuId);
                    if(!childArr.isEmpty()){
                        menuPath.put("children", childArr);
                    }
                }
                jsonArray.add(menuPath);
            }
        }
        return jsonArray;
    }


    /**
     * 对用户权限内的菜单进行梳理
     * 梳理逻辑：先梳理一级菜单，再梳理二级，再梳理三级；
     * 一级菜单的判断规则：PARENT_ID 为 000
     * @param menuList
     * @return
     */
    public JSONObject shuffleMenuList(List<MenuInfo> menuList) {
        JSONObject topMenus = new JSONObject();
        // 梳理一级菜单
        Iterator<MenuInfo> iterator = menuList.iterator();
        while (iterator.hasNext()) {
            MenuInfo menuInfo = iterator.next();
            if (MenuInfo.TOP_MENU_ID.equals(menuInfo.getParentId())) { // 说明是一级菜单
                topMenus.put(menuInfo.getObjId(), JSON.parseObject(JSON.toJSONString(menuInfo)));

                iterator.remove();
            }
        }
        // 梳理二级菜单
        if (menuList.size() > 0) {
            Iterator<MenuInfo> level2It = menuList.iterator();
            while (level2It.hasNext()) {
                MenuInfo menuInfo = level2It.next();
                JSONObject parentMenu = topMenus.getJSONObject(menuInfo.getParentId());
                if (parentMenu != null) {
                    JSONArray childs = parentMenu.getJSONArray("childs");
                    if (childs == null) {
                        childs = new JSONArray();
                    }
                    childs.add(menuInfo);
                    parentMenu.put("childs", childs);

                    level2It.remove();
                }
            }
        }

        return topMenus;
    }

    public String addMenu(MenuInfo mi) {
        if (StringUtils.isNotEmpty(mi.getParentId())) {
            mi = this.basedao.saveAndFlush(mi);  // 1.保存新增的菜单
            if (StringUtils.isNotEmpty(mi.getObjId())) { // 2.更新父级菜单
                MenuInfo parentmenu = this.basedao.getById(MenuInfo.class, mi.getParentId());
                if (null == parentmenu) {
                    return null;
                }
                parentmenu.setIsparent("1");
                this.basedao.update(parentmenu);
                return mi.getObjId();
            }
        } else {
            mi = this.basedao.saveAndFlush(mi);
            return mi.getObjId();
        }
        return null;
    }

    public void updateMenu(MenuInfo mi) {
        this.basedao.update(mi);
    }

    public Result deleteMenu(String objId) {
        //1.获取菜单
        MenuInfo mi = this.basedao.getById(MenuInfo.class, objId);
        //2.查找此菜单是否存在子菜单，如果存在则不能删除
        if (mi.getIsparent().equals("1")) {
            return BuildResult.buildOutResult(ResultEnum.ERROR, "该菜单存在子菜单，不能删除");
        }
        //3.执行删除
        mi.setDeleted(Constants.DELETED_STATE.IS_DELETED);
        this.basedao.update(mi);
        //4.根据parentid查找是否有同一个父级的菜单
        String parentid = mi.getParentId();
        if (StringUtils.isNotEmpty(parentid)) {
            // 查找同级菜单
            MenuInfo menu = new MenuInfo();
            menu.setParentId(mi.getParentId());
            List<MenuInfo> list = this.basedao.get(MenuInfo.class, menu);
            if (list.size() <= 0) { // 原来这个父级菜单有下级，现在没有了，就更新isparent的状态
                MenuInfo parentmenu = this.basedao.getById(MenuInfo.class, mi.getParentId());
                parentmenu.setIsparent("0");
                this.basedao.update(parentmenu);
            }
        }
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, "删除成功！");
    }
}
