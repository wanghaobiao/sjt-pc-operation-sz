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
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.ServiceHourOpenCountService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.dao.ServiceHourOpenCountDao;
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
* 服务分时应用调用次数统计( ServiceHourOpenCountService )服务类
* @author wanghb
* @since 2021-4-21 14:46:58
*/
/**
* 服务分时应用调用次数统计( ServiceHourOpenCountService )服务实现类
* @author wanghb
* @since 2021-4-21 14:46:58
*/
@Service("serviceHourOpenCountService")
public class ServiceHourOpenCountService extends BaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    private ServiceHourOpenCountDao serviceHourOpenCountDao;
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
        sql.FROM(ServiceHourOpenCountEntity.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        /*if (PowerUtil.isNotNull( name )) {
            sql.WHERE(new StringBuilder( " l1.name like '%" ).append( name ).append( "%'" ).toString());
        }*/
        sql.ORDER_BY( "l1.create_time desc" );
        baseDao.getPaginationByNactiveSql( sql, page);
        List<ServiceHourOpenCountEntity> rows = MapUtil.toListBean( page.getRows(),ServiceHourOpenCountEntity.class );
        page.setRows( rows );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,page);
    }


    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-21 14:46:58
    * @author wanghb
    * @edit
    */
    public Result view(String id) {
        ServiceHourOpenCountEntity serviceHourOpenCountEntity = this.baseDao.getById(ServiceHourOpenCountEntity.class, id);
        if (serviceHourOpenCountEntity != null) {
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,serviceHourOpenCountEntity);
    }


    /**
    * @description 获取唯一
    * @param code
    * @return 实体对象
    * @date 2020-12-29 11:06:43
    * @author wanghb
    * @edit
    */
    public ServiceHourOpenCountEntity getOne(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull( code )) {
            queryConditions.add( new QueryCondition("code", code));
        }
        List<ServiceHourOpenCountEntity> list = baseDao.get(ServiceHourOpenCountEntity.class, queryConditions);
        return list.size() > 0 ? list.get( 0 ) : null;
    }

    /**
    * @description 保存或更新
    * @param serviceHourOpenCountEntity 实体
    * @return 无返回值
    * @date 2021-4-21 14:46:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result saveOrUpdate(ServiceHourOpenCountEntity serviceHourOpenCountEntity) {
        String id = serviceHourOpenCountEntity.getId();
        Date nowDate = new Date();
        if (PowerUtil.isNull( id )) {
            id = CodeUtils.getUUID32();
            MapUtil.setCreateBean( serviceHourOpenCountEntity, id, nowDate );
        } else {
            MapUtil.setUpdateBean( serviceHourOpenCountEntity, nowDate );
        }
        this.baseDao.update( serviceHourOpenCountEntity );
        this.serviceHourOpenCountDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description  去保存页面
    * @return  返回结果
    * @date  2021-4-21 14:46:58
    * @author  wanghb
    * @edit
    */
    public Result goSave() {
        ServiceHourOpenCountEntity serviceHourOpenCountEntity = new ServiceHourOpenCountEntity();
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,serviceHourOpenCountEntity);
    }


    /**
    * @description 保存
    * @param serviceHourOpenCountEntity 实体
    * @return 无返回值
    * @date 2021-4-21 14:46:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result save(ServiceHourOpenCountEntity serviceHourOpenCountEntity) {
        Result result = saveOrUpdate( serviceHourOpenCountEntity );
        return result;
    }


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-21 14:46:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result delete(String id) {
        this.baseDao.delete(ServiceHourOpenCountEntity.class, id);
        this.serviceHourOpenCountDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-4-21 14:46:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchDelete(List<String> ids) {
        this.baseDao.delete(ServiceHourOpenCountEntity.class, ids.toArray());
        this.serviceHourOpenCountDao.batchDeleteDetail( ids );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-21 14:46:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result logicDelete(String id) {
        ServiceHourOpenCountEntity serviceHourOpenCountEntity = this.baseDao.getById(ServiceHourOpenCountEntity.class, id);
        if (serviceHourOpenCountEntity != null) {
            Date nowDate = new Date();
            serviceHourOpenCountEntity.setDeleted( ParamEnum.deleted.yesDel.getCode() );
            MapUtil.setUpdateBean( serviceHourOpenCountEntity, nowDate );
            this.baseDao.update( serviceHourOpenCountEntity );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量逻辑删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-4-21 14:46:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchLogicDelete(List<String> ids) {
        this.serviceHourOpenCountDao.batchLogicDelete(ids);
        this.serviceHourOpenCountDao.batchLogicDeleteDetail(ids);
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
        Integer count = baseDao.getCount( ServiceHourOpenCountEntity.class, queryConditions);
        return count;
    }

}
