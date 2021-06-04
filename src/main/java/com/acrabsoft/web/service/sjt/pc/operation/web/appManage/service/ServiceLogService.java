package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.ServiceLogPushEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.HbaseRowkeyMarkService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.SequenceService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
* 应用日志表( appLogService )服务实现类
* @author wanghb
* @since 2020-11-23 14:34:51
*/
@Service("serviceLogService")
public class ServiceLogService extends BaseController {
    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    private BaseDao baseDao;
    @Resource
    private HBaseService hBaseService;
    @Resource
    private SequenceService sequenceService;
    @Resource
    private HbaseRowkeyMarkService hbaseRowkeyMarkService;




    /**
     * @description  获取appCode
     * @param  appValue
     * @return  返回结果
     * @date  2021-4-12 16:40
     * @author  wanghb
     * @edit
     */
    @Transactional(rollbackOn = Exception.class)
    public synchronized String getAppCode(String appValue) {
        while (ScheduledTasks.rowKeyAppNoUseCache.size() == 0){
            try {
                Thread.sleep( 500 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String appCode = new ArrayList<>( ScheduledTasks.rowKeyAppNoUseCache).get( RandomUtil.getRandom( 0 ,ScheduledTasks.rowKeyAppNoUseCache.size() - 1) );
        ScheduledTasks.rowKeyAppNoUseCache.remove( appCode );
        ScheduledTasks.rowKeyAppUseCache.put( appValue,appCode );
        hbaseRowkeyMarkService.updateAppCode( appCode ,appValue, ParamEnum.rowkeyType.type3.getCode());
        return appCode;
    }

    /**
     * @description  获取appCode
     * @param  appValue
     * @return  返回结果
     * @date  2021-4-12 16:40
     * @author  wanghb
     * @edit
     */
    @Transactional(rollbackOn = Exception.class)
    public synchronized String getImeiCode(String appValue) {
        while (ScheduledTasks.rowKeyImeiNoUseCache.size() == 0){
            try {
                Thread.sleep( 500 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String appCode = new ArrayList<>( ScheduledTasks.rowKeyImeiNoUseCache).get( RandomUtil.getRandom( 0 ,ScheduledTasks.rowKeyImeiNoUseCache.size() - 1) );
        ScheduledTasks.rowKeyImeiNoUseCache.remove( appCode );
        ScheduledTasks.rowKeyImeiUseCache.put( appValue,appCode );
        hbaseRowkeyMarkService.updateAppCode( appCode ,appValue, ParamEnum.rowkeyType.type2.getCode());
        return appCode;
    }

    /**
     * @description  获取appCode
     * @param  serviceValue
     * @return  返回结果
     * @date  2021-4-12 16:40
     * @author  wanghb
     * @edit
     */
    @Transactional(rollbackOn = Exception.class)
    public synchronized String getServiceCode(String serviceValue) {
        while (ScheduledTasks.rowKeyServiceNoUseCache.size() == 0){
            try {
                Thread.sleep( 500 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String appCode = new ArrayList<>( ScheduledTasks.rowKeyServiceNoUseCache).get( RandomUtil.getRandom( 0 ,ScheduledTasks.rowKeyServiceNoUseCache.size() - 1) );
        ScheduledTasks.rowKeyServiceNoUseCache.remove( appCode );
        ScheduledTasks.rowKeyServiceUseCache.put( serviceValue,appCode );
        hbaseRowkeyMarkService.updateAppCode( appCode ,serviceValue, ParamEnum.rowkeyType.type4.getCode());
        return appCode;
    }


    /**
     * @description  获取appCode
     * @param  loadValue
     * @return  返回结果
     * @date  2021-4-12 16:40
     * @author  wanghb
     * @edit
     */
    @Transactional(rollbackOn = Exception.class)
    public synchronized String getLoadCode(String loadValue) {
        while (ScheduledTasks.rowKeyLoadNoUseCache.size() == 0){
            try {
                Thread.sleep( 500 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String appCode = new ArrayList<>( ScheduledTasks.rowKeyLoadNoUseCache).get( RandomUtil.getRandom( 0 ,ScheduledTasks.rowKeyLoadNoUseCache.size() - 1) );
        ScheduledTasks.rowKeyLoadNoUseCache.remove( appCode );
        ScheduledTasks.rowKeyLoadUseCache.put( loadValue,appCode );
        hbaseRowkeyMarkService.updateAppCode( appCode ,loadValue, ParamEnum.rowkeyType.type5.getCode());
        return appCode;
    }



    /**
     * @description  应用日志进行消费
     * @param  records
     * @return  返回结果
     * @date  2021-3-24 16:22
     * @author  wanghb
     * @edit
     */
    ExecutorService serviceLogReceiverThreadPool = Executors.newFixedThreadPool( 300 );
    @KafkaListener(topics = {"serviceLog"}, groupId = "kafka-producer" ,containerFactory="batchFactory")
    public void serviceLogReceiver(List<ConsumerRecord> records) {
        for (ConsumerRecord<Integer, String> record : records) {
            serviceLogReceiverThreadPool.execute( new Runnable() {
                @Override
                public void run(){
                    record.offset();
                    Map<String,Object> params = MapUtil.toMap(  record.value());

                    ServiceLogPushEntity serviceLogPushEntity = MapUtil.toBean( params,ServiceLogPushEntity.class );
                    String callTime = serviceLogPushEntity.getCallTime();
                    String callTimeShort = DateUtil.toShortString( callTime );
                    String appId = serviceLogPushEntity.getAppId();
                    String serviceId = serviceLogPushEntity.getServiceId();
                    String loadNum = serviceLogPushEntity.getLoadNum();
                    String appCode = ScheduledTasks.rowKeyAppUseCache.get( appId );
                    if(PowerUtil.isNull( appCode )){
                        appCode = getAppCode(appId);
                    }
                    String serviceCode = ScheduledTasks.rowKeyServiceUseCache.get( serviceId );
                    if(PowerUtil.isNull( serviceCode )){
                        serviceCode = getServiceCode(serviceId);
                    }
                    String loadCode = ScheduledTasks.rowKeyLoadUseCache.get( loadNum );
                    if(PowerUtil.isNull( loadCode )){
                        loadCode = getLoadCode(loadNum);
                    }
                    String serviceNum = sequenceService.getNum( ParamEnum.sequenceType.service.getCode() + callTimeShort);
                    String rowKey = new StringBuilder( appCode ).append( callTimeShort.replace( "-","" ) ).append( "-" ).append( serviceCode ).append( "~" ).append( loadCode ).append( serviceNum ).toString();
                    serviceLogPushEntity.setId( CodeUtils.getUUID32());
                    serviceLogPushEntity.setRowKey( rowKey );
                    //System.out.println("消费结束");
                }
            });
        }
    }


    /**
     * @description  保存
     * @param  params
     * @return  返回结果
     * @date  2021-4-12 16:49
     * @author  wanghb
     * @edit
     */
    public Result save(Map<String, Object> params) {
        List<ServiceLogPushEntity> list = new ArrayList<>();
        List<List<Object>> listDate = new ArrayList<>();
        listDate.add(Arrays.asList( "应用6","服务1","2021-01-11","16"));
        listDate.add(Arrays.asList( "应用6","服务1","2021-01-10","16"));
        listDate.add(Arrays.asList( "应用6","服务1","2021-01-09","16"));
        listDate.add(Arrays.asList( "应用6","服务1","2021-01-08","16"));
        listDate.add(Arrays.asList( "应用6","服务1","2021-01-07","16"));

        listDate.add(Arrays.asList( "应用7","服务2","2021-01-11","16"));
        listDate.add(Arrays.asList( "应用7","服务2","2021-01-10","16"));
        listDate.add(Arrays.asList( "应用7","服务2","2021-01-09","16"));
        listDate.add(Arrays.asList( "应用7","服务2","2021-01-08","16"));
        listDate.add(Arrays.asList( "应用7","服务2","2021-01-07","16"));

        listDate.add(Arrays.asList( "应用8","服务3","2021-01-11","16"));
        listDate.add(Arrays.asList( "应用8","服务3","2021-01-10","16"));
        listDate.add(Arrays.asList( "应用8","服务3","2021-01-09","16"));
        listDate.add(Arrays.asList( "应用8","服务3","2021-01-08","16"));
        listDate.add(Arrays.asList( "应用8","服务3","2021-01-07","16"));

        listDate.add(Arrays.asList( "应用9","服务4","2021-01-11","16"));
        listDate.add(Arrays.asList( "应用9","服务4","2021-01-10","16"));
        listDate.add(Arrays.asList( "应用9","服务4","2021-01-09","16"));
        listDate.add(Arrays.asList( "应用9","服务4","2021-01-08","16"));
        listDate.add(Arrays.asList( "应用9","服务4","2021-01-07","16"));

        listDate.add(Arrays.asList( "应用10","服务5","2021-01-11","16"));
        listDate.add(Arrays.asList( "应用10","服务5","2021-01-10","16"));
        listDate.add(Arrays.asList( "应用10","服务5","2021-01-09","16"));
        listDate.add(Arrays.asList( "应用10","服务5","2021-01-08","16"));
        listDate.add(Arrays.asList( "应用10","服务5","2021-01-07","16"));


        for (int x = 0; x < listDate.size(); x++) {
            List<Object> temp = listDate.get(x);
            Date date = DateUtil.toDate( PowerUtil.getString( temp.get( 2 ) ),DateUtil.DATE_SHORT);
            String nowDateStr = DateUtil.toString( date,DateUtil.DATE_SHORT );
            String appValue = PowerUtil.getString( temp.get( 0 ) );
            String serviceValue = PowerUtil.getString( temp.get( 1 ) );
            Integer transferSumCount = 0;
            //16个负载
            for (int i = 0; i < PowerUtil.getInt( temp.get( 3 ) ); i++) {
                for (int j = 1; j <= 24; j++) {
                    String loadValue = i + "";
                    String appCode = ScheduledTasks.rowKeyAppUseCache.get( appValue );
                    if(PowerUtil.isNull( appCode )){
                        appCode = getAppCode(appValue);
                    }
                    String serviceCode = ScheduledTasks.rowKeyServiceUseCache.get( serviceValue );
                    if(PowerUtil.isNull( serviceCode )){
                        serviceCode = getServiceCode(serviceValue);
                    }
                    String loadCode = ScheduledTasks.rowKeyLoadUseCache.get( loadValue );
                    if(PowerUtil.isNull( loadCode )){
                        loadCode = getLoadCode(loadValue);
                    }
                    Integer transferCount = RandomUtil.getRandom( 1,10 );
                    transferSumCount += transferCount;
                    for (int y = 0; y < transferCount; y++) {
                        String appLogNum = sequenceService.getNum( ParamEnum.sequenceType.service.getCode() + nowDateStr.replace( "-","" ) );
                        //XCD20210101-DSC~CD00000001
                        String rowKey = new StringBuilder( appCode ).append( nowDateStr.replace( "-","" ) ).append( "-" ).append( serviceCode ).append( "~" ).append( loadCode ).append( appLogNum ).toString();
                        ServiceLogPushEntity serviceLogPushEntity = new ServiceLogPushEntity();
                        serviceLogPushEntity.setId( CodeUtils.getUUID32());
                        serviceLogPushEntity.setRowKey( rowKey );

                        serviceLogPushEntity.setAppName(appValue);
                        serviceLogPushEntity.setAppId("");
                        serviceLogPushEntity.setUserId("");
                        serviceLogPushEntity.setServiceId(serviceValue);
                        serviceLogPushEntity.setLoadNum(loadValue);
                        serviceLogPushEntity.setCallTime(nowDateStr + " " + (j <= 9 ? "0" : "") + j+":00:00");
                        serviceLogPushEntity.setServiceAddress("http://"+serviceValue+RandomUtil.getRandom( 0,9 ));
                        serviceLogPushEntity.setRequestParam("");
                        serviceLogPushEntity.setRequestHeader("");
                        serviceLogPushEntity.setResponseTime(nowDateStr + " " + (j <= 9 ? "0" : "") + j+":00:00");
                        serviceLogPushEntity.setCallDuration(new BigDecimal( "16.86" ) );
                        serviceLogPushEntity.setIsError("true");
                        serviceLogPushEntity.setErrorInfo("");
                        serviceLogPushEntity.setResponseContent("");
                        serviceLogPushEntity.setResponseHeader("");
                        list.add(serviceLogPushEntity);
                    }
                    //System.out.println("负载编号:"+loadValue+" ,负载rowKey:"+loadCode+" ,应用编号:"+appValue+" ,应用rowKey:"+appCode+" ,服务编号:"+serviceValue+" ,服务rowKey:"+serviceCode);
                }
            }
            System.out.println("应用:"+appValue+" ,服务:"+serviceValue+" ,时间:"+nowDateStr+" ,调用次数:"+transferSumCount);
        }
        System.out.println("开始插入");
        hBaseService.batchSave( "serviceLog", MapUtil.toListMap( list ) );
        System.out.println("结束插入");
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list.size());
    }
}
