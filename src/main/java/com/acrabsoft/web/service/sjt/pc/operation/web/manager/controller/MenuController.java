package com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller;

import com.acrabsoft.web.pojo.user.BasicUser;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.MenuInfo;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.service.MenuService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.service.UserinfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "菜单管理")
@RestController
@RequestMapping("/menuInfo")
public class MenuController extends BaseController {
    @Autowired
    private MenuService menuService;

    @Autowired
    private UserinfoService userservice;

    @RequestMapping(value = "/addMenu", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "新增菜单", nickname = "chenwang", notes = "新增菜单")
    public Result addMenu(@RequestParam @ApiParam("菜单名称") String menuName, @RequestParam @ApiParam("菜单路径") String menuPath,
                          @RequestParam(required = false) @ApiParam("父菜单id") String parentId, @RequestParam(required = false) @ApiParam("图标路径") String icon,
                          @RequestParam(required = false) @ApiParam("描述") String description, @RequestParam(required = false) @ApiParam("序号") String orderNum) {
        MenuInfo mi = new MenuInfo();
        mi.setMenuName(menuName);
        mi.setMenuPath(menuPath);
        mi.setParentId(parentId);
        mi.setIcon(icon);
        mi.setDescription(description);
        mi.setOrderNum(orderNum);
        BasicUser user = getBaseUser();
        mi.setCreateUser(user.getUserid());
        mi.setCreateUserName(user.getUsername());
        mi.setCreateTime(new Date());
        String id = this.menuService.addMenu(mi);
        if (StringUtils.isEmpty(id)) {
            return BuildResult.buildOutResult(ResultEnum.ERROR, "添加失败");
        }
        Map<String, String> idmap = new HashMap<>();
        idmap.put("id", id);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, idmap);
    }

    @RequestMapping(value = "/updateMenu", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "修改菜单", nickname = "chenwang", notes = "修改菜单")
    public Result udapteMenu(@RequestParam @ApiParam("菜单id") String objId, @RequestParam @ApiParam("菜单名称") String menuName,
                             @RequestParam @ApiParam("菜单路径") String menuPath, @RequestParam(required = false) @ApiParam("父菜单id") String parentId,
                             @RequestParam(required = false) @ApiParam("图标路径") String icon, @RequestParam(required = false) @ApiParam("描述") String description,
                             @RequestParam(required = false) @ApiParam("序号") String orderNum) {
        MenuInfo mi = new MenuInfo();
        mi.setObjId(objId);
        mi.setMenuName(menuName);
        mi.setMenuPath(menuPath);
        mi.setParentId(parentId);
        mi.setIcon(icon);
        mi.setDescription(description);
        mi.setOrderNum(orderNum);
        mi.setUpdateTime(new Date());
        this.menuService.updateMenu(mi);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, "ok");
    }

    @RequestMapping(value = "/deleteMenu", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "删除菜单", nickname = "chenwang", notes = "删除菜单")
    public Result deleteMenu(@RequestParam @ApiParam("菜单id") String objId) {
        return this.menuService.deleteMenu(objId);
    }


    /**
     * @description  首页数据
     * @return Result
     * @date 2020-9-23 10:17:21
     * @author wanghb
     * @edit
     */
    @GetMapping("/indexData")
    @ResponseBody
    @ApiOperation(value = "首页数据", notes = "首页数据")
    public Result indexData() {
        Result result = null;
        return result;
    }


}
