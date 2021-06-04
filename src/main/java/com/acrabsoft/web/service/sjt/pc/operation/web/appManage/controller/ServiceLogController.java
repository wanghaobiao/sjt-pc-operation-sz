package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.controller;

import com.acrabsoft.web.annotation.NotAuthorize;
import com.acrabsoft.web.pojo.user.BasicUser;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HiveService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.ServiceLogService;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.SequenceService;
import com.acrabsoft.web.utils.JwtUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
* 服务日志信息( AppLogController )控制类
* @author wanghbdeptName
* @since 2020-11-23 14:34:51
*/
@RestController
@RequestMapping("/appManage/serviceLog")
@Api(tags = "服务日志信息")
public class ServiceLogController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    /**
    * 服务对象
    */
    @Resource
    private ServiceLogService serviceLogService;

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
     * @description  日志推送
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @PostMapping("/logPush")
    @NotAuthorize
    public Result   testKafkaSendMsg(@RequestBody Map<String, Object> params){
        for (int i = 1; i <= 1; i++) {
            System.out.println(i);
            //发送消息

            ListenableFuture listenableFuture = kafkaTemplate.send( "serviceLog", "{\"id\":\"0A4A5F78F87640A4B4A9FC35829416DC\",\"appId\":\"fwzxapp1\",\"userId\":\"410184197910211212\",\"serviceId\":\"fwzxservice1\",\"loadNum\":\"1\",\"callTime\":\"2021-04-26 09:43:14\",\"serviceAddress\":\"http://127.0.0.1:20001/fwzx/v1.0.0/test\",\"requestParam\":\"fileId=15649013e00c4745b671cccc2fa2c8b5\",\"requestHeader\":\"{\\\"content-length\\\":\\\"25\\\",\\\"x-traceid\\\":\\\"A6VKZYef\\\",\\\"x-forwarded-proto\\\":\\\"http\\\",\\\"postman-token\\\":\\\"767fc0c9-6be6-4de6-a738-e86e2baf1ca5\\\",\\\"usercredential\\\":\\\"%7b%22credential%22%3a%7b%22head%22%3a%7b%22version%22%3a%221.0%22%2c%22credType%22%3a%221%22%2c%22token%22%3a%7b%22tokenId%22%3a%22a000000120123123123123123ec892ba%22%2c%22orgId%22%3a%22120000000000%22%2c%22exten%22%3a%22%22%7d%2c%22duration%22%3a%7b%22startTime%22%3a%222012312312231%22%2c%22endTime%22%3a%222012312312290%22%7d%7d%2c%22load%22%3a%7b%22userInfo%22%3a%7b%22userId%22%3a%22b000000120123123123123123ec892ba%22%2c%22orgId%22%3a%22120000000000%22%2c%22sfzh%22%3a%22410184197910211212%22%2c%22jh%22%3a%22999999%22%2c%22xm%22%3a%22%e9%9f%a9%e7%a7%80%e5%be%b7%22%2c%22exten%22%3a%22%22%7d%7d%2c%22serverSign%22%3a%7b%22alg%22%3a%22SM3%2bSM2%22%2c%22signature%22%3a%22b000000120123123123123123ec892ba%22%2c%22sn%22%3a%221000000000100211%22%2c%22url%22%3a%22http%3a%2f%2f20.3.1.166%2fUAS%2fCredential.do%22%7d%2c%22clientSign%22%3a%7b%22alg%22%3a%22SM3%2bSM2%22%2c%22signature%22%3a%22b000000120123123123123123ec892ba%22%2c%22sn%22%3a%221000000000100001%22%7d%7d%7d\\\",\\\"x-forwarded-port\\\":\\\"9083\\\",\\\"x-forwarded-for\\\":\\\"0:0:0:0:0:0:0:1\\\",\\\"forwarded\\\":\\\"proto=http;host=\\\\\\\"localhost:9083\\\\\\\";for=\\\\\\\"0:0:0:0:0:0:0:1:53417\\\\\\\"\\\",\\\"accept\\\":\\\"*/*\\\",\\\"authorization\\\":\\\"wm3YLxx6_TAt1FQKiVvr3meuFoxZdxLH7ZX0PVvxbY5QhQa94COk6dhLXmowVA-3l5mbAYE1O2XiC55anMlb9JeJt6naFULKmgUrTOj1uDaSBszvxkJCGV_NNiu6cAtQnihspBZMuxuJNjJ5xzP0jwdeYTeNjRLl3xi_1GJREV7-pLMmONPTsaJhemWnBA1euFA8sUpqluNj3P68Qr9GTmlB2FeMcCoPtktDadMIqcPXD90uO7ofGmrbW6rZ4T3z\\\",\\\"x-forwarded-host\\\":\\\"localhost:9083\\\",\\\"x-forwarded-prefix\\\":\\\"/fwzx\\\",\\\"appcredential\\\":\\\"%7b%22credential%22%3a%7b%22head%22%3a%7b%22version%22%3a%221.0%22%2c%22credType%22%3a%221%22%2c%22token%22%3a%7b%22tokenId%22%3a%22a000000120123123123123123ec892ba%22%2c%22orgId%22%3a%22120000000000%22%2c%22exten%22%3a%22%22%7d%2c%22duration%22%3a%7b%22startTime%22%3a%222012312312231%22%2c%22endTime%22%3a%222012312312290%22%7d%7d%2c%22load%22%3a%7b%22appInfo%22%3a%7b%22appId%22%3a%22fwzxapp1%22%2c%22orgId%22%3a%22120000000000%22%2c%22packageName%22%3a%22com.xdja.drs%22%2c+%22networkAreaCode%22%3a%221%22%2c%22name%22%3a%22%e5%ae%89%e5%85%a8%e5%ae%a2%e6%88%b7%e7%ab%af%22%2c%22csType%22%3a%221%22%2c%22exten%22%3a%22%22%7d%7d%2c%22serverSign%22%3a%7b%22alg%22%3a%22SM3%2bSM2%22%2c%22signature%22%3a%22b000000120123123123123123ec892ba%22%2c%22sn%22%3a%221000000000100211%22%2c%22url%22%3a%22http%3a%2f%2f20.3.1.166%2fUAS%2fCredential.do%22%7d%2c%22clientSign%22%3a%7b%22alg%22%3a%22SM3%2bSM2%22%2c%22signature%22%3a%22b000000120123123123123123ec892ba%22%2c%22sn%22%3a%221000000000100001%22%7d%7d%7d\\\",\\\"appid\\\":\\\"fwzxapp1\\\",\\\"host\\\":\\\"192.168.43.238:20001\\\",\\\"content-type\\\":\\\"application/x-www-form-urlencoded\\\",\\\"serviceid\\\":\\\"fwzxservice2\\\",\\\"accept-encoding\\\":\\\"gzip, deflate, br\\\",\\\"x-forwarded-proxy\\\":\\\"127.0.0.1/3\\\",\\\"user-agent\\\":\\\"PostmanRuntime/7.26.10\\\"}\",\"responseTime\":\"2021-04-26 09:43:14\",\"callDuration\":85,\"isError\":false,\"errorInfo\":\"\",\"responseContent\":\"{errcode=0, errmsg=请求成功, data=wm3YLxx6_TAt1FQKiVvr3meuFoxZdxLH7ZX0PVvxbY5QhQa94COk6dhLXmowVA-3l5mbAYE1O2XiC55anMlb9JeJt6naFULKmgUrTOj1uDaSBszvxkJCGV_NNiu6cAtQnihspBZMuxuJNjJ5xzP0jwdeYTeNjRLl3xi_1GJREV7D1kCxARbEZOM18qSN9D3_eBpEhMjI-lud5vL4Vzts3JEseKBuJSRPIxqB3lTvqdejE8G0CVpLF_tqfjrNorf1}\",\"responseHeader\":\"{\\\"Transfer-Encoding\\\":\\\"chunked\\\",\\\"Date\\\":\\\"Mon, 26 Apr 2021 01:43:14 GMT\\\",\\\"Content-Type\\\":\\\"application/json;charset=UTF-8\\\"}\"}");
            //ListenableFuture listenableFuture = kafkaTemplate.send( "serviceLog", "{\"id\":\"0A4A5F78F87640A4B4A9FC35829416DC\",\"appId\":\"fwzxapp1\",\"userId\":\"410184197910211212\",\"serviceId\":\"fwzxservice1\",\"loadNum\":\"1\",\"callTime\":\"2021-04-26 09:43:14\",\"serviceAddress\":\"http://127.0.0.1:20001/fwzx/v1.0.0/test\",\"requestParam\":\"fileId=15649013e00c4745b671cccc2fa2c8b5\",\"responseTime\":\"2021-04-26 09:43:14\",\"callDuration\":85,\"isError\":false,\"errorInfo\":\"\"}" );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }

    /**
    * @description   保存
    * @param params 实体
    * @return 无返回值
    * @date 2020-9-24 15:56:18
    * @author wanghb
    * @edit
    */
    @PostMapping("/save")
    @ResponseBody
    @ApiOperation(value = "保存", notes = "保存")
    public Result save(@RequestBody Map<String, Object> params) {
        return serviceLogService.save(params);
    }




    /**
     * @description  查询列表
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public Result list()  {
        List<Map<String, Object>> list = hiveJdbcTemplate.queryForList( "select * from hbase_students" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list);
    }



    public static void main(String[] args) {
        BasicUser basicUser = new BasicUser();
        basicUser.setUserid( "13629848747" );
        String jwtstr = JwtUtil.getJwtStrByPreFix( JSON.toJSONString(basicUser));
        System.out.println( jwtstr );
    }

}
