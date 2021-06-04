package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao.UserinfoDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.UserInfoEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import io.swagger.annotations.ApiOperation;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserinfoService extends BaseController {
    private Logger logger = LoggerFactory.getLogger( this.getClass() );
    @Autowired
    private UserinfoDao dao;

    @Autowired
    private BaseDao baseDao;
    @Autowired
    private RoleService roleService;

    public Map<String, Object> getUserinfobyuserid(String userid) {
        return this.dao.getUserinfobyuserid(userid);
    }

    /**
     * @description  通过身份证号获取人员信息
     * @param  idCard
     * @return  返回结果
     * @date  20/09/07 12:08
     * @author  wanghb
     * @edit
     */
    public List<UserInfoEntity> getInfoByIdcard(String idCard) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("idcard", idCard));
        List<UserInfoEntity> userInfoEntities = baseDao.get( UserInfoEntity.class, queryConditions );
        return userInfoEntities;
    }

    /**
     * @description  通过id获取人员信息
     * @param  userId
     * @return  返回结果
     * @date  20/09/07 12:08
     * @author  wanghb
     * @edit
     */
    public UserInfoEntity getInfoById(String userId) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("obj_id", userId));
        List<UserInfoEntity> userInfoEntities = baseDao.get( UserInfoEntity.class, queryConditions );
        return userInfoEntities.size() > 0 ? userInfoEntities.get(  0) : new UserInfoEntity() ;
    }

    /**
     * @description  获取所有的人员数据
     * @return
     * @date  2020-10-28 11:15
     * @author  wanghb
     * @edit
     */
    public List<UserInfoEntity> getAll(){
        List<QueryCondition> queryConditions = new ArrayList<>();
        List<UserInfoEntity> userInfoEntities = baseDao.get( UserInfoEntity.class, queryConditions );
        return userInfoEntities;
    }

    /**
     * @description  根据类型和证件号获取人员信息
     * @param  type  证件号类型  0 身份证号  1 警号
     * @param  number  证件号
     * @return
     * @date  2020-10-29 9:48
     * @author  wanghb
     * @edit
     */
    public Result getByUserByType(String type, String number) {
        String idCardType = "0";
        String policeNumType = "1";
        if (PowerUtil.isNull( type )) {
            return BuildResult.buildOutResult( ResultEnum.ERROR,"类型字段不能为空");
        }else {
            if (!idCardType.equals( type ) && !policeNumType.equals( type )) {
                return BuildResult.buildOutResult( ResultEnum.ERROR, new StringBuilder( "无法识别类型字段 : " ).append( type ).append( ",类型字段只能是 0 身份证号 或 1 警号" ).toString() );
            }
        }

        List<QueryCondition> queryConditions = new ArrayList<>();
        if (idCardType.equals( type )) {
            queryConditions.add(new QueryCondition("idcard", number));
        }
        if (policeNumType.equals( type )) {
            queryConditions.add(new QueryCondition("policenum", number));
        }
        List<UserInfoEntity> userInfoEntities = baseDao.get( UserInfoEntity.class, queryConditions );
        if (userInfoEntities.size() == 0) {
            return BuildResult.buildOutResult( ResultEnum.ERROR, new StringBuilder("没有查到这个" ).append( number ).append( "证件编号的人员信息" ).toString() );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,userInfoEntities.get( 0 ));
    }

    /**
     * @description
     * @param  selectStr 证件号,身份证号,姓名模
     * @return
     * @date  2020-11-4 10:31
     * @author  wanghb
     * @edit
     */
    public Result userSelect(String selectStr) {
        SQL sql = new SQL();
        sql.SELECT("obj_id as value, name,idCard id_Card,deptid as dept_Id ,deptname as dept_name ");
        sql.FROM("T_SYS_USER_INFO_VIEW");
        sql.WHERE( new StringBuilder( "idcard like '%" ).append( selectStr ).append( "%'  or policenum like '%").append( selectStr ).append("%' or name like '%").append( selectStr ).append( "%'" ).toString() );
        List<Map<String, Object>> list = baseDao.getListByNactiveSql(sql);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list);
    }

    /**
     * @description  获取所有人员名称和警号
     * @param
     * @return
     * @date  2020-11-19 18:44
     * @author  wanghb
     * @edit
     */
    public Result getNameAndPoliceNum() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<UserInfoEntity> all = getAll();
        for (UserInfoEntity userInfoEntity : all) {
            String name = userInfoEntity.getName();
            String policeNum = userInfoEntity.getPolicenum();
            Map<String, Object> temp = new HashMap<>();
            temp.put( "value", policeNum);
            temp.put( "name", name + " " + policeNum);
            list.add(temp);
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list);
    }

    /**
     * @description  获取人员信息
     * @param  pageNum
     * @param  pageSize
     * @param  policeNum
     * @param  userName
     * @return  返回结果
     * @date  2021-1-11 10:58
     * @author  wanghb
     * @edit
     */
    public Result getUserList(int pageNum, int pageSize, String policeNum, String userName, String idCard) {
        String userId = getBaseUser().getUserid();
        Map<String, Object> role = roleService.getRoleByUserId(userId);
        logger.info( new StringBuilder( "查询者权限========>" ).append( role ).toString() );
        String orderNum = role == null ? "" : PowerUtil.getString( role.get( "orderNum" ) );
        if(role == null ){
            return BuildResult.buildOutResult( ResultEnum.ERROR,"无此权限。");
        }
        Pagination pagination = new Pagination(pageNum, pageSize);
        SQL sql = new SQL();
        sql.SELECT("NAME,USER_ID ID_CARD,POLICE_NUM,DEPT_NAME,BGSDH ");
        sql.FROM("ZD_SYS_USER_SYNC");
        if (PowerUtil.isNotNull( policeNum )) {
            sql.WHERE( "POLICE_NUM like '%" + policeNum + "%'" );
        }
        if (PowerUtil.isNotNull( idCard )) {
            sql.WHERE( "USER_ID like '%" + idCard + "%'" );
        }
        if (PowerUtil.isNotNull( userName )) {
            sql.WHERE( "name like '%" + userName + "%'" );
        }
        sql.ORDER_BY("POLICE_NUM");
        baseDao.getPaginationByNactiveSql(sql, pagination);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,pagination);
    }
}
