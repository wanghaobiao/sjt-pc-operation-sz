package com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.SQL;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserinfoDao {
    @Autowired
    private BaseDao basedao;


    public Map<String, Object> getUserinfobyuserid(String userid) {
        SQL sql = new SQL();
        sql.SELECT("u.*");//, d.value area
        sql.FROM("T_SYS_USER_INFO_VIEW u ");//left join (select * from t_dict_info where state='0') d on u.areaid=d.code
        sql.WHERE("(u.obj_id = '" + userid + "' or u.idcard='" + userid + "')");
        List<Map<String, Object>> list = basedao.getListByNactiveSql(sql);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * @description 通过警号获取人员信息
     * @param  policeNum  警号
     * @return
     * @date  2020-10-27 16:22
     * @author  wanghb
     * @edit
     */
    public Map<String, Object> getUserByPoliceNum(String policeNum) {
        SQL sql = new SQL();
        sql.SELECT("u.*");//, d.value area
        sql.FROM("T_SYS_USER_INFO_VIEW u ");//left join (select * from t_dict_info where state='0') d on u.areaid=d.code
        sql.WHERE("u.POLICENUM = '" + policeNum + "'");
        List<Map<String, Object>> list = basedao.getListByNactiveSql(sql);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * @description 通过警号获取人员信息
     * @param  idCard  身份证号
     * @return
     * @date  2020-10-27 16:22
     * @author  wanghb
     * @edit
     */
    public Map<String, Object> getUserByIdCard(String idCard) {
        SQL sql = new SQL();
        sql.SELECT("u.*");//, d.value area
        sql.FROM("T_SYS_USER_INFO_VIEW u ");//left join (select * from t_dict_info where state='0') d on u.areaid=d.code
        sql.WHERE("u.IDCARD = '" + idCard + "'");
        List<Map<String, Object>> list = basedao.getListByNactiveSql(sql);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
