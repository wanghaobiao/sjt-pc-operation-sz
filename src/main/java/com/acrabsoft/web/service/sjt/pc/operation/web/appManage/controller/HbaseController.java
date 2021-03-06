package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.controller;

import com.acrabsoft.web.annotation.NotAuthorize;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppLogService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HiveService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.SequenceService;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.SystemService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.DateUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.MapUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.ExceptionUtil;
import org.apache.hadoop.hbase.util.MD5Hash;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.util.DigestUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
* ????????????( HbaseController )?????????
* @author wanghbdeptName
* @since 2020-11-23 14:34:51
*/
@RestController
@RequestMapping("/appManage/hbase")
@Api(tags = "????????????")
public class HbaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    protected HttpServletResponse response;
    @Resource
    protected HBaseService hBaseService;
    @Resource
    protected HiveService hiveService;
    @Resource
    @Qualifier("hiveJdbcTemplate")
    private JdbcTemplate hiveJdbcTemplate;
    @Resource
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate oracleJdbcTemplate;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private SequenceService sequenceService;

    @Resource
    private AppLogService appLogService;


    /**
    * @description   ?????????
    * @return ????????????
    * @date 2020-9-24 15:56:18
    * @author wanghb
    * @edit
    */
    @PostMapping("/addTable")
    @ResponseBody
    @ApiOperation(value = "?????????", notes = "?????????")
    public Result addTable() {
        //hBaseService.createTable( "serviceLog" );
        hBaseService.createTable( "appLogTemp" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
    * @description   ???????????????
    * @return ????????????
    * @date 2020-9-24 15:56:18
    * @author wanghb
    * @edit
    */
    @PostMapping("/addContactTable")
    @ResponseBody
    @ApiOperation(value = "???????????????", notes = "???????????????")
    public Result addContactTable() {
        hiveJdbcTemplate.update( "create external table hbase_appLog(key string, name string, code string,areaCode string, areaName string,deptCode string, deptName string,remark string, theCompanyCode string,theCompanyName string, responsible string,phoe string, type string,businessType string, deleted string,createUser string, createTime string,updateUser string, updateTime string) " +
                "row format serde 'org.apache.hadoop.hive.hbase.HBaseSerDe' " +
                "stored by 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' with " +
                "serdeproperties('hbase.columns.mapping'=':key,info:name,info:code,info:areaCode,info:areaName,info:deptCode,info:deptName,info:remark,info:theCompanyCode,info:theCompanyName,info:responsible,info:phoe,info:type,info:businessType,info:deleted,info:createUser,info:createTime,info:updateUser,info:updateTime') " +
                "tblproperties('hbase.table.name'='appLog')" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }

    /**
    * @description   ?????????
    * @return ????????????
    * @date 2020-9-24 15:56:18
    * @author wanghb
    * @edit
    */
    @PostMapping("/delTable")
    @ResponseBody
    @ApiOperation(value = "?????????", notes = "?????????")
    public Result delTable() {
        //hBaseService.delTable( "serviceLog" );
        hBaseService.delTable( "appLogTemp" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
     * @description  ????????????
     * @param
     * @return  ????????????
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @GetMapping("/hiveList")
    @ApiOperation(value = "????????????", notes = "????????????")
    public Result hiveList()  {
        List<Map<String, Object>> list = hiveJdbcTemplate.queryForList( "select * from hbase_appLog limit 0,10" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list);
    }

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @GetMapping("/stop")
    @NotAuthorize
    public void stop() {
        AppLogService.isOpen = false;
        if (AppLogService.appLogPushEntities != null && AppLogService.appLogPushEntities.size() != 0 ) {
            System.out.println("======================>?????????????????????:"+AppLogService.appLogPushEntities.size());
            hBaseService.batchSave( "appLogTemp", MapUtil.toListMap( AppLogService.appLogPushEntities ) );
        } else {
            System.out.println("======================>??????????????????");
        }
        AppLogService.appLogPushEntities = new ArrayList<>();

    }

    @GetMapping("/start")
    @NotAuthorize
    public void start() {
        new Thread(new Runnable(){
            @Override
            public void run(){
                AppLogService.isOpen = true;
                appLogService.appLogReceiver();
            }
        }).start();
    }


    /**
     * @description   ??????
     * @return ????????????
     * @date 2020-9-24 15:56:18
     * @author wanghb
     * @edit
     */
    @PostMapping("/hbaseSave")
    @ResponseBody
    @ApiOperation(value = "??????", notes = "??????")
    public Result hbaseSave() {
        return appLogService.save( new HashMap<>() );
    }




    /**
     * @description  ????????????
     * @param
     * @return  ???????????? SXDG20210331XC
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @GetMapping("/hbaseList")
    @ApiOperation(value = "????????????", notes = "????????????")
    public Result hbaseList()  {
        List<Map<String, String>> list = new ArrayList<>();
        Long appLogCount = 0L;
        //XCD-A20210101DSCX00000001
        //??????1	BBA-A ??????2	BBC-K ??????3	BBB-B ??????4	BBE-N ??????5	BBD-E
        //list = hBaseService.getListMap( "appLog", "BBA-A20210111.*");
        //list = hBaseService.getListMap( "serviceLog");
        //list = hBaseService.getListMapByScanRange("appLog", "1", "1000",  "info");
        //list = hBaseService.getListMap( "appLog", ".*XC000000.*");
        //list = hBaseService.getListMap( "appLog",null, "BBA-A202102", "BBA-A202103");

       appLogCount = hBaseService.getCount( "appLogTemp", ".*20210401.*" );
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool( 5 );


        /*String[] dates = new String[]{"20210111","20210110","20210109","20210108","20210107"};//
        String[] apps = new String[]{"1JC","J1S","0WS","EZ2","XU9"};
        for (int i = 0; i < apps.length; i++) {
            String app = apps[i];
            fixedThreadPool.execute( new Runnable() {
                @Override
                public void run() {
                    List<Map<String, String>> listTemp = hBaseService.getListMap( "serviceLog", app + ".*");
                    System.out.println(app + "=================>" + listTemp.size());
                }
            } );
            *//*for (int j = 0; j < dates.length; j++) {
                String date = dates[j];
                String pararm = app + date;
                fixedThreadPool.execute( new Runnable() {
                    @Override
                    public void run() {
                        List<Map<String, String>> listTemp = hBaseService.getListMap( "serviceLog", pararm + ".*");
                        System.out.println(pararm + "=================>" + listTemp.size());
                    }
                } );
            }*//*
        }*/
        System.out.println(appLogCount);
        System.out.println(list.size());
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list.size() != 0 ? list.get(0) : "");
    }


    /**
     * @description   ???????????????
     * @return ????????????
     * @date 2020-9-24 15:56:18
     * @author wanghb
     * @edit
     */
    @PostMapping("/delContactTable")
    @ResponseBody
    @ApiOperation(value = "???????????????", notes = "???????????????")
    public Result delContactTable() {
        hiveJdbcTemplate.update( "drop table hbase_appLog" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers","122.51.121.254:9092");
        props.put("group.id", "kafka-producer");
        /* ??????????????????offset */
        props.put("enable.auto.commit", "true");
        /* ????????????offset??????????????? */
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
//        props.put("auto.offset.reset", "earliest");
        props.put("auto.offset.reset", "latest");

        // ????????????
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("appLog"));

        try {
            Boolean isOpen = true;
            while (isOpen){
                ConsumerRecords<String, String> records = consumer.poll( 100);
                for (ConsumerRecord<String, String> record : records){
                    System.out.printf("???????????????topic=%s, partition=%d, offset=%d, key=%s, value=%s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }

            }
        } finally {
            consumer.close();
        }
    }

}
