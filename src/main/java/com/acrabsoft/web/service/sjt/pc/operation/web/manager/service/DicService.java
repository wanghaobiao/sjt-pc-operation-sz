package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;


import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao.DicDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.ServiceDic;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import org.acrabsoft.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class DicService {
    @Autowired
    private DicDao dao;
    @Autowired
    private BaseDao basedao;
    @Transactional
    public String insertDic(ServiceDic sd) {
        this.basedao.saveAndFlush(sd);
        return sd.getId();
    }

    @Transactional
    public String updateDic(ServiceDic sd) {
        this.basedao.update(sd);
        return sd.getId();
    }

    public List<ServiceDic> getDicByType(String type) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        if(PowerUtil.isNotNull( type )){
            queryConditions.add(new QueryCondition("type", type));
        }

        List<ServiceDic> list = this.basedao.get(ServiceDic.class,queryConditions,"create_time desc");
        return list;
    }


}
