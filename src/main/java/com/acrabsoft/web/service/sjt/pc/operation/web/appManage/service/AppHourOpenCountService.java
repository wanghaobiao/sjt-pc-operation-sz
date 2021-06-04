package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service;

import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.MapUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.acrabsoft.common.model.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppHourOpenCountService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.dao.AppHourOpenCountDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.*;
import org.springframework.data.jpa.domain.Specification;
import com.acrabsoft.web.dao.base.BaseDao;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import org.acrabsoft.common.BuildResult;
import java.util.*;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
* 应用分时人员启动次数统计( AppHourOpenCountService )服务类
* @author wanghb
* @since 2021-4-21 18:49:44
*/
/**
* 应用分时人员启动次数统计( AppHourOpenCountService )服务实现类
* @author wanghb
* @since 2021-4-21 18:49:44
*/
@Service("appHourOpenCountService")
public class AppHourOpenCountService extends BaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    private AppHourOpenCountDao appHourOpenCountDao;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private BaseDao baseDao;

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
        sql.FROM(AppHourOpenCountEntity.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        /*if (PowerUtil.isNotNull( name )) {
            sql.WHERE(new StringBuilder( " l1.name like '%" ).append( name ).append( "%'" ).toString());
        }*/
        sql.ORDER_BY( "l1.create_time desc" );
        baseDao.getPaginationByNactiveSql( sql, page);
        List<AppHourOpenCountEntity> rows = MapUtil.toListBean( page.getRows(),AppHourOpenCountEntity.class );
        page.setRows( rows );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,page);
    }


    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-21 18:49:44
    * @author wanghb
    * @edit
    */
    public Result view(String id) {
        AppHourOpenCountEntity appHourOpenCountEntity = this.baseDao.getById(AppHourOpenCountEntity.class, id);
        if (appHourOpenCountEntity != null) {
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,appHourOpenCountEntity);
    }


    /**
    * @description 获取唯一
    * @param code
    * @return 实体对象
    * @date 2020-12-29 11:06:43
    * @author wanghb
    * @edit
    */
    public AppHourOpenCountEntity getOne(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull( code )) {
            queryConditions.add( new QueryCondition("code", code));
        }
        List<AppHourOpenCountEntity> list = baseDao.get(AppHourOpenCountEntity.class, queryConditions);
        return list.size() > 0 ? list.get( 0 ) : null;
    }

    /**
    * @description 保存或更新
    * @param appHourOpenCountEntity 实体
    * @return 无返回值
    * @date 2021-4-21 18:49:44
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result saveOrUpdate(AppHourOpenCountEntity appHourOpenCountEntity) {
        String id = appHourOpenCountEntity.getId();
        Date nowDate = new Date();
        if (PowerUtil.isNull( id )) {
            id = CodeUtils.getUUID32();
            MapUtil.setCreateBean( appHourOpenCountEntity, id, nowDate );
        } else {
            MapUtil.setUpdateBean( appHourOpenCountEntity, nowDate );
        }
        this.baseDao.update( appHourOpenCountEntity );
        this.appHourOpenCountDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description  去保存页面
    * @return  返回结果
    * @date  2021-4-21 18:49:44
    * @author  wanghb
    * @edit
    */
    public Result goSave() {
        AppHourOpenCountEntity appHourOpenCountEntity = new AppHourOpenCountEntity();
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,appHourOpenCountEntity);
    }


    /**
    * @description 保存
    * @param appHourOpenCountEntity 实体
    * @return 无返回值
    * @date 2021-4-21 18:49:44
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result save(AppHourOpenCountEntity appHourOpenCountEntity) {
        Result result = saveOrUpdate( appHourOpenCountEntity );
        return result;
    }


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-21 18:49:44
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result delete(String id) {
        this.baseDao.delete(AppHourOpenCountEntity.class, id);
        this.appHourOpenCountDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-4-21 18:49:44
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchDelete(List<String> ids) {
        this.baseDao.delete(AppHourOpenCountEntity.class, ids.toArray());
        this.appHourOpenCountDao.batchDeleteDetail( ids );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-21 18:49:44
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result logicDelete(String id) {
        AppHourOpenCountEntity appHourOpenCountEntity = this.baseDao.getById(AppHourOpenCountEntity.class, id);
        if (appHourOpenCountEntity != null) {
            Date nowDate = new Date();
            appHourOpenCountEntity.setDeleted( ParamEnum.deleted.yesDel.getCode() );
            MapUtil.setUpdateBean( appHourOpenCountEntity, nowDate );
            this.baseDao.update( appHourOpenCountEntity );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量逻辑删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-4-21 18:49:44
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchLogicDelete(List<String> ids) {
        this.appHourOpenCountDao.batchLogicDelete(ids);
        this.appHourOpenCountDao.batchLogicDeleteDetail(ids);
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
        Integer count = baseDao.getCount( AppHourOpenCountEntity.class, queryConditions);
        return count;
    }

}
