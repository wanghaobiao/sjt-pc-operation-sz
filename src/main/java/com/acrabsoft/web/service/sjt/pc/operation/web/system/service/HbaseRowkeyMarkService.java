package com.acrabsoft.web.service.sjt.pc.operation.web.system.service;

import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.acrabsoft.common.model.ResultEnum;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.dao.HbaseRowkeyMarkDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.entity.*;
import com.acrabsoft.web.dao.base.BaseDao;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import org.acrabsoft.common.BuildResult;
import java.util.*;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
* hbaseRowkey标识对照表( HbaseRowkeyMarkService )服务类
* @author wanghb
* @since 2021-4-9 11:57:53
*/
/**
* hbaseRowkey标识对照表( HbaseRowkeyMarkService )服务实现类
* @author wanghb
* @since 2021-4-9 11:57:53
*/
@Service("hbaseRowkeyMarkService")
public class HbaseRowkeyMarkService extends BaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    private HbaseRowkeyMarkDao hbaseRowkeyMarkDao;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Resource
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
        sql.FROM(HbaseRowkeyMarkEntity.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        /*if (PowerUtil.isNotNull( name )) {
            sql.WHERE(new StringBuilder( " l1.name like '%" ).append( name ).append( "%'" ).toString());
        }*/
        sql.ORDER_BY( "l1.create_time desc" );
        baseDao.getPaginationByNactiveSql( sql, page);
        List<HbaseRowkeyMarkEntity> rows = MapUtil.toListBean( page.getRows(),HbaseRowkeyMarkEntity.class );
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
    public void listPageStatus(List<HbaseRowkeyMarkEntity> rows) {

    }

    /**
     * @description  获取所有的数据
     * @param
     * @return  返回结果
     * @date  2021-4-12 11:25
     * @author  wanghb
     * @edit
     */
    public List<HbaseRowkeyMarkEntity> getAllList() {
        SQL sql = new SQL();
        sql.SELECT("l1.type,l1.rowkey_code,l1.rowkey_value");
        sql.FROM(HbaseRowkeyMarkEntity.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        sql.ORDER_BY( "l1.id" );
        List<HbaseRowkeyMarkEntity> rows =  MapUtil.toListBean( baseDao.getListByNactiveSql( sql),HbaseRowkeyMarkEntity.class );
        return rows;
    }

    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    public Result view(String id) {
        HbaseRowkeyMarkEntity hbaseRowkeyMarkentity = this.baseDao.getById(HbaseRowkeyMarkEntity.class, id);
        if (hbaseRowkeyMarkentity != null) {
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,hbaseRowkeyMarkentity);
    }


    /**
    * @description 获取唯一
    * @param code
    * @return 实体对象
    * @date 2020-12-29 11:06:43
    * @author wanghb
    * @edit
    */
    public HbaseRowkeyMarkEntity getOne(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull( code )) {
            queryConditions.add( new QueryCondition("code", code));
        }
        List<HbaseRowkeyMarkEntity> list = baseDao.get(HbaseRowkeyMarkEntity.class, queryConditions);
        return list.size() > 0 ? list.get( 0 ) : null;
    }

    /**
    * @description 保存或更新
    * @param hbaseRowkeyMarkentity 实体
    * @return 无返回值
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result saveOrUpdate(HbaseRowkeyMarkEntity hbaseRowkeyMarkentity) {
        String id = hbaseRowkeyMarkentity.getId();
        Date nowDate = new Date();
        if (PowerUtil.isNull( id )) {
            id = CodeUtils.getUUID32();
            MapUtil.setCreateBean( hbaseRowkeyMarkentity, id, nowDate );
        } else {
            MapUtil.setUpdateBean( hbaseRowkeyMarkentity, nowDate );
        }
        this.baseDao.update( hbaseRowkeyMarkentity );
        this.hbaseRowkeyMarkDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
     * @description  更新appCode
     * @param  appCode
     * @param  appValue
     * @return  返回结果
     * @date  2021-4-12 16:52
     * @author  wanghb
     * @edit
     */
    @Transactional(rollbackOn = Exception.class)
    public void updateAppCode(String appCode, String appValue,String type) {
        HbaseRowkeyMarkEntity hbaseRowkeyMarkEntity = new HbaseRowkeyMarkEntity();
        hbaseRowkeyMarkEntity.setRowkeyCode( appCode );
        hbaseRowkeyMarkEntity.setRowkeyValue( appValue );
        hbaseRowkeyMarkEntity.setUpdateTime( new Date() );
        hbaseRowkeyMarkEntity.setType( type );
        namedParameterJdbcTemplate.update( HbaseRowkeyMarkEntity.updateSql01, JdbcTemplateUtil.beanPropSource( hbaseRowkeyMarkEntity ));
    }

    /**
    * @description  去保存页面
    * @return  返回结果
    * @date  2021-4-9 11:57:53
    * @author  wanghb
    * @edit
    */
    public Result goSave() {
        HbaseRowkeyMarkEntity hbaseRowkeyMarkentity = new HbaseRowkeyMarkEntity();
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,hbaseRowkeyMarkentity);
    }


    /**
    * @description 保存
    * @param hbaseRowkeyMarkentity 实体
    * @return 无返回值
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result save(HbaseRowkeyMarkEntity hbaseRowkeyMarkentity) {
        Result result = saveOrUpdate( hbaseRowkeyMarkentity );

        Set<String> set = new HashSet<>();
        for(int i = 0 ; i < 1000000 ; i++){
            set.add( RandomUtil.getRandomString(RandomUtil.range1,2));
        }
        Date date = new Date();
        List<String> list = new ArrayList<>(set);
        List<HbaseRowkeyMarkEntity> markEntities = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String rowkeyCode = list.get(i);
            HbaseRowkeyMarkEntity hbaseRowkeyMarkentityTemp = new HbaseRowkeyMarkEntity();
            hbaseRowkeyMarkentityTemp.setType(ParamEnum.rowkeyType.type5.getCode());
            hbaseRowkeyMarkentityTemp.setRowkeyCode(rowkeyCode);
            //hbaseRowkeyMarkentityTemp.setRowkeyValue();
            MapUtil.setCreateBean( hbaseRowkeyMarkentityTemp,CodeUtils.getUUID32(),date);
            markEntities.add( hbaseRowkeyMarkentityTemp );
        }
        System.out.println(markEntities.size());
        namedParameterJdbcTemplate.batchUpdate( HbaseRowkeyMarkEntity.insertSql, JdbcTemplateUtil.ListBeanPropSource( markEntities ) );
        return result;
    }


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result delete(String id) {
        this.baseDao.delete(HbaseRowkeyMarkEntity.class, id);
        this.hbaseRowkeyMarkDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchDelete(List<String> ids) {
        this.baseDao.delete(HbaseRowkeyMarkEntity.class, ids.toArray());
        this.hbaseRowkeyMarkDao.batchDeleteDetail( ids );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result logicDelete(String id) {
        HbaseRowkeyMarkEntity hbaseRowkeyMarkentity = this.baseDao.getById(HbaseRowkeyMarkEntity.class, id);
        if (hbaseRowkeyMarkentity != null) {
            Date nowDate = new Date();
            hbaseRowkeyMarkentity.setDeleted( ParamEnum.deleted.yesDel.getCode() );
            MapUtil.setUpdateBean( hbaseRowkeyMarkentity, nowDate );
            this.baseDao.update( hbaseRowkeyMarkentity );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量逻辑删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-4-9 11:57:53
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchLogicDelete(List<String> ids) {
        this.hbaseRowkeyMarkDao.batchLogicDelete(ids);
        this.hbaseRowkeyMarkDao.batchLogicDeleteDetail(ids);
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
        Integer count = baseDao.getCount( HbaseRowkeyMarkEntity.class, queryConditions);
        return count;
    }

}
