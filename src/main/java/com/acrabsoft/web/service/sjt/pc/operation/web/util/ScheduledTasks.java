package com.acrabsoft.web.service.sjt.pc.operation.web.util;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.AppInfoEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.ServiceInfoEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppInfoService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.ServiceInfoService;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.entity.HbaseRowkeyMarkEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.HbaseRowkeyMarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@EnableScheduling
public class ScheduledTasks {
    Logger logger = LoggerFactory.getLogger( ScheduledTasks.class);

    @Value("${spring.profiles.active}")
    public String active;

    @Resource
    private HbaseRowkeyMarkService hbaseRowkeyMarkService;
    @Resource
    private AppInfoService appInfoService;
    @Resource
    private ServiceInfoService serviceInfoService;

    /**
     * rowKey 已存在对应关系的缓存  键 : value 值 : code
     */
    public static Map<String, String> rowKeyCityUseCache = new Hashtable<>();
    public static Map<String, String> rowKeyImeiUseCache = new Hashtable<>();
    public static Map<String, String> rowKeyAppUseCache = new Hashtable<>();
    public static Map<String, String> rowKeyServiceUseCache = new Hashtable<>();
    public static Map<String, String> rowKeyLoadUseCache = new Hashtable<>();

    /**
     * rowKey 不存在对应关系的缓存
     */
    public static Set<String> rowKeyCityNoUseCache = new HashSet<>();
    public static Set<String> rowKeyImeiNoUseCache = new HashSet<>();
    public static Set<String> rowKeyAppNoUseCache = new HashSet<>();
    public static Set<String> rowKeyServiceNoUseCache = new HashSet<>();
    public static Set<String> rowKeyLoadNoUseCache = new HashSet<>();



    /**
     * @description  每五分钟执行的定时任务
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public synchronized void refreshParams(){
        serviceInfoService.getAllList(  );
    }

    /**
     * @description  每五分钟执行的定时任务
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    public static Map<String, String> appInfoIdToName = new Hashtable<>();
    @Scheduled(cron = "0 */5 * * * ?")
    public synchronized void refreshAppInfo(){
        List<AppInfoEntity> allList = appInfoService.getAllList();
        for (int i = 0; i < allList.size(); i++) {
            AppInfoEntity appInfoEntity = allList.get( i );
            String appId = PowerUtil.getString( appInfoEntity.getAppId() );
            String appName = PowerUtil.getString( appInfoEntity.getAppName() );
            appInfoIdToName.put( appId,appName );
        }
    }

    /**
     * @description  每五分钟执行的定时任务
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    public static Map<String, String> serviceInfoIdToName = new Hashtable<>();
    @Scheduled(cron = "0 */5 * * * ?")
    public synchronized void refreshServiceInfo(){
        List<ServiceInfoEntity> allList = serviceInfoService.getAllList();
        for (int i = 0; i < allList.size(); i++) {
            ServiceInfoEntity serviceInfoEntity = allList.get( i );
            String appId = PowerUtil.getString( serviceInfoEntity.getServiceId() );
            String appName = PowerUtil.getString( serviceInfoEntity.getServiceName() );
            serviceInfoIdToName.put( appId,appName );
        }
    }

    /**
     * @description  每五分钟执行的定时任务
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    //@Scheduled(cron = "0 */10 * * * ?")
    public synchronized void refreshRowkeyMark(){
        logger.info("===========================>rowKey缓存开始<===========================");
        List<HbaseRowkeyMarkEntity> allList = hbaseRowkeyMarkService.getAllList();
        for (int i = 0; i < allList.size(); i++) {
            HbaseRowkeyMarkEntity hbaseRowkeyMarkEntity = allList.get( i );
            String type = hbaseRowkeyMarkEntity.getType();
            String rowkeyCode = hbaseRowkeyMarkEntity.getRowkeyCode();
            String rowkeyValue = PowerUtil.getString( hbaseRowkeyMarkEntity.getRowkeyValue() );
            if (ParamEnum.rowkeyType.type1.getCode().equals( type )) {
                if (PowerUtil.isNotNull( rowkeyValue )) {
                    rowKeyCityUseCache.put( rowkeyValue, rowkeyCode);
                }else{
                    /*if (rowKeyCityNoUseCache.size() < 100) {
                        rowKeyCityNoUseCache.add( rowkeyCode);
                    }*/
                    rowKeyCityNoUseCache.add( rowkeyCode);
                }
            }else if (ParamEnum.rowkeyType.type2.getCode().equals( type )) {
                if (PowerUtil.isNotNull( rowkeyValue )) {
                    rowKeyImeiUseCache.put( rowkeyValue, rowkeyCode);
                }else{
                    /*if (rowKeyImeiNoUseCache.size() < 100) {
                        rowKeyImeiNoUseCache.add( rowkeyCode);
                    }*/
                    rowKeyImeiNoUseCache.add( rowkeyCode);
                }
            }else if (ParamEnum.rowkeyType.type3.getCode().equals( type )) {
                if (PowerUtil.isNotNull( rowkeyValue )) {
                    rowKeyAppUseCache.put( rowkeyValue, rowkeyCode);
                }else{
                    /*if (rowKeyAppNoUseCache.size() < 100) {
                        rowKeyAppNoUseCache.add( rowkeyCode);
                    }*/
                    rowKeyAppNoUseCache.add( rowkeyCode);
                }
            }else if (ParamEnum.rowkeyType.type4.getCode().equals( type )) {
                if (PowerUtil.isNotNull( rowkeyValue )) {
                    rowKeyServiceUseCache.put( rowkeyValue, rowkeyCode);
                }else{
                    /*if (rowKeyServiceNoUseCache.size() < 100) {
                        rowKeyServiceNoUseCache.add( rowkeyCode);
                    }*/
                    rowKeyServiceNoUseCache.add( rowkeyCode);
                }
            }else if (ParamEnum.rowkeyType.type5.getCode().equals( type )) {
                if (PowerUtil.isNotNull( rowkeyValue )) {
                    rowKeyLoadUseCache.put( rowkeyValue, rowkeyCode);
                }else{
                    /*if (rowKeyLoadNoUseCache.size() < 100) {
                        rowKeyLoadNoUseCache.add( rowkeyCode);
                    }*/
                    rowKeyLoadNoUseCache.add( rowkeyCode);
                }
            }
        }
        logger.info("===========================>rowKey缓存结束<===========================");
    }


}
