package com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.ServiceDic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DicDao {
    @Autowired
    private BaseDao basedao;

    public List<ServiceDic> getDicList(String type) {
        SQL sql = new SQL();
        sql.SELECT("t.*");
        sql.FROM("t_service_dic_info t");
        sql.WHERE("t.type = '" + type + "'");
        sql.ORDER_BY("t.create_time desc");
        return this.basedao.getListByNactiveSql(sql);
    }
}
