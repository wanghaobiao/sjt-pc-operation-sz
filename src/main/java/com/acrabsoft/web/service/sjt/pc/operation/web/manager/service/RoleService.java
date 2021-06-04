package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;

import com.acrabsoft.web.Configuration.SpringBeanUtil;
import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.constant.Constants;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao.LoginDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao.RoleDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.AuthLevel;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.RoleInfo;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.RoleUserMapping;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import io.swagger.annotations.ApiOperation;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.acrabsoft.utils.StringUtil;
import org.acrabsoft.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class RoleService {
    @Autowired
    private RefreshService refreshService;

    @Autowired
    private RoleDao dao;


    @Value("${njDeptId}")
    public String njDeptId;

    @Autowired
    private LoginDao loginDao;

    @Autowired
    private BaseDao basedao;

    @Transactional
    public String updateRole(String roleId, String roleName, String roleDescription) {
        RoleInfo role = this.basedao.getById(RoleInfo.class, roleId);
        if (role != null) {
            if (!StringUtil.isNullBlank(roleName)) {
                role.setRoleName(roleName);
            }
            if (!StringUtil.isNullBlank(roleDescription)) {
                role.setRoleDescription(roleDescription);
            }
            this.basedao.update(role);
            return role.getRoleId();
        } else {
            return null;
        }
    }

    @Transactional
    public String saveRole(RoleInfo role) {
        this.basedao.saveAndFlush(role);
        return role.getRoleId();
    }

    @Transactional
    public void adduser(String roleId, String[] users, Map<String, Object> userinfo, int days, Map<String, Object> loginUserMap, String branchDeptId, String branchDeptName) {
        String convertStartTime = "";
        String convertEndTime = "";
        if (days > 0) {
            Map<String, Object> map = this.dao.getDatabaseTime(days);
            convertStartTime = map.get("startime").toString();
            convertEndTime = map.get("endtime").toString();
        }



        RoleUserMapping roleUserMapping;
        List<RoleUserMapping> list = new ArrayList<>();
        for (int i = 0; i < users.length; i++) {
            roleUserMapping = new RoleUserMapping();
            roleUserMapping.setId(UUIDUtil.getUUID32());
            roleUserMapping.setRoleId(roleId);
            roleUserMapping.setUserId(users[i]);
            Map<String, Object> map = this.loginDao.getUserInfoByobj(users[i]);
            if (map != null) {
                roleUserMapping.setIdcard(map.get("idcard") == null ? "" : map.get("idcard").toString().trim());
            }
            roleUserMapping.setDeleted(Constants.DELETED_STATE.NOT_DELETED);
            roleUserMapping.setCreateDept((String) userinfo.get("deptname"));
            roleUserMapping.setCreateUser((String) userinfo.get("objId"));
            roleUserMapping.setCreateTime(new Date());
            roleUserMapping.setRoleStartDate(convertStartTime);
            roleUserMapping.setRoleEndDate(convertEndTime);
            roleUserMapping.setBranchDeptId( branchDeptId );
            roleUserMapping.setBranchDeptName( branchDeptName );
            list.add(roleUserMapping);
        }
        this.basedao.batchInsert(list);
    }

    public void adduserReFreshAuth(String[] users) {
        for (String userId : users) {
            if (!StringUtil.isNullBlank(userId)) {
                this.refreshService.refreshUser(userId);
            }
        }
    }

    public List<RoleInfo> getRoleList(String roleName) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull(roleName)) {
            queryConditions.add(new QueryCondition("role_name", roleName));
        }
        List<RoleInfo> list = this.basedao.get(RoleInfo.class,queryConditions,"create_time");
        return list;
    }

    public List<RoleUserMapping> getRoleUserList(String roleId) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (!StringUtils.isEmpty(roleId)) {
            queryConditions.add(new QueryCondition("roleId", roleId));
        }
        List<RoleUserMapping> list = this.basedao.get(RoleUserMapping.class,queryConditions);
        return list;
    }

    /**
     * @description  获取角色数量
     * @param  roleId  角色id
     * @param  userId  人员id
     * @return  返回结果
     * @date  2020-12-18 10:30
     * @author  wanghb
     * @edit
     */
    public Integer getRoleUserCount(String roleId,String userId) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if(PowerUtil.isNotNull( roleId )){
            queryConditions.add(new QueryCondition("roleId", roleId));
        }
        if(PowerUtil.isNotNull( userId )){
            queryConditions.add(new QueryCondition("userId", userId));
        }
        Integer count = basedao.getCount(RoleUserMapping.class, queryConditions);
        return count;
    }

    @Transactional
    public Result deleteuser(String roleId, String userid, Map<String, Object> userinfo) {
        List<RoleUserMapping> list = new ArrayList<RoleUserMapping>();
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("roleId", roleId));
        queryConditions.add(new QueryCondition("userId", userid));
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        List<RoleUserMapping> rumlist = this.basedao.get(RoleUserMapping.class, queryConditions);
        if (rumlist.size() > 0) {
            for (RoleUserMapping rolemap : rumlist) {
                rolemap.setDeleted(Constants.DELETED_STATE.IS_DELETED);
                rolemap.setUpdateTime(new Date());
                rolemap.setUpdateUser((String) userinfo.get("objId"));
                rolemap.setUpdateDept((String) userinfo.get("deptname"));
                this.basedao.update(rolemap);
            }
            return BuildResult.buildOutResult(ResultEnum.SUCCESS, "ok");
        }
        return BuildResult.buildOutResult(ResultEnum.ERROR, "删除失败，未获取到此条记录");
    }

    public void deleteuserRefreshAuth(String userid) {
        if (!StringUtil.isNullBlank(userid)) {
            this.refreshService.refreshUser(userid);
        }
    }

    public Pagination getroleusers(String roleId, String username, int pageNum, int pageSize, String policenum, String branchDeptName) {
        Pagination p = new Pagination(pageNum, pageSize);
        return this.dao.getroleusers(roleId, username, p,branchDeptName);
    }

    /*@Transactional
    public List<Map<String,String>> addDept(String roleId, String deptid, Map<String, Object> userinfo) {
        RoleDeptMapping rdm;
        // 1.根据部门id获取所有子部门,同时将关联表中已存在上述的子部门关联记录全部逻辑删除
        List<Map<String,String>> list = this.dao.getchildById(roleId,deptid);
        // 2.将所有子部门状态关联信息插入关联表
        List<RoleDeptMapping> newlist  = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            rdm = new RoleDeptMapping();
            rdm.setId(UUIDUtil.getUUID32());
            rdm.setRoleId(roleId);
            rdm.setDeptId(list.get(i).get("objId"));
            rdm.setDeleted(Constants.DELETED_STATE.NOT_DELETED);
            rdm.setCreateDept((String)userinfo.get("deptname"));
            rdm.setCreateUser((String)userinfo.get("objId"));
            rdm.setCreateTime(new Date());
            newlist.add(rdm);
        }
        this.basedao.batchInsert(newlist);
        return list;
    }*/



    public void deletedeptRefreshAuth(String deptid) {
        if (!StringUtil.isNullBlank(deptid)) {
            this.refreshService.refreshDept(deptid);
        }
    }


    public Pagination getUserInfoByRoleAndDept(String roleId, String deptId, String xm, String idcard, int pageNum, int pageSize,String policeNum) {
        String superRoleId = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("superRoleId");
        String highRoleId = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("highRoleId");
        String middleRoleId = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("middleRoleId");
        Pagination p = new Pagination(pageNum, pageSize);
        if (!StringUtil.isNullBlank(roleId)) {
            //int count = this.dao.getcount(roleId, deptId);
            //老逻辑暂时不用
            if (false) { // count > 0说明当前部门或当前部门的上级部门已经和此角色关联
                Pagination pagination = this.dao.getUserDeptReleve(deptId, xm, idcard,policeNum, p);
                List<Map<String, Object>> list = pagination.getRows();
                for (Map<String, Object> map : list) {
                    map.put("roleEndDate", "");
                }
                return pagination;
            } else {
                Pagination pagination = this.dao.getUserDeptNoReleve(roleId, deptId, xm, idcard,policeNum, p);
                /*List<Map<String, Object>> list = pagination.getRows();
                for (Map<String, Object> map : list) {
                    String canreleve = map.get("canreleve").toString();
                    Object o = map.get("roleEndDate");
                    if ("false".equals(canreleve)) {
                        if (o == null) {
                            map.put("roleEndDate", "");
                        }
                    } else {
                        map.put("roleEndDate", null);
                    }
                }*/
                return pagination;
            }
        } else {
            Pagination pagination = this.dao.getDeptUsers(deptId, xm, idcard, p);
            List<Map<String, Object>> list = pagination.getRows();
            for (Map<String, Object> map : list) {
                String userid = map.get("objId").toString();
                String superId = map.get("super").toString();
                String highId = map.get("high").toString();
                String middleId = map.get("middle").toString();
                if ("1".equals(superId)) {
                    String roleEndDate = this.dao.getUserRoleEndDate(superRoleId, userid);
                    map.put("roleEndDate", roleEndDate);
                } else if ("1".equals(highId)) {
                    String roleEndDate = this.dao.getUserRoleEndDate(highRoleId, userid);
                    map.put("roleEndDate", roleEndDate);
                } else if ("1".equals(middleId)) {
                    String roleEndDate = this.dao.getUserRoleEndDate(middleRoleId, userid);
                    map.put("roleEndDate", roleEndDate);
                } else {
                    map.put("roleEndDate", "");
                }
            }
            return pagination;
        }
    }



    public List<Map<String, Object>> getDeptTreeById(String id, Map<String, Object> loginUserMap) {
        if ("0".equals(njDeptId)) {
            String authdeptobj = loginUserMap.get("authdeptobj").toString();
            return this.dao.getDeptByObj(authdeptobj);
        } else {
            List<Map<String, Object>> list = this.dao.getchilds(id);
            return list;
        }

    }


