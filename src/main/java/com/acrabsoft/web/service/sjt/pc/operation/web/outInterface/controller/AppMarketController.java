package com.acrabsoft.web.service.sjt.pc.operation.web.outInterface.controller;

import com.acrabsoft.web.annotation.NotAuthorize;
import com.acrabsoft.web.pojo.user.BasicUser;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppLogService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HiveService;
import com.acrabsoft.web.service.sjt.pc.operation.web.outInterface.service.AppMarketService;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.SequenceService;
import com.acrabsoft.web.utils.JwtUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
* 应用市场相关接口控制类
* @author wanghbdeptName
* @since 2020-11-23 14:34:51
*/
@RestController
@RequestMapping("/outInterface/appMarket")
@Api(tags = "应用市场相关接口")
public class AppMarketController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    /**
    * 服务对象
    */
    @Resource
    private AppMarketService appMarketService;
    @Resource
    protected HttpServletResponse response;
    @Resource
    protected HBaseService hBaseService;
    @Resource
    protected HiveService hiveService;
    @Resource
    protected SequenceService sequenceService;
    @Resource
    KafkaTemplate kafkaTemplate;
    @Autowired
    @Qualifier("hiveJdbcTemplate")
    private JdbcTemplate hiveJdbcTemplate;
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate oracleJdbcTemplate;
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * @description 上周应用使用情况
     * @param id 主键id
     * @return 实体对象
     * @date 2021-4-26 10:48:57
     * @author wanghb
     * @edit
     */
    @GetMapping("/lastWeekAppUseCount")
    @ResponseBody
    @NotAuthorize
    @ApiOperation(value = "上周应用使用情况")
    public Result lastWeekAppUseCount(@RequestParam(name = "id", required =false) @ApiParam("应用id") String id) {
        Result result = appMarketService.lastWeekAppUseCount( id );
        return result;
    }
}
