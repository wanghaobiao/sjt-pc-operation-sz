package com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.DeptInfoEntity;
import org.acrabsoft.common.model.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class DeptDao {
    @Autowired
    private BaseDao basedao;

    public List<DeptInfoEntity> getDeptTree() {
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("T_SYS_DEPT_INFO_VIEW");
        List<DeptInfoEntity> list = this.basedao.getListByNactiveSql(sql);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }

    public List<DeptInfoEntity> getRoots() {
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("T_SYS_DEPT_INFO_VIEW");
        sql.WHERE("parentobj ='0' order by ordercode");
        List<DeptInfoEntity> list = this.basedao.getListByNactiveSql(sql);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }

    public List<DeptInfoEntity> getchilds(String id) {
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("T_SYS_DEPT_INFO_VIEW");
        sql.WHERE("parentobj = '" + id + "' order by ordercode");
        List<DeptInfoEntity> list = this.basedao.getListByNactiveSql(sql);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }

    public Pagination getUserByDeptid(Pagination p, String deptid, String username) {
        SQL sql = new SQL();
        sql.SELECT("u.*");
        sql.FROM("T_SYS_USER_INFO_VIEW u");
        if (!StringUtils.isEmpty(deptid)) {
            sql.WHERE("u.deptobj='" + deptid + "'");
        }
        if (!StringUtils.isEmpty(username)) {
            sql.WHERE("u.name like '%" + username + "%'");
        }
        sql.ORDER_BY("u.name");
        return this.basedao.getPaginationByNactiveSql(sql, p);
    }
}
