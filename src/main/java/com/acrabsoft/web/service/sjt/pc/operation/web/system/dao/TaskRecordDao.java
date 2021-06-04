
package com.acrabsoft.web.service.sjt.pc.operation.web.system.dao;

import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.JdbcTemplateUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import org.springframework.stereotype.Repository;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.entity.TaskRecordEntity;
import com.acrabsoft.web.dao.base.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.entity.*;
import java.util.Date;
import java.util.List;


/**
* 任务执行记录( TaskRecordDao )Dao类
* @author wanghb
* @since 2021-4-15 17:23:27
*/
@Repository
public class TaskRecordDao extends BaseController {
    @Autowired
    private BaseDao baseDao;

    /**
    * @description  删除明细
    * @param  id  父id
    * @return  返回结果
    * @date  2021-4-15 17:23:27
    * @author  wanghb
    * @edit
    */
    public void deleteDetail(String id) {
    }

    /**
    * @description  批量逻辑删除
    * @param  ids  id集合
    * @return  返回结果
    * @date  2021-4-15 17:23:27
    * @author  wanghb
    * @edit
    */
    public void batchLogicDelete(List<String> ids) {
        String userId = getBaseUser().getUserid();
        SQL sql = new SQL();
        sql.UPDATE( TaskRecordEntity.tableName );
        sql.SET( new StringBuilder( " deleted = '" ).append( ParamEnum.deleted.yesDel.getCode() ).append( "'" ).toString() );
        sql.SET( JdbcTemplateUtil.getOracleUpate( new Date(), userId ) );
        sql.WHERE( new StringBuilder( "id in (" ).append( JdbcTemplateUtil.toInParams( ids ) ).append( ")" ).toString() );
        baseDao.executeUpdateByNactiveSql( sql );
    }


    /**
    * @description  批量删除明细
    * @param  ids  父id
    * @return  返回结果
    * @date  2021-4-15 17:23:27
    * @author  wanghb
    * @edit
    */
    public void batchDeleteDetail(List<String> ids) {
    }


    /**
    * @description  批量逻辑删除明细
    * @param  ids  id集合
    * @return  返回结果
    * @date  2021-4-15 17:23:27
    * @author  wanghb
    * @edit
    */
    public void batchLogicDeleteDetail(List<String> ids) {
        String userId = getBaseUser().getUserid();
    }
}
