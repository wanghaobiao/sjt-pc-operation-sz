package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;


import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao.LoginDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao.MenuInfoJPA;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.MenuInfo;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.UserLoginInfo;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.regexp.internal.RE;
import io.swagger.annotations.ApiOperation;
import org.acrabsoft.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class LoginService {

    @Autowired
    private MenuService menuService;

    @Autowired
    private LoginDao dao;


    @Autowired
    private MenuInfoJPA menuInfoJPA;

    public Map<String, Object> checkuser(String account) throws NoSuchAlgorithmException {
        //password = MD5Utils.md532(password);
        // 获取用户基本信息
        Map<String, Object> map = this.dao.checkuser(account);
        putMenus(map);
        return map;
    }

    /**
     * @description  根据用户信息，获取用户的角色以及角色关联的菜单信息
     * @param  map  用户信息
     * @return
     * @date  2020-11-4 16:13
     * @author  wanghb
     * @edit
     */
    public void putMenus(Map<String, Object> map) {
        if (map != null) {
            String userObjId = map.get("objId").toString();
            // 将角色的关联的菜单也一起查询出来
            List<MenuInfo> menuInfoList = menuInfoJPA.getUserRoledMenu(userObjId);

            JSONObject shuffleMenuList = this.menuService.shuffleMenuList(menuInfoList);
            map.put("menus", shuffleMenuList);

//            String ownMenuId = map.get("menuId") == null ? "" : map.get("menuId").toString();
//            if (!StringUtil.isNullBlank(ownMenuId)) {
//                MenuInfo menuInfo = new MenuInfo();
//                menuInfo.setObjId("000");
//                jsonArray = this.menuService.getChildMenus(menuInfo, ownMenuId);
//            }
//            map.put("router", jsonArray);
        }
    }

    public Map<String, Object> checkuserZslogin(String account) throws NoSuchAlgorithmException {
        //password = MD5Utils.md532(password);
        JSONArray jsonArray = new JSONArray();
        Map<String, Object> map = this.dao.checkuserZslogin(account);
        if (map != null) {
            String ownMenuId = map.get("menuId") == null ? "" : map.get("menuId").toString();
            if (!StringUtil.isNullBlank(ownMenuId)) {
                MenuInfo menuInfo = new MenuInfo();
                menuInfo.setObjId("000");
                jsonArray = this.menuService.getChildMenus(menuInfo, ownMenuId);
            }
            map.put("router", jsonArray);
        }
        return map;
    }

    public Map<String, Object> getLoginUser(String account) {
        return this.dao.checkuserZslogin(account);
    }

    /**
     * @description  保存登陆信息
     * @param  user  人员信息实体
     * @return
     * @date  2020-10-27 16:51
     * @author  wanghb
     * @edit
     */
    public UserLoginInfo getUserLogin(Map<String, Object> user) {
        String policeNum = PowerUtil.getString( user.get( "policenum" ) );
        String userId = PowerUtil.getString( user.get( "objId" ) );
        String idCard = PowerUtil.getString( user.get( "idcard" ) );
        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.setObjId( CodeUtils.getUUID32());
        userLoginInfo.setAccount(policeNum);
        userLoginInfo.setAuthlevelobj(policeNum);
        userLoginInfo.setLastLoginTime(new Date());
        userLoginInfo.setUserId(userId);
        userLoginInfo.setIdcard(idCard);
        userLoginInfo.setDeleted( ParamEnum.deleted.noDel.getCode());
        return userLoginInfo;
    }
}
