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
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppOpenCountService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.dao.AppOpenCountDao;
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
* 应用每日人员启动次数统计( AppOpenCountService )服务类
* @author wanghb
* @since 2021-4-16 15:58:46
*/
/**
* 应用每日人员启动次数统计( AppOpenCountService )服务实现类
* @author wanghb
* @since 2021-4-16 15:58:46
*/
@Service("appOpenCountService")
public class AppOpenCountService extends BaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    private AppOpenCountDao appOpenCountDao;
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
        sql.FROM(AppOpenCountEntity.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        /*if (PowerUtil.isNotNull( name )) {
            sql.WHERE(new StringBuilder( " l1.name like '%" ).append( name ).append( "%'" ).toString());
        }*/
        sql.ORDER_BY( "l1.create_time desc" );
        baseDao.getPaginationByNactiveSql( sql, page);
        List<AppOpenCountEntity> rows = MapUtil.toListBean( page.getRows(),AppOpenCountEntity.class );
        page.setRows( rows );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,page);
    }


    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-16 15:58:46
    * @author wanghb
    * @edit
    */
    public Result view(String id) {
        AppOpenCountEntity appOpenCountEntity = this.baseDao.getById(AppOpenCountEntity.class, id);
        if (appOpenCountEntity != null) {
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,appOpenCountEntity);
    }


    /**
    * @description 获取唯一
    * @param code
    * @return 实体对象
    * @date 2020-12-29 11:06:43
    * @author wanghb
    * @edit
    */
    public AppOpenCountEntity getOne(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull( code )) {
            queryConditions.add( new QueryCondition("code", code));
        }
        List<AppOpenCountEntity> list = baseDao.get(AppOpenCountEntity.class, queryConditions);
        return list.size() > 0 ? list.get( 0 ) : null;
    }

    /**
    * @description 保存或更新
    * @param appOpenCountEntity 实体
    * @return 无返回值
    * @date 2021-4-16 15:58:46
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result saveOrUpdate(AppOpenCountEntity appOpenCountEntity) {
        String id = appOpenCountEntity.getId();
        Date nowDate = new Date();
        if (PowerUtil.isNull( id )) {
            id = CodeUtils.getUUID32();
            MapUtil.setCreateBean( appOpenCountEntity, id, nowDate );
        } else {
            MapUtil.setUpdateBean( appOpenCountEntity, nowDate );
        }
        this.baseDao.update( appOpenCountEntity );
        this.appOpenCountDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description  去保存页面
    * @return  返回结果
    * @date  2021-4-16 15:58:46
    * @author  wanghb
    * @edit
    */
    public Result goSave() {
        AppOpenCountEntity appOpenCountEntity = new AppOpenCountEntity();
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,appOpenCountEntity);
    }


    /**
    * @description 保存
    * @param appOpenCountEntity 实体
    * @return 无返回值
    * @date 2021-4-16 15:58:46
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result save(AppOpenCountEntity appOpenCountEntity) {
        Result result = saveOrUpdate( appOpenCountEntity );
        return result;
    }


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-16 15:58:46
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result delete(String id) {
        this.baseDao.delete(AppOpenCountEntity.class, id);
        this.appOpenCountDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-4-16 15:58:46
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchDelete(List<String> ids) {
        this.baseDao.delete(AppOpenCountEntity.class, ids.toArray());
        this.appOpenCountDao.batchDeleteDetail( ids );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-16 15:58:46
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result logicDelete(String id) {
        AppOpenCountEntity appOpenCountEntity = this.baseDao.getById(AppOpenCountEntity.class, id);
        if (appOpenCountEntity != null) {
            Date nowDate = new Date();
            appOpenCountEntity.setDeleted( ParamEnum.deleted.yesDel.getCode() );
            MapUtil.setUpdateBean( appOpenCountEntity, nowDate );
            this.baseDao.update( appOpenCountEntity );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量逻辑删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-4-16 15:58:46
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchLogicDelete(List<String> ids) {
        this.appOpenCountDao.batchLogicDelete(ids);
        this.appOpenCountDao.batchLogicDeleteDetail(ids);
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
        Integer count = baseDao.getCount( AppOpenCountEntity.class, queryConditions);
        return count;
    }

}
