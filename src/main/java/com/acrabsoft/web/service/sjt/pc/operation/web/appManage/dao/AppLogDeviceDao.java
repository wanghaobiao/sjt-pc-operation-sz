
package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.dao;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
* 应用日志信息( AppLogDeviceDao )Dao类
* @author wanghb
* @since 2020-11-23 14:34:51
*/
@Repository
public class AppLogDeviceDao extends BaseController {
    @Autowired
    private BaseDao baseDao;

    /**
    * @description  删除明细
    * @param  id  父id
    * @return  返回结果
    * @date  2020-11-23 14:34:51
    * @author  wanghb
    * @edit
    */
    public void deleteDetail(String id) {

    }


    /**
    * @description  批量逻辑删除
    * @param  ids  id集合
    * @return  返回结果
    * @date  2020-11-23 14:34:51
    * @author  wanghb
    * @edit
    */
    public void batchLogicDelete(List<String> ids) {

    }


    /**
    * @description  批量删除明细
    * @param  ids  父id
    * @return  返回结果
    * @date  2020-11-23 14:34:51
    * @author  wanghb
    * @edit
    */
    public void batchDeleteDetail(List<String> ids) {
    }


    /**
    * @description  批量逻辑删除明细
    * @param  ids  id集合
    * @return  返回结果
    * @date  2020-11-23 14:34:51
    * @author  wanghb
    * @edit
    */
    public void batchLogicDeleteDetail(List<String> ids) {

    }
}
