package com.acrabsoft.web.service.sjt.pc.operation.web.system.controller;

import com.acrabsoft.web.annotation.NotAuthorize;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.SequenceService;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.acrabsoft.common.BuildResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import javax.annotation.Resource;

/**
* 序列表( SequenceController )控制类
* @author wanghbdeptName
* @since 2020-12-30 18:25:32
*/
@RestController
@RequestMapping("/system/sequence")
@Api(value = "序列表",  tags = "序列表")
public class SequenceController {

    private static Logger logger = Logger.getLogger( SequenceController.class );

    /**
    * 服务对象
    */
    @Resource
    private SequenceService sequenceService;


    /**
    * @description  分页查询
    * @param  pageNo  一页个数
    * @param  pageSize  页码
    * @return  返回结果
    * @date  20/09/05 8:04
    * @author  wanghb
    * @edit
    */
    /*@PostMapping("/getListPage")
    @ResponseBody
    @ApiOperation(value = "分页查询")
    public Result getListPage(@RequestParam(defaultValue = "1") @ApiParam("一页个数") int pageNo,
                              @RequestParam(defaultValue = "10") @ApiParam("页码") int pageSize) {
        Result result = sequenceService.getListPage( pageNo,pageSize);
        return result;
    }*/


    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    /*@GetMapping("/view")
    @ResponseBody
    @ApiOperation(value = "详情")
    public Result view(@RequestParam(name = "id", required = true) @ApiParam("主键id") String id) {
        Result result = sequenceService.view( id );
        return result;
    }*/

    /**
     * @description 详情
     * @return 实体对象
     * @date 2020-12-30 18:25:32
     * @author wanghb
     * @edit
     */
    @GetMapping("/getNumberByPrefix")
    @ResponseBody
    @NotAuthorize
    @ApiOperation(value = "获取序列")
    public Result getNumberByPrefix() {
        for (int i = 0; i < 10000; i++) {
            String result = sequenceService.getNum( "20210329"  );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
    * @description 保存或更新
    * @param sequenceInfo 实体
    * @return 无返回值
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/saveOrUpdate")
    @ResponseBody
    @ApiOperation(value = "保存或更新")
    public Result saveOrUpdate(@RequestBody SequenceInfo sequenceInfo) {
        Result result = sequenceService.saveOrUpdate( sequenceInfo );
        return result;
    }*/



    /**
    * @description 去保存页面
    * @return 实体对象
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    /*@GetMapping("/goSave")
    @ResponseBody
    @ApiOperation(value = "详情")
    public Result goSave() {
        Result result = sequenceService.goSave(  );
        return result;
    }*/


    /**
    * @description 保存
    * @param sequenceInfo 实体
    * @return 无返回值
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/save")
    @ResponseBody
    @ApiOperation(value = "保存")
    public Result save(@RequestBody SequenceInfo sequenceInfo) {
        Result result = sequenceService.save( sequenceInfo );
        return result;
    }*/


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    /*@GetMapping("/delete")
    @ResponseBody
    @ApiOperation(value = "删除")
    public Result delete(@RequestParam(name = "id", required = true) @ApiParam("主键id") String id) {
        Result result = sequenceService.delete( id );
        return result;
    }*/


    /**
    * @description 批量删除
    * @param ids 主键id
    * @return 实体对象
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/batchDelete")
    @ResponseBody
    @ApiOperation(value = "批量删除")
    public Result batchDelete(@RequestBody List<String> ids) {
        Result result = sequenceService.batchDelete( ids );
        return result;
    }*/


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    /*@GetMapping("/logicDelete")
    @ResponseBody
    @ApiOperation(value = "逻辑删除")
    public Result logicDelete(@RequestParam(name = "id", required = true) @ApiParam("主键id") String id) {
        Result result = sequenceService.logicDelete( id );
        return result;
    }*/


    /**
    * @description 批量逻辑删除
    * @param ids 主键id
    * @return 实体对象
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/batchLogicDelete")
    @ResponseBody
    @ApiOperation(value = "批量删除")
    public Result batchLogicDelete(@RequestBody List<String> ids) {
        Result result = sequenceService.batchLogicDelete( ids );
        return result;
    }*/


}
