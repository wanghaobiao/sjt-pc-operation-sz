package com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller;

import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.ServiceDic;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.service.DicService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.acrabsoft.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "字典管理")
@RequestMapping("/dicmaneger/")
@RestController
public class DicController {

    @Autowired
    private DicService service;

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "更新/保存字典", nickname = "chenwang", notes = "更新保存字典")
    public Result update(@RequestParam(required = false) @ApiParam("id") String id,
                         @RequestParam(required = false) @ApiParam("字典编码") String code,
                         @RequestParam(required = false) @ApiParam("字典名称") String codename,
                         @RequestParam(required = false) @ApiParam("字典类型") String type) {
        ServiceDic sd = new ServiceDic();
        sd.setCode(code);
        sd.setCodename(codename);
        sd.setType(type);
        if (StringUtils.isEmpty(id)) {
            sd.setCreateTime(new Date());
            sd.setId(UUIDUtil.getUUID32());
            id = this.service.insertDic(sd);
        } else {
            sd.setId(id);
            id = this.service.updateDic(sd);
        }
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, id);
    }

    @RequestMapping(value = "/getDic", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "获取字典", nickname = "chenwang", notes = "获取字典")
    public Result getDicByType() {
        List<ServiceDic> datatypeList = this.service.getDicByType("datatype");
        List<ServiceDic> interfaceType = this.service.getDicByType("interfaceType");
        List<ServiceDic> databaseDriver = this.service.getDicByType("databaseDriver");
        List<ServiceDic> database = this.service.getDicByType("database");
        JSONObject jo = new JSONObject();
        jo.put("datatype", datatypeList);
        jo.put("interfaceType", interfaceType);
        jo.put("databaseDriver", databaseDriver);
        jo.put("database", database);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, jo);
    }


    /**
     * @description  获取字典根据类型
     * @param  type  字典类型
     * @return
     * @date  2020-11-4 9:54
     * @author  wanghb
     * @edit
     */
    @RequestMapping(value = "/getDicByType", method = { RequestMethod.GET})
    @ApiOperation(value = "获取字典根据类型", nickname = "chenwang", notes = "获取字典根据类型")
    public Result getDicByType(@RequestParam(name = "type", required = true) @ApiParam("类型") String type) {
        List<ServiceDic> serviceDics = this.service.getDicByType(type);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, serviceDics.size() == 0 ? null : serviceDics);
    }


    /**
     * @description  获取所有字典项
     * @return
     * @date  2020-11-4 9:54
     * @author  wanghb
     * @edit
     */
    @RequestMapping(value = "/getAllDic", method = { RequestMethod.GET})
    @ApiOperation(value = "获取所有字典项",  notes = "获取所有字典项")
    public Result getAllDic() {
        List<ServiceDic> serviceDics = this.service.getDicByType(null);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, serviceDics.size() == 0 ? null : serviceDics);
    }



    /**
     * @description 根据
     * @param type
     * @return 实体对象
     * @date 2020-9-25 16:04:40
     * @author wanghb
     * @edit
     */
    @GetMapping("/getSelectByType")
    @ResponseBody
    @ApiOperation(value = "根据类型", notes = "根据类型")
    public Result getSelectByType(@RequestParam(name = "type", required = true) @ApiParam("类型") String type) {
        List<ServiceDic> result = service.getDicByType( type );
        List<Map<String, Object>> list = new ArrayList<>();
        for (ServiceDic serviceDics : result) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("value",serviceDics.getCode());
            temp.put("name",serviceDics.getCodename());

            list.add(temp);
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list);
    }



}
