package com.acrabsoft.web.service.sjt.pc.operation.web.system.service;

import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.MapUtil;
import org.acrabsoft.common.model.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.dao.SequenceDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.entity.*;
import org.springframework.data.jpa.domain.Specification;
import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
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
* 序列表( SequenceService )服务类
* @author wanghb
* @since 2020-12-30 18:25:32
*/
/**
* 序列表( SequenceService )服务实现类
* @author wanghb
* @since 2020-12-30 18:25:32
*/
@Service("sequenceService")
public class SequenceService extends BaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    private SequenceDao sequenceDao;
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
        sql.FROM(SequenceInfo.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        /*if (PowerUtil.isNotNull( name )) {
            sql.WHERE(new StringBuilder( " l1.name like '%" ).append( name ).append( "%'" ).toString());
        }*/
        sql.ORDER_BY( "l1.create_time desc" );
        baseDao.getPaginationByNactiveSql( sql, page);
        List<SequenceInfo> rows = MapUtil.toListBean( page.getRows(),SequenceInfo.class );
        page.setRows( rows );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,page);
    }


    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    public Result view(String id) {
        SequenceInfo sequenceInfo = this.baseDao.getById(SequenceInfo.class, id);
        if (sequenceInfo != null) {
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,sequenceInfo);
    }


    /**
    * @description 获取唯一
    * @param code
    * @return 实体对象
    * @date 2020-12-29 11:06:43
    * @author wanghb
    * @edit
    */
    public SequenceInfo getOne(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull( code )) {
            queryConditions.add( new QueryCondition("code", code));
        }
        List<SequenceInfo> list = baseDao.get(SequenceInfo.class, queryConditions);
        return list.size() > 0 ? list.get( 0 ) : null;
    }

    /**
    * @description 保存或更新
    * @param sequenceInfo 实体
    * @return 无返回值
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result saveOrUpdate(SequenceInfo sequenceInfo) {
        String id = sequenceInfo.getId();
        Date nowDate = new Date();
        if (PowerUtil.isNull( id )) {
            id = CodeUtils.getUUID32();
            MapUtil.setCreateBean( sequenceInfo, id, nowDate );
        } else {
            MapUtil.setUpdateBean( sequenceInfo, nowDate );
        }
        this.baseDao.update( sequenceInfo );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description  去保存页面
    * @return  返回结果
    * @date  2020-12-30 18:25:32
    * @author  wanghb
    * @edit
    */
    public Result goSave() {
        SequenceInfo sequenceInfo = new SequenceInfo();
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,sequenceInfo);
    }


    /**
    * @description 保存
    * @param sequenceInfo 实体
    * @return 无返回值
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result save(SequenceInfo sequenceInfo) {
        Result result = saveOrUpdate( sequenceInfo );
        return result;
    }

    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result delete(String id) {
        this.baseDao.delete(SequenceInfo.class, id);
        this.sequenceDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchDelete(List<String> ids) {
        this.baseDao.delete(SequenceInfo.class, ids.toArray());
        this.sequenceDao.batchDeleteDetail( ids );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result logicDelete(String id) {
        SequenceInfo sequenceInfo = this.baseDao.getById(SequenceInfo.class, id);
        if (sequenceInfo != null) {
            Date nowDate = new Date();
            sequenceInfo.setDeleted( ParamEnum.deleted.yesDel.getCode() );
            MapUtil.setUpdateBean( sequenceInfo, nowDate );
            this.baseDao.update( sequenceInfo );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量逻辑删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2020-12-30 18:25:32
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchLogicDelete(List<String> ids) {
        this.sequenceDao.batchLogicDelete(ids);
        this.sequenceDao.batchLogicDeleteDetail(ids);
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
        Integer count = baseDao.getCount( SequenceInfo.class, queryConditions);
        return count;
    }


    static Queue<String> queueApp = new LinkedList<>();

    /**
     * @description  获取app序列
     * @param  sequenceName
     * @return  返回结果
     * @date  2021-3-29 18:25
     * @author  wanghb
     * @edit
     */
    public synchronized String getNum(String sequenceName){
        Integer queueCount = 10;
        Integer minDigits = 8;
        if( queueApp.isEmpty()){
            crarteQueue(sequenceName,queueCount,minDigits,queueApp);
        }
        return queueApp.poll();
    }


    /**
     * @description  生成队列
     * @param  sequenceName
     * @param  queueCount
     * @param  minDigits
     * @param  queueApp
     * @return  返回结果
     * @date  2021-3-29 18:30
     * @author  wanghb
     * @edit
     */
    private void crarteQueue(String sequenceName, Integer queueCount, Integer minDigits, Queue<String> queueApp) {
        List<Map<String, Object>>  list = jdbcTemplate.queryForList( "select SEQUENCE_INDEX from YY_SEQUENCE where SEQUENCE_NAME = '" + sequenceName + "'" );
        String nextval = "1";
        if(list.size() == 0){
            jdbcTemplate.update( "INSERT INTO YY_SEQUENCE(ID, SEQUENCE_NAME, SEQUENCE_INDEX, DELETED) VALUES ('"+ CodeUtils.getUUID32() +"', '"+sequenceName+"', '1', '0')" );
        }else {
            nextval = PowerUtil.getString( list.get( 0 ).get( "SEQUENCE_INDEX" ) );
        }
        for (int i = 0; i < queueCount; i++) {
            //System.out.println("---------"+nextval);
            queueApp.add( sequenceFmt(nextval ,minDigits) );
            nextval = (PowerUtil.getInt( nextval ) + 1) + "";
        }
        jdbcTemplate.update( "UPDATE YY_SEQUENCE SET SEQUENCE_INDEX = "+ (PowerUtil.getInt( nextval ) ) +" WHERE SEQUENCE_NAME = '" +sequenceName+ "'");
    }


    /**
     * @description  栈格式化  例如 :  1 格式化为   00001
     * @param  nextval
     * @param  minDigits
     * @return  返回结果
     * @date  2021-3-29 18:19
     * @author  wanghb
     * @edit
     */
    private String sequenceFmt(String nextval, Integer minDigits) {
        if (nextval.length() < minDigits) {
            if (nextval.length() == 1) {
                nextval = new StringBuilder( "0000000" ).append( nextval ).toString();
            }else if (nextval.length() == 2) {
                nextval = new StringBuilder( "000000" ).append( nextval ).toString();
            }else if (nextval.length() == 3) {
                nextval = new StringBuilder( "00000" ).append( nextval ).toString();
            }else if (nextval.length() == 4) {
                nextval = new StringBuilder( "0000" ).append( nextval ).toString();
            }else if (nextval.length() == 5) {
                nextval = new StringBuilder( "000" ).append( nextval ).toString();
            }else if (nextval.length() == 6) {
                nextval = new StringBuilder( "00" ).append( nextval ).toString();
            }else if (nextval.length() == 7) {
                nextval = new StringBuilder( "0" ).append( nextval ).toString();
            }
        }
        return nextval;
    }


}
