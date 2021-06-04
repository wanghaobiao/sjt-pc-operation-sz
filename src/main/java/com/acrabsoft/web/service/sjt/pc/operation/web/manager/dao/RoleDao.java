package com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao;

import com.acrabsoft.web.Configuration.SpringBeanUtil;
import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.RoleInfo;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.RoleUserMapping;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.UserInfoEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ScheduledTasks;
import io.swagger.annotations.ApiOperation;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class RoleDao {
    @Autowired
    private BaseDao basedao;

    public Pagination getRoleList(Pagination p, String roleName) {
        SQL sql = new SQL();
        sql.SELECT("t.*");
        sql.FROM("ZD_SYS_ROLE t");
        if (StringUtils.isNotEmpty(roleName)) {
            sql.WHERE("t.role_name like '%" + roleName + "%'");
        }
        sql.WHERE("t.deleted = '0' ORDER BY t.create_time desc");
        return this.basedao.getPaginationByNactiveSql(sql, p);
    }

    /**
     * @description  通过id获取数据
     * @param  roleId
     * @return  返回结果
     * @date  2020-12-22 14:30
     * @author  wanghb
     * @edit
     */
    public RoleInfo getRoleById(String roleId,String ordernum) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        if (PowerUtil.isNotNull( roleId )) {
            queryConditions.add(new QueryCondition("roleId", roleId));
        }
        if (PowerUtil.isNotNull( ordernum )) {
            queryConditions.add( new QueryCondition( "ordernum", ordernum ) );
        }
        List<RoleInfo> list = basedao.get(RoleInfo.class, queryConditions);
        return list.size() > 0 ? list.get( 0 ) : null;
    }


    public Pagination getroleusers(String roleId, String username, Pagination p, String branchDeptName) {
        SQL sql = new SQL();
        //u.*,
        sql.SELECT("t.user_id ,t.branch_dept_name,t.branch_dept_id,t.create_time gltime,t.role_start_date,t.role_end_date ");//,(case when b.sfzhm is null then '0' else '1' end) isblacklist
        sql.FROM("t_sys_role_user t " +
               " LEFT JOIN T_SYS_USER_SIMPLE_VIEW u on  t.user_id = u.user_id "+
               " LEFT JOIN T_SYS_DEPT_INFO_VIEW d on  d.DEPTID = t.branch_dept_id ");
        sql.ORDER_BY( "to_number(d.pxh) asc,t.user_id" );
        //sql.WHERE("u.idcard = b.sfzhm(+)");
        if (StringUtils.isNotEmpty(roleId)) {
            sql.WHERE("t.role_id='" + roleId + "'");
        }
        if (StringUtils.isNotEmpty(username)) {
            sql.WHERE("u.name like '%" + username + "%'");
        }
        if (StringUtils.isNotEmpty(branchDeptName)) {
            sql.WHERE("t.branch_dept_name like '%" + branchDeptName + "%'");
        }
        sql.WHERE("t.deleted = '0' and (sysdate < DATE_FORMAT(t.role_end_date,'yyyymmdd') or t.role_end_date is null) ");//ORDER BY u.ordercode,u.ordernum
        Pagination paginationByNactiveSql = this.basedao.getPaginationByNactiveSql( sql, p );
        List<Map<String, Object>> rows = paginationByNactiveSql.getRows();
        for (Map<String, Object> row : rows) {
            String userId = PowerUtil.getString( row.get( "userId" ) );
            UserInfoEntity userInfoEntity = null;
            String name = userInfoEntity.getName();
            String idcard = userInfoEntity.getIdcard();
            String policenum = userInfoEntity.getPolicenum();
            row.put( "name",name );
            row.put( "objId",idcard );
            row.put( "idcard",idcard );
            row.put( "policenum",policenum );
        }
        return paginationByNactiveSql;
    }



    public Pagination getUserDeptReleve(String deptId, String xm, String idcard,String policeNum, Pagination p) {
        String endSql = "";
        if (!StringUtil.isNullBlank(xm)) {
            endSql += " and t.name like '%" + xm + "%'";
        }
        if (!StringUtil.isNullBlank(idcard)) {
            endSql += " and t.idcard like '%" + idcard + "%'";
        }
        if (!StringUtil.isNullBlank(policeNum)) {
            endSql += " and t.policeNum like '%" + policeNum + "%'";
        }
        SQL sql = new SQL();
        sql.SELECT("distinct t.*,'' role_end_date,'false' as canreleve,'false' as enabled,(case when b.sfzhm is null then '0' else '1' end) isblacklist");
        sql.FROM("T_SYS_USER_INFO_VIEW t,t_sys_black_list b");
        sql.WHERE("t.idcard = b.sfzhm(+)");
        sql.WHERE("exists(select dg.* from(SELECT * FROM T_SYS_DEPT_INFO_VIEW D CONNECT BY D.parentobj = PRIOR D.obj_id START WITH D.obj_id = '" + deptId + "') dg where dg.obj_id=t.deptobj)" + endSql);
        sql.ORDER_BY("t.ordercode,t.ordernum");
        return this.basedao.getPaginationByNactiveSql(sql, p);
    }


    public Pagination getUserDeptNoReleve(String roleId, String deptId, String xm, String idcard,String policeNum, Pagination p) {
        SQL sql = new SQL();
        sql.SELECT(" t.*");
        sql.FROM("T_SYS_USER_INFO_VIEW t");
        sql.WHERE("t.idcard in ( select distinct s.sfzh from user_info_sm s WHERE exists (select dg.* from (SELECT *  FROM T_SYS_DEPT_INFO_VIEW D CONNECT BY D.parentobj = PRIOR D.obj_id START WITH D.obj_id = "+deptId+") dg where dg.obj_id = s.dwid) )");
        if (!StringUtil.isNullBlank(xm)) {
            sql.WHERE("t.name like '%" + xm + "%'");
        }
        if (!StringUtil.isNullBlank(idcard)) {
            sql.WHERE("t.idcard like '%" + idcard + "%'");
        }
        if (!StringUtil.isNullBlank(policeNum)) {
            sql.WHERE("t.policeNum like '%" + policeNum + "%'");
        }
        return this.basedao.getPaginationByNactiveSql(sql, p);
    }

    public Pagination getDeptUsers(String deptId, String xm, String idcard, Pagination p) {
        String superRoleId = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("superRoleId");
        String highRoleId = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("highRoleId");
        String middleRoleId = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("middleRoleId");
        String lowRoleId = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("lowRoleId");
        String endSql = "";
        if (!StringUtil.isNullBlank(xm)) {
            endSql += " and t.name like '%" + xm + "%'";
        }
        if (!StringUtil.isNullBlank(idcard)) {
            endSql += " and t.idcard like '%" + idcard + "%'";
        }
        SQL sql = new SQL();
        sql.SELECT("distinct t.*,'" + superRoleId + "' superRoleObj,'" + highRoleId + "' highRoleObj,'" + middleRoleId + "' middleRoleObj,'" + lowRoleId + "' lowRoleObj,(case\n" +
                "                  when instr(aa.roleids, '" + superRoleId + "') > 0 then\n" +
                "                   '1'\n" +
                "                  else\n" +
                "                   '0'\n" +
                "                end) super,\n" +
                "                (case\n" +
                "                  when instr(aa.roleids, '" + highRoleId + "') > 0 then\n" +
                "                   '1'\n" +
                "                  else\n" +
                "                   '0'\n" +
                "                end) high,\n" +
                "                (case\n" +
                "                  when instr(aa.roleids, '" + middleRoleId + "') > 0 then\n" +
                "                   '1'\n" +
                "                  else\n" +
                "                   '0'\n" +
                "                end) middle,\n" +
                "                (case\n" +
                "                  when instr(aa.roleids, '" + lowRoleId + "') > 0 then\n" +
                "                   '1'\n" +
                "                  else\n" +
                "                   '1'\n" +  //低级权限是用部门进行全省授权，每个人都有的，这里统一简单处理
                "                end) low,(case when b.sfzhm is null then '0' else '1' end) isblacklist");
        sql.FROM("T_SYS_USER_INFO_VIEW t left join (select ww.user_id, wm_concat(distinct ww.role_id) roleids\n" +
                "  from (select ru.*\n" +
                "          from ZD_SYS_ROLE r, T_SYS_ROLE_USER ru\n" +
                "         where r.role_id = ru.role_id\n" +
                "           and r.deleted = '0'\n" +
                "           and ru.deleted = '0' and (sysdate < DATE_FORMAT(ru.role_end_date,'yyyymmdd') or ru.role_end_date is null)) ww\n" +
                " group by ww.user_id) aa on aa.user_id=t.obj_id  left join t_sys_black_list b on t.idcard = b.sfzhm");
        sql.WHERE("exists(select dg.* from(SELECT * FROM T_SYS_DEPT_INFO_VIEW D CONNECT BY D.parentobj = PRIOR D.obj_id START WITH D.obj_id = '" + deptId + "') dg where dg.obj_id=t.deptobj)" + endSql);
        sql.ORDER_BY("t.ordercode,t.ordernum");
        return this.basedao.getPaginationByNactiveSql(sql, p);
    }
    public String getUserRoleEndDate(String roleObj, String userId) {
        String sql0 = "select t.*\n" +
                "  from T_SYS_ROLE_USER t\n" +
                " where t.user_id = '" + userId + "'\n" +
                "   and t.role_id = '" + roleObj + "'\n" +
                "   and t.deleted = '0'\n" +
                " order by t.role_end_date desc";
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("(" + sql0 + ")");
        List<Map<String, Object>> list = this.basedao.getListByNactiveSql(sql);
        if (list.size() > 0) {
            Object o = list.get(0).get("roleEndDate");
            return o == null ? "" : o.toString();
        } else {
            return "-";
        }
    }


    //老代码  不知道干嘛用的  所以注释
    /*public int getcount(String roleId, String deptId) {
        String roleIdSql = "";
        if (!StringUtil.isNullBlank(roleId)) {
            roleIdSql += " and t.role_id='" + roleId + "'";
        }
        SQL sql = new SQL();
        sql.SELECT("distinct t.id");
        sql.FROM("t_sys_role_dept t");
        sql.WHERE("1=1" + roleIdSql + " and t.deleted = '0' and t.dept_id in (select obj_id from T_SYS_DEPT_INFO_VIEW d start with d.obj_id = '" + deptId + "' " +
                "connect by d.obj_id = prior d.parentobj)");
        logger.info(sql.toString());
        return this.basedao.getCountByNactiveSql(sql);
    }*/

    public List<Map<String, Object>> getchilds(String id) {
        SQL sql = new SQL();
        sql.SELECT("distinct t.*");
        sql.FROM("T_SYS_DEPT_INFO_VIEW t");
        sql.WHERE("t.parentobj = '" + id + "'");
        sql.ORDER_BY("to_number(t.pxh) asc");
        List<Map<String, Object>> list = this.basedao.getListByNactiveSql(sql);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }

    public List<Map<String, Object>> getDeptByObj(String obj) {
        SQL sql = new SQL();
        sql.SELECT("distinct t.*");
        sql.FROM("T_SYS_DEPT_INFO_VIEW t");
        sql.WHERE("t.obj_id = '" + obj + "'");
        List<Map<String, Object>> list = this.basedao.getListByNactiveSql(sql);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }



    public Map<String, Object> getDatabaseTime(int days) {
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("(select DATE_FORMAT(sysdate,'yyyymmdd') startime,DATE_FORMAT(sysdate+" + days + ",'yyyymmdd') endtime from dual)");
        List<Map<String, Object>> list = this.basedao.getListByNactiveSql(sql);
        return list.get(0);
    }

}
