package com.acrabsoft.web.service.sjt.pc.operation.web.system.service;
import java.math.BigDecimal;
import java.util.Date;

import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.*;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppOpenCountService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.*;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.ApiOperation;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.datanucleus.store.rdbms.schema.SQLiteTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.acrabsoft.common.model.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.dao.TaskRecordDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.entity.*;
import com.acrabsoft.web.dao.base.BaseDao;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import org.acrabsoft.common.BuildResult;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
* 任务执行记录( TaskRecordService )服务类
* @author wanghb
* @since 2021-4-15 17:23:27
*/
/**
* 任务执行记录( TaskRecordService )服务实现类
* @author wanghb
* @since 2021-4-15 17:23:27
*/
@Service("taskRecordService")
public class TaskRecordService extends BaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    private TaskRecordDao taskRecordDao;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Resource
    private AppOpenCountService appOpenCountService;

    @Resource
    private BaseDao baseDao;

    @Resource
    private HBaseService hBaseService;

    /**
    * @description  分页查询
    * @param  pageNo  一页个数
    * @param  pageSize  页码
    * @return  返回结果
    * @date  20/09/05 8:13
    * @author  wanghb
    * @edit
    */
    public Result getListPage(int pageNo, int pageSize) {
        Pagination page = new Pagination(pageNo,pageSize);
        SQL sql = new SQL();
        sql.SELECT("l1.*");
        sql.FROM(TaskRecordEntity.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        /*if (PowerUtil.isNotNull( name )) {
            sql.WHERE(new StringBuilder( " l1.name like '%" ).append( name ).append( "%'" ).toString());
        }*/
        sql.ORDER_BY( "l1.create_time desc" );
        baseDao.getPaginationByNactiveSql( sql, page);
        List<TaskRecordEntity> rows = MapUtil.toListBean( page.getRows(),TaskRecordEntity.class );
        listPageStatus(rows);
        page.setRows( rows );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,page);
    }

    /**
     * @description  状态处理
     * @param  rows
     * @return  返回结果
     * @date  2021-4-16 18:00
     * @author  wanghb
     * @edit
     */
    public void listPageStatus(List<TaskRecordEntity> rows) {
        for (int i = 0; i < rows.size(); i++) {
            TaskRecordEntity temp = rows.get(i);
            if (ParamEnum.taskRecordStatus.status1.getCode().equals( temp.getStatus() ) && PowerUtil.isNull( temp.getDuration() )) {
                temp.setDuration( PowerUtil.getBigDecimal( System.currentTimeMillis() - temp.getStartDate().getTime() ).divide( new BigDecimal( "1000" ),2, BigDecimal.ROUND_CEILING  ) );
            }
        }
    }

    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    public Result view(String id) {
        TaskRecordEntity taskRecordEntity = this.baseDao.getById(TaskRecordEntity.class, id);
        if (taskRecordEntity != null) {
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,taskRecordEntity);
    }


    /**
    * @description 获取唯一
    * @param code
    * @return 实体对象
    * @date 2020-12-29 11:06:43
    * @author wanghb
    * @edit
    */
    public TaskRecordEntity getOne(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull( code )) {
            queryConditions.add( new QueryCondition("code", code));
        }
        List<TaskRecordEntity> list = baseDao.get(TaskRecordEntity.class, queryConditions);
        return list.size() > 0 ? list.get( 0 ) : null;
    }

    /**
    * @description 保存或更新
    * @param taskRecordEntity 实体
    * @return 无返回值
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result saveOrUpdate(TaskRecordEntity taskRecordEntity) {
        String id = taskRecordEntity.getId();
        Date nowDate = new Date();
        if (PowerUtil.isNull( id )) {
            id = CodeUtils.getUUID32();
            MapUtil.setCreateBean( taskRecordEntity, id, nowDate );
        } else {
            MapUtil.setUpdateBean( taskRecordEntity, nowDate );
        }
        this.baseDao.update( taskRecordEntity );
        this.taskRecordDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description  去保存页面
    * @return  返回结果
    * @date  2021-4-15 17:23:27
    * @author  wanghb
    * @edit
    */
    public Result goSave() {
        TaskRecordEntity taskRecordEntity = new TaskRecordEntity();
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,taskRecordEntity);
    }


    /**
    * @description 保存
    * @param taskRecordEntity 实体
    * @return 无返回值
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result save(TaskRecordEntity taskRecordEntity) {
        Result result = saveOrUpdate( taskRecordEntity );
        return result;
    }


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result delete(String id) {
        this.baseDao.delete(TaskRecordEntity.class, id);
        this.taskRecordDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchDelete(List<String> ids) {
        this.baseDao.delete(TaskRecordEntity.class, ids.toArray());
        this.taskRecordDao.batchDeleteDetail( ids );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result logicDelete(String id) {
        TaskRecordEntity taskRecordEntity = this.baseDao.getById(TaskRecordEntity.class, id);
        if (taskRecordEntity != null) {
            Date nowDate = new Date();
            taskRecordEntity.setDeleted( ParamEnum.deleted.yesDel.getCode() );
            MapUtil.setUpdateBean( taskRecordEntity, nowDate );
            this.baseDao.update( taskRecordEntity );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量逻辑删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchLogicDelete(List<String> ids) {
        this.taskRecordDao.batchLogicDelete(ids);
        this.taskRecordDao.batchLogicDeleteDetail(ids);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description  根据条件查询数量
    * @param  id  id
    * @return  查询数据
    * @date  2020-10-29 14:29
    * @author  wanghb
    * @edit
    */
    public Integer getCount(String id) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull( id )) {
            queryConditions.add(new QueryCondition("id",QueryCondition.NOEQ, id));
        }
        /*if (PowerUtil.isNotNull(  )) {
            queryConditions.add(new QueryCondition("", ));
        }*/
        Integer count = baseDao.getCount( TaskRecordEntity.class, queryConditions);
        return count;
    }

    @Autowired
    private PlatformTransactionManager platformTransactionManager;


    /**
     * @description  统计应用启动次数
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public static ExecutorService fixedThreadPool = Executors.newFixedThreadPool( 10 );
    @Transactional(rollbackOn = Exception.class)
    public Result appOpenCount(Map<String, Object> params) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        fixedThreadPool.execute( new Runnable() {
            Date date = new Date();
            @Override
            public void run() {
                //设置子线程共享
                RequestContextHolder.setRequestAttributes(servletRequestAttributes,true);
                TaskRecordEntity taskRecordEntity = saveTaskRecordThread(ParamEnum.taskRecord.appOpenCount,params,date);
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = platformTransactionManager.getTransaction(def);
                try {
                    String startDate = PowerUtil.getString( params.get( "startDate" ) );
                    String endDate = PowerUtil.getString( params.get( "endDate" ) );
                    startDate = "2021-01-07";
                    endDate = "2021-01-11";
                    startDate = "2021-04-01";
                    endDate = "2021-04-05";
                    //键 : 日期 + 应用名称 + imei  值 : AppOpenCountEntity
                    Map<String, AppOpenCountEntity> appOpenCountCache = new HashMap<>();
                    //键 : 日期(分时) + 应用名称 值 : AppOpenCountEntity
                    Map<String, AppHourOpenCountEntity> appHourOpenCountCache = new HashMap<>();
                    //键 : 日期(分时) + 应用名称 值 : AppOpenCountEntity
                    Map<String, AppHourActiveCountEntity> appHourActiveCountCache = new HashMap<>();
                    //键 : 日期(分时) + 应用名称 + 设备型号 值 : AppDeviceCountEntity
                    Map<String, AppDeviceCountEntity> appDeviceCountCache = new HashMap<>();
                    //键 : 日期(分时) + 应用名称 + 应用版本 值 : AppVersionCountEntity
                    Map<String, AppVersionCountEntity> appVersionCountCache = new HashMap<>();
                    List<String> middleDate = DateUtil.getMiddleDate( DateUtil.toDate( startDate, DateUtil.DATE_SHORT ), DateUtil.toDate( endDate, DateUtil.DATE_SHORT ) );
                    BigDecimal schedule = middleDate.size() == 0 ? new BigDecimal( "100" ) : new BigDecimal( "100" ).divide(  PowerUtil.getBigDecimal( middleDate.size() ),0, BigDecimal.ROUND_CEILING );
                    for (int i = 0; i < middleDate.size(); i++) {
                        String openDate = middleDate.get(i);
                        System.out.println("查询条件=====================>"+".*"+openDate.replaceAll( "-","" )+".*");
                        List<Map<String, String>> list = hBaseService.getListMap( "appLog", ".*"+openDate.replaceAll( "-","" )+".*");
                        System.out.println("结果条数=====================>"+list.size());
                        for (int j = 0; j < list.size(); j++) {
                            Map<String, String> temp = list.get(j);
                            String imei = PowerUtil.getString( temp.get( "imei" ) );
                            String appName = PowerUtil.getString( temp.get( "name" ) );
                            String startTime = PowerUtil.getString( temp.get( "startTime" ) );
                            String deviceModel = PowerUtil.getString( temp.get( "deviceModel" ) );
                            String versionCount = PowerUtil.getString( temp.get( "versionNum" ) );
                            String startTimeHour = startTime.substring( 0,13 );
                            /*==========================================按天统计开始==========================================*/
                            String appOpenCountCacheKey = new StringBuilder(openDate ).append( appName ).append( imei ).toString();
                            if (appOpenCountCache.containsKey( appOpenCountCacheKey )) {
                                AppOpenCountEntity appOpenCountEntity = appOpenCountCache.get( appOpenCountCacheKey );
                                appOpenCountEntity.setOpenCount( appOpenCountEntity.getOpenCount() + 1 );
                            }else{
                                AppOpenCountEntity appOpenCountEntity = new AppOpenCountEntity();

                                String areaName = PowerUtil.getString( temp.get( "areaName" ) );
                                appOpenCountEntity.setAppName(appName);
                                appOpenCountEntity.setOpenDate(DateUtil.toDate( openDate,DateUtil.DATE_SHORT ));
                                appOpenCountEntity.setAreaName(areaName);
                                appOpenCountEntity.setImei(imei);
                                appOpenCountEntity.setOpenCount(1);
                                appOpenCountEntity.setDeviceModel(deviceModel);
                                MapUtil.setCreateBean( appOpenCountEntity, CodeUtils.getUUID32(), date );
                                appOpenCountCache.put( appOpenCountCacheKey,appOpenCountEntity );
                            }
                            /*==========================================按天统计结束==========================================*/

                            /*==========================================按天统计终端开始==========================================*/
                            String appDeviceCountCacheKey = new StringBuilder(openDate ).append( appName ).append( deviceModel ).toString();
                            if (appDeviceCountCache.containsKey( appDeviceCountCacheKey )) {
                                AppDeviceCountEntity appDeviceCountEntity = appDeviceCountCache.get( appDeviceCountCacheKey );
                                appDeviceCountEntity.setDeviceCount( appDeviceCountEntity.getDeviceCount() + 1 );
                            }else{
                                AppDeviceCountEntity appDeviceCountEntity = new AppDeviceCountEntity();
                                appDeviceCountEntity.setAppName(appName);
                                appDeviceCountEntity.setOpenDate(DateUtil.toDate( openDate,DateUtil.DATE_SHORT ));
                                appDeviceCountEntity.setDeviceCount(1);
                                appDeviceCountEntity.setDeviceModel(deviceModel);
                                MapUtil.setCreateBean( appDeviceCountEntity, CodeUtils.getUUID32(), date );
                                appDeviceCountCache.put( appDeviceCountCacheKey,appDeviceCountEntity );
                            }
                            /*==========================================按天统计结束==========================================*/

                            /*==========================================按天统计应用版本开始==========================================*/
                            String appVersionCountCacheKey = new StringBuilder(openDate ).append( appName ).append( versionCount ).toString();
                            if (appVersionCountCache.containsKey( appVersionCountCacheKey )) {
                                AppVersionCountEntity appVersionCountEntity = appVersionCountCache.get( appVersionCountCacheKey );
                                appVersionCountEntity.setVersionCount( appVersionCountEntity.getVersionCount() + 1 );
                            }else{
                                AppVersionCountEntity appVersionCountEntity = new AppVersionCountEntity();
                                appVersionCountEntity.setAppName(appName);
                                appVersionCountEntity.setOpenDate(DateUtil.toDate( openDate,DateUtil.DATE_SHORT ));
                                appVersionCountEntity.setVersionCount(1);
                                appVersionCountEntity.setVersionNum(versionCount);
                                MapUtil.setCreateBean( appVersionCountEntity, CodeUtils.getUUID32(), date );
                                appVersionCountCache.put( appVersionCountCacheKey,appVersionCountEntity );
                            }
                            /*==========================================按天统计应用版本结束==========================================*/

                            /*==========================================分时统计开始==========================================*/
                            String appHourOpenCountCacheKey = new StringBuilder(startTimeHour ).append( appName ).toString();
                            if (appHourOpenCountCache.containsKey( appHourOpenCountCacheKey )) {
                                AppHourOpenCountEntity appHourOpenCountEntity = appHourOpenCountCache.get( appHourOpenCountCacheKey );
                                appHourOpenCountEntity.setOpenCount( appHourOpenCountEntity.getOpenCount() + 1 );
                            }else{
                                AppHourOpenCountEntity appHourOpenCountEntity = new AppHourOpenCountEntity();

                                String areaName = PowerUtil.getString( temp.get( "areaName" ) );

                                appHourOpenCountEntity.setAppName(appName);
                                appHourOpenCountEntity.setOpenDate(DateUtil.toDate( startTimeHour +":00:00",DateUtil.DATE_LONG ));
                                appHourOpenCountEntity.setAreaName(areaName);
                                appHourOpenCountEntity.setOpenCount(1);
                                appHourOpenCountEntity.setDeviceModel(deviceModel);
                                MapUtil.setCreateBean( appHourOpenCountEntity, CodeUtils.getUUID32(), date );
                                appHourOpenCountCache.put( appHourOpenCountCacheKey,appHourOpenCountEntity );
                            }
                            /*==========================================分时统计结束==========================================*/

                            /*==========================================分时活跃用户统计开始==========================================*/
                            String appHourActiveCountCacheKey = new StringBuilder(startTimeHour ).append( appName ).toString();
                            if (appHourActiveCountCache.containsKey( appHourActiveCountCacheKey )) {
                                AppHourActiveCountEntity appHourActiveCountEntity = appHourActiveCountCache.get( appHourActiveCountCacheKey );
                                Set<String> imeiSet = appHourActiveCountEntity.getImeiSet();
                                imeiSet.add( imei );
                            }else{
                                AppHourActiveCountEntity appHourActiveCountEntity = new AppHourActiveCountEntity();

                                String areaName = PowerUtil.getString( temp.get( "areaName" ) );

                                appHourActiveCountEntity.setAppName(appName);
                                appHourActiveCountEntity.setOpenDate(DateUtil.toDate( startTimeHour +":00:00",DateUtil.DATE_LONG ));
                                appHourActiveCountEntity.setAreaName(areaName);
                                appHourActiveCountEntity.setImeiSet(new HashSet<>(Arrays.asList( imei )));
                                appHourActiveCountEntity.setDeviceModel(deviceModel);
                                MapUtil.setCreateBean( appHourActiveCountEntity, CodeUtils.getUUID32(), date );
                                appHourActiveCountCache.put( appHourActiveCountCacheKey,appHourActiveCountEntity );
                            }
                            /*==========================================分时活跃用户统计结束==========================================*/
                        }
                        taskRecordEntity.setSchedule( PowerUtil.getInt(  middleDate.size() - 1 == i ? 95 : schedule.multiply( PowerUtil.getBigDecimal( i + 1 ) ) ) );
                        upDateTaskRecordThread(taskRecordEntity);
                    }
                    /*==========================================按天统计开始==========================================*/
                    List<AppOpenCountEntity> appOpenCountEntities = new ArrayList<>();
                    for(Map.Entry<String, AppOpenCountEntity> entry : appOpenCountCache.entrySet()){
                        AppOpenCountEntity mapValue = entry.getValue() ;
                        appOpenCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( AppOpenCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate ,DateUtil.DATE_SHORT))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate ,DateUtil.DATE_SHORT)) );
                    namedParameterJdbcTemplate.batchUpdate( AppOpenCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( appOpenCountEntities ) );
                    /*==========================================按天统计结束==========================================*/

                    /*==========================================按天统计终端开始==========================================*/
                    List<AppDeviceCountEntity> appDeviceCountEntities = new ArrayList<>();
                    for(Map.Entry<String, AppDeviceCountEntity> entry : appDeviceCountCache.entrySet()){
                        AppDeviceCountEntity mapValue = entry.getValue() ;
                        appDeviceCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( AppDeviceCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate ,DateUtil.DATE_SHORT))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate ,DateUtil.DATE_SHORT)) );
                    namedParameterJdbcTemplate.batchUpdate( AppDeviceCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( appDeviceCountEntities ) );
                    /*==========================================按天统计终端结束==========================================*/

                    /*==========================================按天统计应用版本开始==========================================*/
                    List<AppVersionCountEntity> appVersionCountEntities = new ArrayList<>();
                    for(Map.Entry<String, AppVersionCountEntity> entry : appVersionCountCache.entrySet()){
                        AppVersionCountEntity mapValue = entry.getValue() ;
                        appVersionCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( AppVersionCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate ,DateUtil.DATE_SHORT))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate ,DateUtil.DATE_SHORT)) );
                    namedParameterJdbcTemplate.batchUpdate( AppVersionCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( appVersionCountEntities ) );
                    /*==========================================按天统计应用版本结束==========================================*/

                    /*==========================================分时统计开始==========================================*/
                    List<AppHourOpenCountEntity> appHourOpenCountEntities = new ArrayList<>();
                    for(Map.Entry<String, AppHourOpenCountEntity> entry : appHourOpenCountCache.entrySet()){
                        AppHourOpenCountEntity mapValue = entry.getValue() ;
                        appHourOpenCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( AppHourOpenCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate ,DateUtil.DATE_SHORT))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate ,DateUtil.DATE_SHORT)) );
                    namedParameterJdbcTemplate.batchUpdate( AppHourOpenCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( appHourOpenCountEntities ) );
                    /*==========================================分时统计结束==========================================*/

                    /*==========================================分时活跃用户统计开始==========================================*/
                    List<AppHourActiveCountEntity> appHourActiveCountEntities = new ArrayList<>();
                    for(Map.Entry<String, AppHourActiveCountEntity> entry : appHourActiveCountCache.entrySet()){
                        AppHourActiveCountEntity mapValue = entry.getValue() ;
                        mapValue.setOpenCount( mapValue.getImeiSet().size()  );
                        //mapValue.setOpenCount( RandomUtil.getRandom(6,15) );
                        appHourActiveCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( AppHourActiveCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate ,DateUtil.DATE_SHORT))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate ,DateUtil.DATE_SHORT)) );
                    namedParameterJdbcTemplate.batchUpdate( AppHourActiveCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( appHourActiveCountEntities ) );
                    /*==========================================分时活跃用户统计结束==========================================*/

                    taskRecordEntity.setEndDate( new Date() );
                    taskRecordEntity.setDuration( (PowerUtil.getBigDecimal( System.currentTimeMillis()  - date.getTime() )).divide( new BigDecimal( "1000" ) ) );
                    taskRecordEntity.setSchedule( 100 );
                    taskRecordEntity.setStatus( ParamEnum.taskRecordStatus.status2.getCode() );
                    upDateTaskRecordThread(taskRecordEntity);
                    platformTransactionManager.commit(status);
                } catch (Exception e) {
                    taskRecordEntity.setStatus( ParamEnum.taskRecordStatus.status3.getCode() );
                    taskRecordEntity.setErrorInfo( e.getMessage() );
                    e.printStackTrace();
                    upDateTaskRecordThread(taskRecordEntity);
                }

            }
        } );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
     * @description  统计应用启动次数
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public static ExecutorService serviceThreadPool = Executors.newFixedThreadPool( 10 );
    @Transactional(rollbackOn = Exception.class)
    public Result serviceOpenCount(Map<String, Object> params) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        serviceThreadPool.execute( new Runnable() {
            Date date = new Date();
            @Override
            public void run() {
                //设置子线程共享
                RequestContextHolder.setRequestAttributes(servletRequestAttributes,true);
                TaskRecordEntity taskRecordEntity = saveTaskRecordThread(ParamEnum.taskRecord.serviceOpenCount,params,date);
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = platformTransactionManager.getTransaction(def);
                try {
                    String startDate = PowerUtil.getString( params.get( "startDate" ) );
                    String endDate = PowerUtil.getString( params.get( "endDate" ) );
                    startDate = "2021-01-07";
                    endDate = "2021-01-11";
                    //键 : 日期 + 应用名称 + 负载编号  值 : AppOpenCountEntity
                    Map<String, ServiceOpenCountEntity> serviceOpenCountCache = new HashMap<>();
                    //键 : 日期(分时) + 应用名称  值 : ServiceHourOpenCountEntity
                    Map<String, ServiceHourOpenCountEntity> serviceHourOpenCountCache = new HashMap<>();
                    List<String> middleDate = DateUtil.getMiddleDate( DateUtil.toDate( startDate, DateUtil.DATE_SHORT ), DateUtil.toDate( endDate, DateUtil.DATE_SHORT ) );
                    BigDecimal schedule = middleDate.size() == 0 ? new BigDecimal( "100" ) : new BigDecimal( "100" ).divide(  PowerUtil.getBigDecimal( middleDate.size() ),0, BigDecimal.ROUND_CEILING );
                    for (int i = 0; i < middleDate.size(); i++) {
                        String openDate = middleDate.get(i);
                        System.out.println("查询条件=====================>"+".*"+openDate.replaceAll( "-","" )+".*");
                        List<Map<String, String>> list = hBaseService.getListMap( "serviceLog", ".*"+openDate.replaceAll( "-","" )+".*");
                        System.out.println("结果条数=====================>"+list.size());
                        for (int j = 0; j < list.size(); j++) {
                            Map<String, String> temp = list.get(j);
                            String appName = PowerUtil.getString( temp.get( "appName" ) );
                            String serviceId = PowerUtil.getString( temp.get( "serviceId" ) );
                            String loadNum = PowerUtil.getString( temp.get( "loadNum" ) );
                            String callTime = PowerUtil.getString( temp.get( "callTime" ) );
                            String callTimeHour = callTime.substring( 0,13 );
                            Boolean isError = Boolean.parseBoolean(  temp.get( "isError" ) );
                            /*==========================================按天统计开始==========================================*/
                            String serviceOpenCountCacheKey = new StringBuilder(openDate ).append( appName ).append( loadNum ).toString();
                            if (serviceOpenCountCache.containsKey( serviceOpenCountCacheKey )) {
                                ServiceOpenCountEntity serviceOpenCountEntity = serviceOpenCountCache.get( serviceOpenCountCacheKey );
                                if (isError) {
                                    serviceOpenCountEntity.setErrorOpenCount( serviceOpenCountEntity.getErrorOpenCount() + 1 );
                                }else{
                                    serviceOpenCountEntity.setOpenCount( serviceOpenCountEntity.getOpenCount() + 1 );
                                }
                            }else{
                                ServiceOpenCountEntity serviceOpenCountEntity = new ServiceOpenCountEntity();
                                String areaName = PowerUtil.getString( temp.get( "areaName" ) );
                                serviceOpenCountEntity.setAppName(appName);
                                serviceOpenCountEntity.setServiceName( serviceId );
                                serviceOpenCountEntity.setOpenDate(DateUtil.toDate( openDate,DateUtil.DATE_SHORT ));
                                serviceOpenCountEntity.setAreaName(areaName);
                                serviceOpenCountEntity.setLoadNum( loadNum );
                                if (isError) {
                                    serviceOpenCountEntity.setErrorOpenCount( 1 );
                                    serviceOpenCountEntity.setOpenCount( 0 );
                                }else{
                                    serviceOpenCountEntity.setOpenCount( 1 );
                                    serviceOpenCountEntity.setErrorOpenCount( 0 );
                                }
                                MapUtil.setCreateBean( serviceOpenCountEntity, CodeUtils.getUUID32(), date );
                                serviceOpenCountCache.put( serviceOpenCountCacheKey,serviceOpenCountEntity );
                            }
                            /*==========================================按天统计结束==========================================*/

                            /*==========================================分时统计开始==========================================*/
                            String serviceHourOpenCountCacheKey = new StringBuilder(openDate ).append( appName ).append( callTimeHour ).toString();
                            if (serviceHourOpenCountCache.containsKey( serviceHourOpenCountCacheKey )) {
                                ServiceHourOpenCountEntity serviceHourOpenCountEntity = serviceHourOpenCountCache.get( serviceHourOpenCountCacheKey );
                                if (isError) {
                                    serviceHourOpenCountEntity.setErrorOpenCount( serviceHourOpenCountEntity.getErrorOpenCount() + 1 );
                                }else{
                                    serviceHourOpenCountEntity.setOpenCount( serviceHourOpenCountEntity.getOpenCount() + 1 );
                                }
                            }else{
                                ServiceHourOpenCountEntity serviceHourOpenCountEntity = new ServiceHourOpenCountEntity();
                                String areaName = PowerUtil.getString( temp.get( "areaName" ) );
                                serviceHourOpenCountEntity.setAppName(appName);
                                serviceHourOpenCountEntity.setServiceName( serviceId );
                                serviceHourOpenCountEntity.setOpenDate(DateUtil.toDate( callTimeHour + ":00:00",DateUtil.DATE_LONG ));
                                serviceHourOpenCountEntity.setAreaName(areaName);
                                if (isError) {
                                    serviceHourOpenCountEntity.setErrorOpenCount( 1 );
                                    serviceHourOpenCountEntity.setOpenCount( 0 );
                                }else{
                                    serviceHourOpenCountEntity.setOpenCount( 1 );
                                    serviceHourOpenCountEntity.setErrorOpenCount( 0 );
                                }
                                MapUtil.setCreateBean( serviceHourOpenCountEntity, CodeUtils.getUUID32(), date );
                                serviceHourOpenCountCache.put( serviceHourOpenCountCacheKey,serviceHourOpenCountEntity );
                            }
                            /*==========================================分时统计结束==========================================*/
                        }
                        taskRecordEntity.setSchedule( PowerUtil.getInt(  middleDate.size()-1 == i ? 95 : schedule.multiply( PowerUtil.getBigDecimal( i + 1 ) ) ) );
                        upDateTaskRecordThread(taskRecordEntity);
                    }
                    List<ServiceOpenCountEntity> serviceOpenCountEntities = new ArrayList<>();
                    for(Map.Entry<String, ServiceOpenCountEntity> entry : serviceOpenCountCache.entrySet()){
                        ServiceOpenCountEntity mapValue = entry.getValue() ;
                        serviceOpenCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( ServiceOpenCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate ,DateUtil.DATE_SHORT))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate ,DateUtil.DATE_SHORT)) );
                    namedParameterJdbcTemplate.batchUpdate( ServiceOpenCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( serviceOpenCountEntities ) );

                    List<ServiceHourOpenCountEntity> serviceHourOpenCountEntities = new ArrayList<>();
                    for(Map.Entry<String, ServiceHourOpenCountEntity> entry : serviceHourOpenCountCache.entrySet()){
                        ServiceHourOpenCountEntity mapValue = entry.getValue() ;
                        serviceHourOpenCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( ServiceHourOpenCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate ,DateUtil.DATE_SHORT))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate ,DateUtil.DATE_SHORT)) );
                    namedParameterJdbcTemplate.batchUpdate( ServiceHourOpenCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( serviceHourOpenCountEntities ) );

                    taskRecordEntity.setEndDate( new Date() );
                    taskRecordEntity.setDuration( (PowerUtil.getBigDecimal( System.currentTimeMillis()  - date.getTime() )).divide( new BigDecimal( "1000" ) ) );
                    taskRecordEntity.setSchedule( 100 );
                    taskRecordEntity.setStatus( ParamEnum.taskRecordStatus.status2.getCode() );
                    upDateTaskRecordThread(taskRecordEntity);
                    platformTransactionManager.commit(status);
                } catch (Exception e) {
                    taskRecordEntity.setStatus( ParamEnum.taskRecordStatus.status3.getCode() );
                    taskRecordEntity.setErrorInfo( e.getMessage() );
                    e.printStackTrace();
                    upDateTaskRecordThread(taskRecordEntity);
                }

            }
        } );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }

    /**
     * @description  统计应用启动次数
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appOpenCountEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        date = "2021-01-11";

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum );
        if (PowerUtil.isNotNull( date )){
            sql.append( " AND OPEN_DATE = "+JdbcTemplateUtil.getOracelToDate( date ));
        }
        sql.append( "  group by APP_NAME ) temp order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            seriesData.add( openCount );
            xAxisData.add( appName );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  业务统计饼状图Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result businessPieEcharts(Map<String, Object> params) {
        String businessName = PowerUtil.getString( params.get( "businessName" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (PowerUtil.isNotNull( businessName )){
            sql.append( " AND category_name = '"+businessName + "'");
        }
        sql.append( " GROUP BY APP_NAME ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<Map<String, Object>> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            Map<String, Object> series = new HashMap<>();
            series.put("name",appName);
            series.put("value",openCount);
            seriesData.add( series );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  业务统计排行Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result businessRankEcharts(Map<String, Object> params) {
        String businessName = PowerUtil.getString( params.get( "businessName" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (PowerUtil.isNotNull( businessName )){
            sql.append( " AND CATEGORY_NAME = '"+businessName + "'");
        }
        sql.append( " GROUP BY APP_NAME ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> yAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            seriesData.add( openCount );
            xAxisData.add( appName );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  统计应用启动Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result businessLineECharts(Map<String, Object> params) {
        String businessName = PowerUtil.getString( params.get( "businessName" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum1 );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (PowerUtil.isNotNull( businessName )){
            sql.append( " AND CATEGORY_NAME = '"+businessName + "'");
        }
        sql.append( " GROUP BY APP_NAME,OPEN_DATE ) ORDER BY openDate desc " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        Set<String> xAxisDataSet = new HashSet<>();
        Set<String> legendDataSet = new HashSet<>();
        //键 : 应用名称 + 日期  值 : 启动次数
        Map<String, String> seriesDataCache = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            String openDate = PowerUtil.getString( temp.get( "openDate" ) );
            seriesDataCache.put( appName + openDate, openCount );
            xAxisDataSet.add( openDate );
            legendDataSet.add( appName );
        }

        List<String> xAxisData = new ArrayList<>(xAxisDataSet);
        List<String> legendData = new ArrayList<>(legendDataSet);
        List<Map<String, Object>> seriesData = new ArrayList<>();
        for (int j = 0; j < legendData.size(); j++) {
            String legendTemp = legendData.get(j);
            Map<String, Object> series = new HashMap<>();
            series.put("name",legendTemp);
            series.put("type","line");
            series.put("smooth","true");
            List<String> seriesList = new ArrayList<>();
            for (int i = xAxisData.size() - 1; i >= 0; i--) {
                String xAxisTemp = xAxisData.get(i);
                seriesList.add( seriesDataCache.get( legendTemp + xAxisTemp)  );
            }
            series.put( "data" ,seriesList );
            seriesData.add(series);

        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("legendData",legendData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  业务统计饼状图Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result policePieEcharts(Map<String, Object> params) {
        String policeName = PowerUtil.getString( params.get( "policeName" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (PowerUtil.isNotNull( policeName )){
            sql.append( " AND police_name = '"+policeName + "'");
        }
        sql.append( " GROUP BY APP_NAME ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<Map<String, Object>> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            Map<String, Object> series = new HashMap<>();
            series.put("name",appName);
            series.put("value",openCount);
            seriesData.add( series );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  业务统计排行Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result policeRankEcharts(Map<String, Object> params) {
        String policeName = PowerUtil.getString( params.get( "policeName" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (PowerUtil.isNotNull( policeName )){
            sql.append( " AND police_name = '"+policeName + "'");
        }
        sql.append( " GROUP BY APP_NAME ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> yAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            seriesData.add( openCount );
            xAxisData.add( appName );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  统计应用启动Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result policeLineECharts(Map<String, Object> params) {
        String policeName = PowerUtil.getString( params.get( "policeName" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum1 );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (PowerUtil.isNotNull( policeName )){
            sql.append( " AND police_name = '"+policeName + "'");
        }
        sql.append( " GROUP BY APP_NAME,OPEN_DATE ) ORDER BY openDate desc " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        Set<String> xAxisDataSet = new HashSet<>();
        Set<String> legendDataSet = new HashSet<>();
        //键 : 应用名称 + 日期  值 : 启动次数
        Map<String, String> seriesDataCache = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            String openDate = PowerUtil.getString( temp.get( "openDate" ) );
            seriesDataCache.put( appName + openDate, openCount );
            xAxisDataSet.add( openDate );
            legendDataSet.add( appName );
        }

        List<String> xAxisData = new ArrayList<>(xAxisDataSet);
        List<String> legendData = new ArrayList<>(legendDataSet);
        List<Map<String, Object>> seriesData = new ArrayList<>();
        for (int j = 0; j < legendData.size(); j++) {
            String legendTemp = legendData.get(j);
            Map<String, Object> series = new HashMap<>();
            series.put("name",legendTemp);
            series.put("type","line");
            series.put("smooth","true");
            List<String> seriesList = new ArrayList<>();
            for (int i = xAxisData.size() - 1; i >= 0; i--) {
                String xAxisTemp = xAxisData.get(i);
                seriesList.add( seriesDataCache.get( legendTemp + xAxisTemp)  );
            }
            series.put( "data" ,seriesList );
            seriesData.add(series);

        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("legendData",legendData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  活跃用户环比
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appActiveChainRatioEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );

        startDate = "2021-01-07";
        endDate = "2021-01-11";

        StringBuilder sql = new StringBuilder(AppOpenCountEntity.countSql2);
        sql.append( " AND APP_NAME = '"+appName+"'");
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sql.append( " GROUP BY open_date " );
        sql.append( " ORDER BY open_date ASC " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
            if (i == 0) {
                continue;
            }
            value = value - PowerUtil.getIntValue( list.get(i - 1).get( "value" ) );
            String name = PowerUtil.getString( temp.get( "name" ) );
            yAxisMin = value < yAxisMin ? value : yAxisMin;
            seriesData.add( value );
            xAxisData.add( name );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  新增用户环比
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appAddChainRatioEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );

        startDate = "2021-01-08";
        endDate = "2021-01-11";

        StringBuilder sql = new StringBuilder(AppOpenCountEntity.countSql3);
        sql.append( " AND APP_NAME = '"+appName+"'");
        sql.append( " AND OPEN_DATE < "+JdbcTemplateUtil.getOracelToDate( startDate ));
        List<Map<String, Object>> historyImeiList = jdbcTemplate.queryForList( sql.toString() );
        Set<String> historyImeiSet = new HashSet<>();
        for (int i = 0; i < historyImeiList.size(); i++) {
            Map<String, Object> temp = historyImeiList.get( i );
            String imei = PowerUtil.getString( temp.get( "imei" ) );
            historyImeiSet.add( imei );
        }

        sql = new StringBuilder(AppOpenCountEntity.countSql3);
        sql.append( " AND APP_NAME = '"+appName+"'");
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));

        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        //键 : 日期  值 : imei Set
        Map<String, Set<String>> dateCache = new TreeMap<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get( i );
            String openDate = PowerUtil.getString( temp.get( "openDate" ) );
            String imei = PowerUtil.getString( temp.get( "imei" ) );
            if (!dateCache.containsKey( openDate )) {
                dateCache.put( openDate , new HashSet<>(Arrays.asList( imei )));
            }else{
                Set<String> imeisTemp = dateCache.get( openDate );
                imeisTemp.add( imei );
            }
        }

        List<String> xAxisData = new ArrayList<>();
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = 0 ;

        for(Map.Entry<String, Set<String>> entry : dateCache.entrySet()){
            String mapKey = entry.getKey();
            Set<String> mapValue =  entry.getValue() ;
            Integer value = 0;
            for (String imei : mapValue) {
                if (!historyImeiSet.contains( imei )) {
                    value ++;
                    historyImeiSet.add(imei);
                }
            }
            seriesData.add( value );
            xAxisData.add( mapKey );
        }

        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  终端机型分析
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appDeviceEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );

        startDate = "2021-04-01";
        endDate = "2021-04-05";

        StringBuilder sql = new StringBuilder(AppDeviceCountEntity.countSql1);
        sql.append( " AND APP_NAME = '"+appName+"'");
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sql.append( " ORDER BY open_date ASC " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = DateUtil.getMiddleDate( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        Set<String> yAxisDataSet = new HashSet<>();
        //键 : 日期 + 终端型号  值 : 数量
        Map<String, Integer> deviceCountCache = new HashMap<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openDate = PowerUtil.getString( temp.get( "openDate" ) );
            Integer deviceCount = PowerUtil.getInt( temp.get( "deviceCount" ) );
            String deviceModel = PowerUtil.getString( temp.get( "deviceModel" ) );
            String deviceCountkey = openDate + deviceModel;
            yAxisDataSet.add( deviceModel );
            deviceCountCache.put( deviceCountkey,deviceCount );
        }

        List<String> yAxisData = new ArrayList<>(yAxisDataSet);
        for (int i = 0; i < yAxisData.size(); i++) {
            String deviceModel = yAxisData.get( i );
            Map<String, Object> seriesTemp = new HashMap<>();
            seriesTemp.put("name",deviceModel);
            seriesTemp.put("type","bar");
            seriesTemp.put("stack","total");
            seriesTemp.put("label", ImmutableMap.of("show","true"));
            seriesTemp.put("emphasis", ImmutableMap.of("focus","series"));
            List<Integer> data = new ArrayList<>();
            for (int j = 0; j < xAxisData.size(); j++) {
                String openDate = xAxisData.get(j);
                String deviceCountkey = openDate + deviceModel;
                Integer deviceCount = PowerUtil.getInt( deviceCountCache.get( deviceCountkey ) );
                data.add( deviceCount );
            }
            seriesTemp.put("data" ,data);
            seriesData.add( seriesTemp );

        }
        //yAxisMin = value < yAxisMin ? value : yAxisMin;
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  应用版本分析
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appVersionEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );

        startDate = "2021-04-01";
        endDate = "2021-04-05";

        StringBuilder sql = new StringBuilder(AppVersionCountEntity.countSql1);
        sql.append( " AND APP_NAME = '"+appName+"'");
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sql.append( " ORDER BY open_date ASC " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = DateUtil.getMiddleDate( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        Set<String> yAxisDataSet = new HashSet<>();
        //键 : 日期 + 终端型号  值 : 数量
        Map<String, Integer> versionCountCache = new HashMap<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openDate = PowerUtil.getString( temp.get( "openDate" ) );
            Integer versionCount = PowerUtil.getInt( temp.get( "versionCount" ) );
            String versionNum = PowerUtil.getString( temp.get( "versionNum" ) );
            String versionCountkey = openDate + versionNum;
            yAxisDataSet.add( versionNum );
            versionCountCache.put( versionCountkey,versionCount );
        }

        List<String> yAxisData = new ArrayList<>(yAxisDataSet);
        for (int i = 0; i < yAxisData.size(); i++) {
            String versionNum = yAxisData.get( i );
            Map<String, Object> seriesTemp = new HashMap<>();
            seriesTemp.put("name",versionNum);
            seriesTemp.put("type","bar");
            seriesTemp.put("stack","total");
            seriesTemp.put("label", ImmutableMap.of("show","true"));
            seriesTemp.put("emphasis", ImmutableMap.of("focus","series"));
            List<Integer> data = new ArrayList<>();
            for (int j = 0; j < xAxisData.size(); j++) {
                String openDate = xAxisData.get(j);
                String versionCountkey = openDate + versionNum;
                Integer versionCount = PowerUtil.getInt( versionCountCache.get( versionCountkey ) );
                data.add( versionCount );
            }
            seriesTemp.put("data" ,data);
            seriesData.add( seriesTemp );

        }
        //yAxisMin = value < yAxisMin ? value : yAxisMin;
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  一天内的启动趋势折线图
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appUserFrequencyEcharts(Map<String, Object> params) {
        String openDate = PowerUtil.getString( params.get( "openDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );
        openDate = "2021-01-07";
        StringBuilder sql = new StringBuilder(AppHourActiveCountEntity.countSql1);
        sql.append( " AND APP_NAME = '"+appName+"'");
        sql.append( " AND "+JdbcTemplateUtil.getOracelToChar( "open_date","%Y-%m-%d" ) + " = '"+openDate+"'");
        sql.append( " order by open_date asc " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
            String name = PowerUtil.getString( temp.get( "name" ) );
            yAxisMin = value < yAxisMin ? value : yAxisMin;
            seriesData.add( value );
            xAxisData.add( name );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  一天内活跃的启动趋势折线图
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appActiveFrequencyEcharts(Map<String, Object> params) {
        String openDate = PowerUtil.getString( params.get( "openDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );

        StringBuilder sql = new StringBuilder(AppHourOpenCountEntity.countSql1);
        sql.append( " AND APP_NAME = '"+appName+"'");
        sql.append( " AND "+JdbcTemplateUtil.getOracelToChar( "open_date",DateUtil.DATE_SHORT ) + " = '"+openDate+"'");
        sql.append( " order by open_date asc " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
            String name = PowerUtil.getString( temp.get( "name" ) );
            yAxisMin = value < yAxisMin ? value : yAxisMin;
            seriesData.add( value );
            xAxisData.add( name );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  一天内的服务启动趋势折线图
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result serviceUserFrequencyEcharts(Map<String, Object> params) {
        String openDate = PowerUtil.getString( params.get( "openDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );

        StringBuilder sql = new StringBuilder(ServiceHourOpenCountEntity.countSql1);
        sql.append( " AND APP_NAME = '"+appName+"'");
        sql.append( " AND "+JdbcTemplateUtil.getOracelToChar( "open_date","%Y-%m-%d" ) + " = '"+openDate+"'");
        sql.append( " order by open_date asc " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> yAxisData = Arrays.asList("异常调用", "正常调用" );
        List<Map<String, Object>> seriesData = new ArrayList<>();
        Map<String, Object> seriesErrTemp = new HashMap<>();
        seriesErrTemp.put("name","异常调用");
        seriesErrTemp.put("type","bar");
        seriesErrTemp.put("stack","total");
        seriesErrTemp.put("label", ImmutableMap.of("show","true"));
        seriesErrTemp.put("emphasis", ImmutableMap.of("focus","series"));
        List<Integer> errorData = new ArrayList<>();
        seriesErrTemp.put("data" ,errorData);
        seriesData.add( seriesErrTemp );

        Map<String, Object> seriesTemp = new HashMap<>();
        seriesTemp.put("name","正常调用");
        seriesTemp.put("type","bar");
        seriesTemp.put("stack","total");
        seriesTemp.put("label", ImmutableMap.of("show","true"));
        seriesTemp.put("emphasis", ImmutableMap.of("focus","series"));
        List<Integer> data = new ArrayList<>();
        seriesTemp.put("data" ,data);
        seriesData.add( seriesTemp );

        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
            Integer errorValue = PowerUtil.getIntValue( temp.get( "errorValue" ) );
            String name = PowerUtil.getString( temp.get( "name" ) );
            data.add( value );
            errorData.add( errorValue );
            xAxisData.add( name );

        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  服务负载情况统计
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result serviceLoadHappenEcharts(Map<String, Object> params) {
        String openDate = PowerUtil.getString( params.get( "openDate" ) );

        StringBuilder sql = new StringBuilder("select * from (").append( ServiceOpenCountEntity.countSql3 );
        if (PowerUtil.isNotNull( openDate )) {
            sql.append( " AND "+JdbcTemplateUtil.getOracelToChar( "open_date","%Y-%m-%d" ) + " = '"+openDate+"'");
        }
        sql.append( " group by load_num " ).append( ") temp" );
        sql.append( " order by \"value\" desc " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> yAxisData = Arrays.asList("异常调用", "正常调用" );
        List<Map<String, Object>> seriesData = new ArrayList<>();
        Map<String, Object> seriesErrTemp = new HashMap<>();
        seriesErrTemp.put("name","异常调用");
        seriesErrTemp.put("type","bar");
        seriesErrTemp.put("stack","total");
        seriesErrTemp.put("label", ImmutableMap.of("show","true"));
        seriesErrTemp.put("emphasis", ImmutableMap.of("focus","series"));
        List<Integer> errorData = new ArrayList<>();
        seriesErrTemp.put("data" ,errorData);
        seriesData.add( seriesErrTemp );

        Map<String, Object> seriesTemp = new HashMap<>();
        seriesTemp.put("name","正常调用");
        seriesTemp.put("type","bar");
        seriesTemp.put("stack","total");
        seriesTemp.put("label", ImmutableMap.of("show","true"));
        seriesTemp.put("emphasis", ImmutableMap.of("focus","series"));
        List<Integer> data = new ArrayList<>();
        seriesTemp.put("data" ,data);
        seriesData.add( seriesTemp );

        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
            Integer errorValue = PowerUtil.getIntValue( temp.get( "errorValue" ) );
            String loadNum = PowerUtil.getString( temp.get( "loadNum" ) );
            data.add( value );
            errorData.add( errorValue );
            xAxisData.add( loadNum );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  多日天内的启动趋势折线图
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appOpenFrequencyEcharts(Map<String, Object> params) {
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );

        StringBuilder sql = new StringBuilder(AppOpenCountEntity.countSql1);
        sql.append( " AND APP_NAME = '"+appName+"'");
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sql.append( " GROUP BY open_date " );
        sql.append( " ORDER BY open_date ASC " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
            String name = PowerUtil.getString( temp.get( "name" ) );
            yAxisMin = value < yAxisMin ? value : yAxisMin;
            seriesData.add( value );
            xAxisData.add( name );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  活跃用户排名
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result activeUserEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        date = "2021-01-11";

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.countSql );
        if (PowerUtil.isNotNull( date )){
            sql.append( " AND OPEN_DATE = "+JdbcTemplateUtil.getOracelToDate( date ));
        }
        sql.append( "  group by aPP_NAME ) temp order by imeiCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String imeiCount = PowerUtil.getString( temp.get( "imeiCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            seriesData.add( imeiCount );
            xAxisData.add( appName );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  应用用户总量排名Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result userCountEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        date = "2021-01-11";

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.countSql );
        sql.append( " and open_date = " + JdbcTemplateUtil.getOracelToDate( date ) );
        sql.append( " group by APP_NAME ) temp order by imeiCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String imeiCount = PowerUtil.getString( temp.get( "imeiCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            seriesData.add( imeiCount );
            xAxisData.add( appName );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  应用地区分布Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appAreaEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        date = "2021-01-11";
        StringBuilder sql = new StringBuilder( TaskRecordEntity.countSql1);
        sql.append( " and open_date = " + JdbcTemplateUtil.getOracelToDate( date ) );
        sql.append( " group by area_name " );
        List<Map<String, Object>> seriesData = jdbcTemplate.queryForList( sql.toString() );
        Integer visualMapMin = seriesData.size() > 0 ? PowerUtil.getIntValue(seriesData.get(0).get("value")) : 0 ;
        Integer visualMapMax = 0 ;
        for (int i = 0; i < seriesData.size(); i++) {
            Integer value = PowerUtil.getIntValue(seriesData.get( i ).get( "value" ));
            visualMapMin = value < visualMapMin ? value : visualMapMin;
            visualMapMax = value > visualMapMax ? value : visualMapMax;
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("visualMapMin",visualMapMin.equals( visualMapMax ) ? 0 : visualMapMin);
        echartsData.put("visualMapMax",visualMapMax);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  今日服务调用次数地区分布Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result serviceOpenEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        date = "2021-01-11";
        StringBuilder sql = new StringBuilder( ServiceOpenCountEntity.countSql1);
        sql.append( " and open_date = " + JdbcTemplateUtil.getOracelToDate( date ) );
        sql.append( " group by area_name " );
        List<Map<String, Object>> seriesData = jdbcTemplate.queryForList( sql.toString() );
        Integer visualMapMin = seriesData.size() > 0 ? PowerUtil.getIntValue(seriesData.get(0).get("value")) : 0 ;
        Integer visualMapMax = 0 ;
        for (int i = 0; i < seriesData.size(); i++) {
            Integer value = PowerUtil.getIntValue(seriesData.get( i ).get( "value" ));
            visualMapMin = value < visualMapMin ? value : visualMapMin;
            visualMapMax = value > visualMapMax ? value : visualMapMax;
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("visualMapMin",visualMapMin.equals( visualMapMax ) ? 0 : visualMapMin);
        echartsData.put("visualMapMax",visualMapMax);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  首页卡片数据
     * @param  params
     * @return  返回结果
     * @date  2021-4-22 9:21
     * @author  wanghb
     * @edit
     */
    public Result countCardEcharts(Map<String, Object> params) {

        String date = PowerUtil.getString( params.get( "date" ) );
        date = "2021-01-11";
        StringBuilder sql = new StringBuilder( TaskRecordEntity.countSql2);
        sql.append( " and open_date = " + JdbcTemplateUtil.getOracelToDate( date ) );
        Integer appUserCount = jdbcTemplate.queryForObject( sql.toString(),Integer.class );
        sql = new StringBuilder( ServiceOpenCountEntity.countSql2);
        sql.append( " and open_date = " + JdbcTemplateUtil.getOracelToDate( date ) );

        Integer serverSumCount = jdbcTemplate.queryForObject( sql.toString(),Integer.class );
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("appCount",18);
        echartsData.put("appUserCount",appUserCount);
        echartsData.put("serverCount",15);
        echartsData.put("serverSumCount",serverSumCount);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  独立事务提交任务日志
     * @param  taskRecord
     * @param  params
     * @param  startDate
     * @return  返回结果
     * @date  2021-4-16 14:54
     * @author  wanghb
     * @edit
     */
    public TaskRecordEntity saveTaskRecordThread(ParamEnum.taskRecord taskRecord, Map<String, Object> params,Date startDate) {
        //开启单独事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = platformTransactionManager.getTransaction(def);
        TaskRecordEntity taskRecordEntity = new TaskRecordEntity();
        taskRecordEntity.setName( taskRecord.getName() );
        taskRecordEntity.setCode( taskRecord.getCode() );
        taskRecordEntity.setConfigJson( JSON.toJSONString( params ) );
        taskRecordEntity.setSchedule( 0 );
        taskRecordEntity.setStatus( ParamEnum.taskRecordStatus.status1.getCode() );
        taskRecordEntity.setStartDate( startDate );
        taskRecordEntity.setEndDate( null );
        String id = CodeUtils.getUUID32();
        MapUtil.setCreateBean( taskRecordEntity, id, startDate );
        this.baseDao.update( taskRecordEntity );
        this.taskRecordDao.deleteDetail( id );
        platformTransactionManager.commit(status);
        return taskRecordEntity;
    }

    /**
     * @description  独立事务更新日志
     * @param  taskRecordEntity
     * @return  返回结果
     * @date  2021-4-16 15:03
     * @author  wanghb
     * @edit
     */
    public void upDateTaskRecordThread(TaskRecordEntity taskRecordEntity) {
        DefaultTransactionDefinition def2 = new DefaultTransactionDefinition();
        def2.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status2 = platformTransactionManager.getTransaction(def2);
        saveOrUpdate( taskRecordEntity );
        platformTransactionManager.commit(status2);
    }

}
