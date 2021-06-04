package com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.constant.Constants;
import org.acrabsoft.common.model.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Repository
public class InterfaceDao {

    @Autowired
    private BaseDao basedao;


    @Transactional
    public void deleteDeptMapping(String id, String deptid) {
        SQL sql = new SQL();
        sql.UPDATE("t_sys_dept_interface");
        sql.SET("deleted = '" + Constants.DELETED_STATE.IS_DELETED + "'");
        sql.WHERE("datainterface_id = '" + id + "' and dept_id = '" + deptid + "' and deleted='" + Constants.DELETED_STATE.NOT_DELETED + "'");
        this.basedao.executeUpdateByNactiveSql(sql);
    }



    public Pagination getdeptList(String objId, Pagination p) {
        SQL sql = new SQL();
        sql.SELECT("d.*,t.create_time authtime");
        sql.FROM("t_sys_dept_interface t,T_SYS_DEPT_INFO_VIEW d");
        sql.WHERE("t.deleted='0' and t.dept_id = d.obj_id");
        sql.WHERE("t.datainterface_id = '" + objId + "'");
        sql.ORDER_BY("t.create_time desc");
        return this.basedao.getPaginationByNactiveSql(sql, p);
    }


    public Pagination getNoAuthdeptList(Pagination p) {
        SQL sql = new SQL();
        sql.SELECT("d.*");
        sql.FROM("T_SYS_DEPT_INFO_VIEW d");
        sql.WHERE("d.obj_id not in (select dept_id from t_sys_dept_interface t where t.deleted='0')");
        return this.basedao.getPaginationByNactiveSql(sql, p);
    }

    public List<Map<String, Object>> getInterfaceInfo(String objId) {
        SQL sql = new SQL();
        sql.SELECT("t.*");
        sql.FROM("t_zhcx_data_interface t");
        sql.WHERE("t.id = '" + objId + "' and t.deleted='0'");
        return this.basedao.getListByNactiveSql(sql);
    }
}
