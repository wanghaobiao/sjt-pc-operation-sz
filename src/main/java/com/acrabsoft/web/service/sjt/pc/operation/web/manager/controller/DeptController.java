package com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller;

import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.DeptInfoEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Deptinfo")
@Api(value = "部门管理",  tags = "部门管理")
public class DeptController {

    @Autowired
    private DeptService service;
    @Value("${njDeptId}")
    public String njDeptId;
    @RequestMapping(value = "/getDeptTree", method = {RequestMethod.POST})
    @ApiOperation(value = "获取部门结构树", nickname = "chenwang", notes = "获取部门结构树")
    public Result getDeptTree() {
        List<DeptInfoEntity> list = this.service.getDeptTree();
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, list);
    }

    @RequestMapping(value = "/getDeptByParentid", method = {RequestMethod.POST})
    @ApiOperation(value = "根据部门id获取下级部门", nickname = "chenwang", notes = "根据部门id获取下级部门")
    public Result getDeptTreeById(@RequestParam(required = false) @ApiParam("部门id") String objId) {
        List<DeptInfoEntity> list = this.service.getDeptTreeById(objId);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, list);
    }

    @RequestMapping(value = "/getUserByDeptid", method = {RequestMethod.POST})
    @ApiOperation(value = "根据部门获取用户信息", nickname = "chenwang", notes = "根据部门获取用户信息")
    public Result getUserByDeptid(@RequestParam(defaultValue = "1") @ApiParam("当前页码") int pageNum, @RequestParam(defaultValue = "10") @ApiParam("每页记录数") int pageSize,
                                  @RequestParam(required = false) @ApiParam("部门id") String deptid, @RequestParam(required = false) @ApiParam("用户名") String username) {
        Pagination p = this.service.getUserByDeptid(pageNum, pageSize, deptid, username);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, p);
    }


    /**
     * @description  分局部门下拉接口
     * @param
     * @return  返回结果
     * @date  20/09/23 11:25
     * @author  wanghb
     * @edit
     */
    @RequestMapping(value = "/getBranchDepts", method = {RequestMethod.GET})
    @ApiOperation(value = "分局部门下拉接口", notes = "分局部门下拉接口")
    public Result getBranchDepts() {
        List<Map<String, Object>> list = this.service.getDeptsByParentid(njDeptId);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, list);
    }

    /**
     * @description  分局部门map  键:id 值:name
     * @param
     * @return  返回结果
     * @date  20/09/23 11:25
     * @author  wanghb
     * @edit
     */
    @RequestMapping(value = "/getBranchDeptMap", method = {RequestMethod.GET})
    @ApiOperation(value = "分局部门下拉接口", notes = "分局部门下拉接口")
    public Result getBranchDeptMap() {
        Map<String, String> temp = this.service.getBranchDeptMap(njDeptId);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, temp);
    }
}
