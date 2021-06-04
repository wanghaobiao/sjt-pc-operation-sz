package com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller;

import com.acrabsoft.web.service.sjt.pc.operation.web.manager.service.UserinfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "用户管理")
@RequestMapping("/userinfo/")
@RestController
public class UserinfoController extends com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController {

    @Autowired
    private UserinfoService userInfoService;


    /**
     * @description  根据类型和证件号获取人员信息
     * @param  type  证件号类型  0 身份证号  1 警号
     * @param  number  证件号
     * @return
     * @date  2020-10-29 9:48
     * @author  wanghb
     * @edit
     */
    @GetMapping("/getByUserByType")
    @ResponseBody
    @ApiOperation(value = "根据类型和证件号获取人员信息", notes = "根据类型和证件号获取人员信息")
    public Result getByUserByType(@RequestParam(name = "type") @ApiParam("证件号类型  0 身份证号  1 警号") String type,
                                  @RequestParam(name = "number") @ApiParam("证件号") String number) {
        Result result = userInfoService.getByUserByType( type, number);
        return result;
    }


    /**
     * @description  获取所有人员名称和警号
     * @return
     * @date  2020-10-29 9:48
     * @author  wanghb
     * @edit
     */
    /*@GetMapping("/getNameAndPoliceNum")
    @ResponseBody
    @ApiOperation(value = "获取所有人员名称和警号", notes = "获取所有人员名称和警号")
    public Result getNameAndPoliceNum() {
        Result result = userInfoService.getNameAndPoliceNum( );
        return result;
    }*/



    /**
     * @description  获取人员信息
     * @param  pageNum
     * @param  pageSize
     * @param  policeNum
     * @param  userName
     * @return  返回结果
     * @date  2021-1-11 10:58
     * @author  wanghb
     * @edit
     */
    /*@RequestMapping(value = "/getUserList", method = {RequestMethod.POST})
    @ApiOperation(value = "根据部门获取用户信息", nickname = "chenwang", notes = "根据部门获取用户信息")
    public Result getUserList(@RequestParam(defaultValue = "1") @ApiParam("当前页码") int pageNum,
                              @RequestParam(defaultValue = "10") @ApiParam("每页记录数") int pageSize,
                              @RequestParam(required = false) @ApiParam("警号") String policeNum,
                              @RequestParam(required = false) @ApiParam("身份证号") String idCard,
                              @RequestParam(required = false) @ApiParam("用户名") String userName) {

        return userInfoService.getUserList(pageNum, pageSize, policeNum, userName,idCard);
    }*/


    /**
     * @description  根据证件号,身份证号,姓名模糊查询
     * @param  selectStr  证件号,身份证号,姓名
     * @return
     * @date  2020-10-29 9:48
     * @author  wanghb
     * @edit
     */
    @GetMapping("/userSelect")
    @ResponseBody
    @ApiOperation(value = "根据证件号,身份证号,姓名模糊查询获取人员信息", notes = "根据证件号,身份证号,姓名模糊查询获取人员信息")
    public Result getByUserByType( @RequestParam(name = "selectStr", required = false) @ApiParam("身份证号 警号 或 姓名") String selectStr) {
        Result result = userInfoService.userSelect(selectStr);
        return result;
    }

}
