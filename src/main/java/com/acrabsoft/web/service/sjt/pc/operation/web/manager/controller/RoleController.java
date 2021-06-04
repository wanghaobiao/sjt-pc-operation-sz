package com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller;

import com.acrabsoft.web.pojo.user.BasicUser;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.constant.Constants;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.RoleInfo;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.service.LoginService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.service.RefreshService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.service.RoleService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.service.UserinfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.acrabsoft.utils.StringUtil;
import org.acrabsoft.utils.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "角色管理")
@RequestMapping("/role")
@RestController
public class RoleController extends BaseController {
    @Autowired
    private RefreshService refreshService;

    @Autowired
    private RoleService service;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserinfoService userinfoService;

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "更新/保存角色", nickname = "chenwang", notes = "更新/保存角色")
    public Result update(@RequestParam(required = false) @ApiParam("roleId") String roleId, @RequestParam(required = false) @ApiParam("角色名") String roleName,
                         @RequestParam(required = false) @ApiParam("角色描述") String roleDescription) {
        if (StringUtils.isNotEmpty(roleId)) {
            roleId = this.service.updateRole(roleId, roleName, roleDescription);
        } else {
            RoleInfo role = new RoleInfo();
            if (!StringUtil.isNullBlank(roleName)) {
                role.setRoleName(roleName);
            }
            if (!StringUtil.isNullBlank(roleDescription)) {
                role.setRoleDescription(roleDescription);
            }
            role.setCreateTime(new Date());
            role.setRoleId(UUIDUtil.getUUID32());
            role.setDeleted(Constants.DELETED_STATE.NOT_DELETED);
            roleId = this.service.saveRole(role);
        }
        Map<String, String> idMap = new HashMap<>();
        idMap.put("roleId", roleId);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, idMap);
    }


    @RequestMapping(value = "/adduser", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.GET})
    @ApiOperation(value = "添加关联用户", nickname = "chenwang", notes = "添加关联用户")
    public Result adduser(@RequestParam(defaultValue = "0") @ApiParam("授权天数") int days,
                          @RequestParam @ApiParam("角色id") String roleId,
                          @RequestParam @ApiParam("用户id数组") String[] users,
                          @RequestParam(required = false) @ApiParam("管辖的分局部门id") String branchDeptId,
                          @RequestParam(required = false) @ApiParam("管辖的分局部门名称") String branchDeptName
                          ) {
        String userId = getBaseUser().getUserid();
        Map<String, Object> userinfo = this.userinfoService.getUserinfobyuserid(userId);
        Map<String, Object> loginUserMap = this.loginService.getLoginUser(userinfo.get("idcard").toString());
        if (loginUserMap == null) {
            return BuildResult.buildOutResult(ResultEnum.ERROR, "登陆用户不存在！");
        }
        Integer roleUserCount = service.getRoleUserCount( roleId, users[0] );
        if (roleUserCount > 0) {
            return BuildResult.buildOutResult(ResultEnum.ERROR, "该用户已绑定此角色！");
        }
        this.service.adduser(roleId, users, userinfo, days, loginUserMap,branchDeptId,branchDeptName);
        this.service.adduserReFreshAuth(users);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, "添加完成");
    }

    @RequestMapping(value = "/getRoleList", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.GET})
    @ApiOperation(value = "查询角色列表", nickname = "chenwang", notes = "查询角色列表")
    public Result getRoleList(@RequestParam(required = false) @ApiParam("角色名") String roleName) {
        List<RoleInfo> list = this.service.getRoleList(roleName);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, list);
    }

    @RequestMapping(value = "/deluser", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.GET})
    @ApiOperation(value = "删除角色中的用户", nickname = "chenwang", notes = "删除角色中的用户")
    public Result deleteuser(@RequestParam @ApiParam("角色id") String roleId, @RequestParam @ApiParam("用户id") String userid) {
        BasicUser user = getBaseUser();
        String uid = user.getUserid();
        Map<String, Object> userinfo = this.userinfoService.getUserinfobyuserid(uid);
        Result result = this.service.deleteuser(roleId, userid, userinfo);
        if (result.getErrcode().equals("0")) {
            this.service.deleteuserRefreshAuth(userid);
        }
        return result;
    }

    @RequestMapping(value = "/getUserByRoleid", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.GET})
    @ApiOperation(value = "获取角色中的用户", nickname = "chenwang", notes = "获取角色中的用户")
    public Result getUserByRoleid(@RequestParam(required = false) @ApiParam("角色id") String roleId,
                                  @RequestParam(required = true) @ApiParam("姓名") String name,
                                  @RequestParam(required = true) @ApiParam("警号") String policenum,
                                  @RequestParam(required = true) @ApiParam("责任部门") String branchDeptName,
                                  @RequestParam(defaultValue = "1") @ApiParam("当前页码") int pageNum,
                                  @RequestParam(defaultValue = "5") @ApiParam("每页记录数") int pageSize) {
        Pagination p = this.service.getroleusers(roleId, name, pageNum, pageSize,policenum,branchDeptName);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, p);
    }

    @RequestMapping(value = "/getUserInfoByRoleAndDept", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "根据角色id和部门id获取用户", nickname = "chenwang", notes = "根据角色id和部门id获取用户")
    public Result getUserInfoByRoleAndDept(@RequestParam(required = false) @ApiParam("角色id") String roleId, @RequestParam(required = false) @ApiParam("部门id") String deptId,
                                           @RequestParam(required = false) @ApiParam("姓名（检索）") String xm, @RequestParam(required = false) @ApiParam("身份证（检索）") String idcard,
                                           @RequestParam(defaultValue = "1") @ApiParam("当前页码") int pageNum, @RequestParam(defaultValue = "5") @ApiParam("每页记录数") int pageSize,
                                           @RequestParam(required = false) @ApiParam("警号") String policenum) {
        Pagination p = this.service.getUserInfoByRoleAndDept(roleId, deptId, xm, idcard, pageNum, pageSize,policenum);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, p);
    }


    @RequestMapping(value = "/getDeptInfoByRole", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "根据部门id获取下级部门", nickname = "chenwang", notes = "根据部门id获取下级部门")
    public Result getDeptTreeById(@RequestParam(required = false) @ApiParam("部门id") String objId) {
        BasicUser user = getBaseUser();
        String uid = user.getUserid();
        Map<String, Object> userinfo = this.userinfoService.getUserinfobyuserid(uid);
        Map<String, Object> loginUserMap = this.loginService.getLoginUser(userinfo.get("idcard").toString());
        if (loginUserMap == null) {
            return BuildResult.buildOutResult(ResultEnum.ERROR, "登陆用户不存在！");
        }
        List<Map<String, Object>> list = this.service.getDeptTreeById(objId, loginUserMap);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, list);
    }


    @RequestMapping(value = "/removeBlackList", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "移除黑名单", nickname = "nj", notes = "移除黑名单")
    public Result removeBlackList(@RequestParam @ApiParam("身份证号") String idcard) {
//        this.service.removeBlackList(idcard);
        this.refreshService.refreshBlackList(idcard);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS);
    }

}
