package com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.SQL;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import springfox.documentation.spring.web.json.Json;

import java.util.List;
import java.util.Map;

@Repository
public class LoginDao {
    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private BaseDao basedao;

    public Map<String, Object> getUserInfoByobj(String objid) {
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("T_SYS_USER_INFO_VIEW");
        sql.WHERE("(obj_id = '" + objid + "' or idcard = '" + objid + "')");
        List<Map<String, Object>> list = basedao.getListByNactiveSql(sql);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public Map<String, Object> checkuser(String account) {
        SQL sql = new SQL();
        sql.SELECT("u.obj_id, " +
                " u.policenum, " +
                " u.idcard, " +
                " u.name, " +
                " u.full_name     deptname, " +
                " lg.password, " +
                " lg.account ");
        sql.FROM("T_LOGIN_USER lg " +
                " LEFT JOIN T_SYS_USER_INFO_VIEW u ON lg.idcard = u.idcard");
        sql.WHERE("(lg.account = '" + account + "' or u.policenum='" + account + "' or u.mobilenumber='" + account + "' or u.idcard='" + account + "')  and lg.deleted='0'");
        logger.info("账号(数字证书)登陆查询===>"+sql.toString());
        List<Map<String, Object>> list = basedao.getListByNactiveSql(sql);

        if (list.size() > 0) {
            logger.info("登陆成功===>"+ JSON.toJSONString( list.get( 0 ) ) );
            return list.get(0);
        }else {
            logger.info("登陆失败===>未查询到结果");
        }
        return null;
    }

    public List<Object> getUserRoleAndMenu(String userObjId) {
        SQL sql = new SQL();
        sql.SELECT("m.* ").FROM("T_SYS_MENU m ").WHERE("exists ( " +
                "    select * from ZD_SYS_ROLE_MENU rm , T_SYS_ROLE_USER ru " +
                "    where rm.role_Id = ru.ROLE_ID and rm.DELETED = '0' and ru.DELETED = '0' and rm.menu_Id = m.OBJ_ID and ru.USER_ID = '" + userObjId + "' " +
                "    )");
        List<Object> objectList = this.basedao.getListByNactiveSql(sql);
        return objectList;
    }

    public Map<String, Object> checkuserZslogin(String account) {
        SQL sql = new SQL();
        sql.SELECT("u.obj_id, " +
                " u.policenum, " +
                " u.idcard, " +
                " u.name, " +
                " u.deptId    authdeptobj," +
                " lg.account authobj,"+
               /* "        lg.account, " +
                " le.menu_id, " +
                " le.objid authobj, " +
                " le.authdeptname     authdeptname, " +
                " le.deptobj     authdeptobj, " +
                " le.deptcode     authdept, " +
                " le.can_auth_son, " +
                " le.jf, " +
                " le.name         authname,"+*/
        "       u.full_name     deptname  " );
        sql.FROM("T_LOGIN_USER lg " +
                "  join T_SYS_USER_INFO_VIEW u " +
                "    on lg.idcard = u.idcard " /*+
                "  left join T_AUTH_LEVEL le " +
                "    on lg.authlevelobj = le.objid"*/);
        sql.WHERE("(lg.account = '" + account + "' or u.policenum='" + account + "' or u.mobilenumber='" + account + "' or u.idcard='" + account + "') and lg.deleted='0'");
        List<Map<String, Object>> list = basedao.getListByNactiveSql(sql);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}