//    @Transactional
//    public void removeBlackList(String idcard) {
//        BlackList b = this.basedao.getById(BlackList.class, idcard);
//        if (b != null) {
//            this.basedao.delete(BlackList.class, idcard);
//        }
//    }


    /**
     * @description  通过userId获取权限及部门数据
     * @param  userId  人员id
     * @return  返回结果
     * @date  20/09/09 13:58
     * @author  wanghb
     * @edit
     */
    public Map<String, Object> getRoleByUserId(String userId) {
        SQL sql = new SQL();
        sql.SELECT("l3.ORDERNUM ORDER_NUM,L2.DEPTID DEPT_ID,L2.DEPTNAME DEPT_NAME,L2.NAME user_Name,l3.role_name ,l1.role_id");
        sql.FROM("t_sys_role_user l1 left join T_SYS_USER_INFO_VIEW l2 on l1.USER_ID = l2.Obj_ID LEFT JOIN ZD_SYS_ROLE l3 on l1.role_id = l3.role_id\n");
        sql.WHERE( new StringBuilder( "l1.USER_ID = '" ).append( userId ).append( "' AND L1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "' AND L3.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString() );
        sql.ORDER_BY( " l3.ordernum desc " );
        List<Map<String, Object>> list = basedao.getListByNactiveSql(sql);
        if (list.size() > 0){
            Map<String, Map<String, Object>> orderNumCache = new HashMap<>();
            for (Map<String, Object> temp : list) {
                String orderNum = PowerUtil.getString( temp.get( "orderNum" ) );
                orderNumCache.put(orderNum,temp);
            }
            /*for (int i = 0; i < ParamEnum.roleOrderNum.getRoleOrder().size(); i++) {
                String orderNum = ParamEnum.roleOrderNum.getRoleOrder().get( i );
                if(orderNumCache.containsKey( orderNum )){
                    return orderNumCache.get( orderNum );
                }
            }*/
            return list.get( 0 );
        }else{
            return null;
        }

    }

    /**
     * @description  通过userId获取权限及部门数据
     * 该方法只有二类区(APK)使用内网逻辑变更  (非APK逻辑勿动)
     * @param  userId  人员id
     * @return  返回结果
     * @date  20/09/09 13:58
     * @author  wanghb
     * @edit
     */
    public Map<String, Object> getRoleByUserIdForApk(String userId) {
        SQL sql = new SQL();
        sql.SELECT("l3.ORDERNUM ORDER_NUM,l3.role_name ,l1.role_id");
        sql.FROM("t_sys_role_user l1  LEFT JOIN ZD_SYS_ROLE l3 on l1.role_id = l3.role_id\n");
        sql.WHERE( new StringBuilder( "l1.USER_ID = '" ).append( userId ).append( "' AND L1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "' AND L3.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString() );
        List<Map<String, Object>> list = basedao.getListByNactiveSql(sql);
        return list.size() > 0 ? list.get( 0 ) : null;
    }

}
