package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao.DeptDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.DeptInfoEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.RoleUserMapping;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import javafx.util.Pair;
import org.acrabsoft.common.model.Pagination;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeptService {
    @Autowired
    private DeptDao dao;

    @Autowired
    private BaseDao baseDao;

    private List<DeptInfoEntity> nodes;

    public List<DeptInfoEntity> getDeptTree() {
        List<DeptInfoEntity> list = this.dao.getDeptTree();
        if (list.size() > 0) {
            nodes = new ArrayList<DeptInfoEntity>();
            JSONArray jsonarray = JSONArray.parseArray(JSON.toJSONString(list));
            List<DeptInfoEntity> listnew = new ArrayList<>();
            for (int i = 0; i < jsonarray.size(); i++) {
                JSONObject jsonObject = jsonarray.getJSONObject(i);
                DeptInfoEntity dif = new DeptInfoEntity();
                dif.setObj_id(jsonObject.getString("objId"));
                dif.setDeptid(jsonObject.getString("deptid"));
                dif.setDeptname(jsonObject.getString("deptname"));
                dif.setParentid(jsonObject.getString("parentid"));
                dif.setParentobj(jsonObject.getString("parentobj"));
                /*dif.setAlias(jsonObject.getString("alias"));
                dif.setAreaid(jsonObject.getString("areaid"));
                dif.setFull_name(jsonObject.getString("fullName"));
                dif.setIsparent(jsonObject.getString("isparent"));
                dif.setUpdatetime(jsonObject.getString("updatetime"));
                dif.setOrdercode(jsonObject.getString("ordercode"));
                dif.setScope_id(jsonObject.getString("scopeId"));
                dif.setOrg_status(jsonObject.getInteger("orgStatus"));*/
                listnew.add(dif);
            }
//            List<DeptInfoEntity> listnew = JSONArray.parseArray(jsonarray.toString(),DeptInfoEntity.class);
            Iterator<DeptInfoEntity> it = listnew.iterator();
            while (it.hasNext()) {
                DeptInfoEntity ob = (DeptInfoEntity) it.next();
                nodes.add(ob);
            }
            return this.buildTree();
        }
        return null;
    }


    /**
     * 构建树形结构
     *
     * @return
     */
    public List<DeptInfoEntity> buildTree() {
        List<DeptInfoEntity> treeNodes = new ArrayList<DeptInfoEntity>();
        List<DeptInfoEntity> rootNodes = getRootNodes();
        for (DeptInfoEntity rootNode : rootNodes) {
            buildChildNodes(rootNode);
            treeNodes.add(rootNode);
        }
        return treeNodes;
    }


    /**
     * 获取集合中所有的根节点
     *
     * @return
     */
    public List<DeptInfoEntity> getRootNodes() {
        List<DeptInfoEntity> rootNodes = new ArrayList<DeptInfoEntity>();
        for (DeptInfoEntity n : nodes) {
            if (rootNode(n)) {
                rootNodes.add(n);//把所有的根节点放入rootNodes集合中
            }
        }
        return rootNodes;
    }

    /**
     * 判断是否为根节点
     *
     * @param node
     * @return
     */
    public boolean rootNode(DeptInfoEntity node) {
        boolean isRootNode = true;
        if ("0" != node.getParentobj()) {//判断传入的node对象中，他的上级成员编号还有没有node中的成员编号与之对应，如果没有，则为根节点
            isRootNode = false;
        }
        return isRootNode;
    }


    /**
     * 递归子节点
     *
     * @param node
     */
    public void buildChildNodes(DeptInfoEntity node) {
        List<DeptInfoEntity> children = getChildNodes(node);
        if (!children.isEmpty()) {
            for (DeptInfoEntity child : children) {
                buildChildNodes(child);
            }
            node.setChild(children);
        }
    }

    /**
     * 获取父节点下所有的子节点
     *
     * @param pnode
     * @return
     */
    public List<DeptInfoEntity> getChildNodes(DeptInfoEntity pnode) {//传入父节点对象，如果为该父节点的子节点，则放入子节点集合中
        List<DeptInfoEntity> childNodes = new ArrayList<DeptInfoEntity>();
        for (DeptInfoEntity n : nodes) {//从nodes中筛选所以为pnode的子节点
            if (pnode.getObj_id().equals(n.getParentobj())) {
                childNodes.add(n);
            }
        }
        return childNodes;
    }

    public List<DeptInfoEntity> getDeptTreeById(String id) {
        List<DeptInfoEntity> list = null;
        if (StringUtils.isNotEmpty(id)) {
            list = this.dao.getchilds(id);
        } else {
            list = this.dao.getRoots();
        }
        return list;
    }

    public Pagination getUserByDeptid(int pageNum, int pageSize, String deptid, String username) {
        Pagination p = new Pagination(pageNum, pageSize);
        return this.dao.getUserByDeptid(p, deptid, username);
    }


    /**
     * @description  通过部门id获取分局部门id
     * @param  deptId  部门id
     * @param  njDeptId  南京部门id
     * @return  返回结果
     * @date  20/09/09 17:38
     * @author  wanghb
     * @edit
     */
    public Pair<String,String> getBranchDeptIdByDeptId(String deptId, String njDeptId) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deptid", deptId));
        List<DeptInfoEntity> list = baseDao.get( DeptInfoEntity.class,queryConditions );
        String objId = null;
        if (list.size() == 0) {
            return null;
        }else{
            objId = list.get( 0 ).getObj_id();
        }

        while (true){
            if (objId == null) {
                return null;
            }
            DeptInfoEntity deptInfoEntity = baseDao.getById( DeptInfoEntity.class, objId );
            if(deptInfoEntity == null){
                return null;
            }
            String parentId = deptInfoEntity.getParentid();
            if (njDeptId.equals( parentId )) {
                return new Pair<>( deptInfoEntity.getDeptid(),deptInfoEntity.getDeptname() );
            }else{
                objId = parentId;
            }
        }
    }

    /**
     * @description  通过人员id获取分局部门id
     * @param  userId  人员id
     * @return  返回结果
     * @date  20/09/09 17:38
     * @author  wanghb
     * @edit
     */
    public Pair<String,String> getBranchDeptIdByUserId(String userId) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("userId", userId));
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        List<RoleUserMapping> list = baseDao.get( RoleUserMapping.class, queryConditions);
        return list.size() == 0 ? null : new Pair<>( list.get( 0 ).getBranchDeptId(),list.get( 0 ).getBranchDeptName() );
    }

    /**
     * @description  通过部门id获取分局部门id
     * @param  deptId  部门id
     * @param  deptname  部门名称
     * @param  njDeptId  南京部门id
     * @return  返回结果
     * @date  20/09/09 17:38
     * @author  wanghb
     * @edit
     */
    public String getFullName(String deptId, String deptname, String njDeptId) {
        String fullName = deptname;
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deptid", deptId));
        List<DeptInfoEntity> list = baseDao.get( DeptInfoEntity.class,queryConditions );
        String objId = null;
        if (list.size() == 0) {
            return null;
        }else{
            objId = list.get( 0 ).getObj_id();
        }

        while (true){
            if (objId == null) {
                return null;
            }
            DeptInfoEntity deptInfoEntity = baseDao.getById( DeptInfoEntity.class, objId );
            if(deptInfoEntity == null){
                return null;
            }
            String parentId = deptInfoEntity.getParentid();
            if (njDeptId.equals( parentId )) {
                return "";
            }else{
                objId = parentId;
            }
        }
    }

    /**
     * @description  获取数据
     * @param  deptId  部门id
     * @return  返回结果
     * @date  20/09/14 20:43
     * @author  wanghb
     * @edit
     */
    public DeptInfoEntity getByDeptId(String deptId) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deptid", deptId));
        List<DeptInfoEntity> userInfoEntities = baseDao.get( DeptInfoEntity.class, queryConditions );
        return userInfoEntities.size() > 0 ? userInfoEntities.get(  0) : new DeptInfoEntity() ;
    }

    /**
     * @description  通过部门id  获取下属部门id
     * @param
     * @return  返回结果
     * @date  20/09/23 11:26
     * @author  wanghb
     * @edit
     */
    public List<Map<String, Object>> getDeptsByParentid(String deptId) {
        SQL sql = new SQL();
        sql.SELECT("deptid as id ,deptName as name");
        sql.FROM("T_SYS_DEPT_INFO_VIEW");
        sql.WHERE( new StringBuilder(" PARENTID = '" ).append( deptId ).append( "'" ).toString() );
        sql.ORDER_BY( "pxh" );
        List<Map<String, Object>> list = baseDao.getListByNactiveSql(sql);
        return list;
    }

    /**
     * @description  分局部门map  键:id 值:name
     * @param  njDeptId  南京id
     * @return  返回结果
     * @date  2020-12-1 14:52
     * @author  wanghb
     * @edit
     */
    public Map<String, String> getBranchDeptMap(String njDeptId) {
        List<Map<String, Object>> list = getDeptsByParentid(njDeptId);
        Map<String, String> temp = new HashMap<>();

        for (Map<String, Object> map : list) {
            String id = PowerUtil.getString( map.get( "id" ) );
            String name = PowerUtil.getString(map.get( "name" ) );
            temp.put(id,name);
        }
        return temp;
    }
}
