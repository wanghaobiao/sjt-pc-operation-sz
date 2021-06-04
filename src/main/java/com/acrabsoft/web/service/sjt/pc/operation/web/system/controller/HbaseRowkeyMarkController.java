package com.acrabsoft.web.service.sjt.pc.operation.web.system.controller;

import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.HbaseRowkeyMarkService;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.entity.*;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.BuildResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import javax.annotation.Resource;
import java.util.List;

/**
* hbaseRowkey标识对照表( HbaseRowkeyMarkController )控制类
* @author wanghbdeptName
* @since 2021-4-9 11:57:53
*/
@RestController
@RequestMapping("/system/hbaseRowkeyMark")
@Api(value = "hbaseRowkey标识对照表",  tags = "hbaseRowkey标识对照表")
public class HbaseRowkeyMarkController {

    private static Logger logger = Logger.getLogger( HbaseRowkeyMarkController.class );

    /**
    * 服务对象
    */
    @Resource
    private HbaseRowkeyMarkService hbaseRowkeyMarkService;


    /**
    * @description  分页查询
    * @param  pageNo  一页个数
    * @param  pageSize  页码
    * @return  返回结果
    * @date  20/09/05 8:04
    * @author  wanghb
    * @edit
    */
    @PostMapping("/getListPage")
    @ResponseBody
    @ApiOperation(value = "分页查询")
    public Result getListPage(@RequestParam(defaultValue = "1") @ApiParam("页码") int pageNo,
                              @RequestParam(defaultValue = "10") @ApiParam("一页个数") int pageSize) {
        Result result = hbaseRowkeyMarkService.getListPage( pageNo,pageSize);
        return result;
    }


    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    @GetMapping("/view")
    @ResponseBody
    @ApiOperation(value = "详情")
    public Result view(@RequestParam(name = "id", required =false) @ApiParam("主键id") String id) {
        Result result = hbaseRowkeyMarkService.view( id );
        return result;
    }


    /**
    * @description 保存或更新
    * @param hbaseRowkeyMarkentity 实体
    * @return 无返回值
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    @ApiOperation(value = "保存或更新")
    public Result saveOrUpdate(@RequestBody HbaseRowkeyMarkEntity hbaseRowkeyMarkentity) {
        Result result = hbaseRowkeyMarkService.saveOrUpdate( hbaseRowkeyMarkentity );
        return result;
    }



    /**
    * @description 去保存页面
    * @return 实体对象
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    /*@GetMapping("/goSave")
    @ResponseBody
    @ApiOperation(value = "详情")
    public Result goSave() {
        Result result = hbaseRowkeyMarkService.goSave(  );
        return result;
    }*/


    /**
    * @description 保存
    * @param hbaseRowkeyMarkentity 实体
    * @return 无返回值
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    @PostMapping("/save")
    @ResponseBody
    @ApiOperation(value = "保存")
    public Result save(@RequestBody HbaseRowkeyMarkEntity hbaseRowkeyMarkentity) {
        Result result = hbaseRowkeyMarkService.save( hbaseRowkeyMarkentity );
        return result;
    }


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    /*@GetMapping("/delete")
    @ResponseBody
    @ApiOperation(value = "删除")
    public Result delete(@RequestParam(name = "id", required =false) @ApiParam("主键id") String id) {
        Result result = hbaseRowkeyMarkService.delete( id );
        return result;
    }*/


    /**
    * @description 批量删除
    * @param ids 主键id
    * @return 实体对象
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/batchDelete")
    @ResponseBody
    @ApiOperation(value = "批量删除")
    public Result batchDelete(@RequestBody List<String> ids) {
        Result result = hbaseRowkeyMarkService.batchDelete( ids );
        return result;
    }*/


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    /*@GetMapping("/logicDelete")
    @ResponseBody
    @ApiOperation(value = "逻辑删除")
    public Result logicDelete(@RequestParam(name = "id", required =false) @ApiParam("主键id") String id) {
        Result result = hbaseRowkeyMarkService.logicDelete( id );
        return result;
    }*/


    /**
    * @description 批量逻辑删除
    * @param ids 主键id
    * @return 实体对象
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/batchLogicDelete")
    @ResponseBody
    @ApiOperation(value = "批量删除")
    public Result batchLogicDelete(@RequestBody List<String> ids) {
        Result result = hbaseRowkeyMarkService.batchLogicDelete( ids );
        return result;
    }*/


}
