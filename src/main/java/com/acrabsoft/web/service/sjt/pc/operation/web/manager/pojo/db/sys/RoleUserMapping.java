package com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;
@Entity
@Getter
@Setter
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name = "t_sys_role_user")//, indexes = {@Index(columnList = "roleId"), @Index(columnList = "userId"), @Index(columnList = "deleted")}
public class RoleUserMapping {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public final static String insertSql = "INSERT INTO t_sys_role_user(ID, CREATE_DEPT, CREATE_TIME, CREATE_USER, DELETED, ROLE_ID, UPDATE_DEPT, UPDATE_TIME, UPDATE_USER, USER_ID, IDCARD, ROLE_END_DATE, ROLE_START_DATE, COST_JF, BRANCH_DEPT_ID, BRANCH_DEPT_NAME) VALUES (:id,:createDept,:createTime,:createUser,:deleted,:roleId,:updateDept,:updateTime,:updateUser,:userId,:idcard,:roleEndDate,:roleStartDate,:costJf,:branchDeptId,:branchDeptName)";
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public final static String delSql = "DELETE FROM t_sys_role_user where 1 = 1 ";
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public final static String selectSql = "SELECT * FROM t_sys_role_user where 1 = 1 ";
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public final static String roleSql = "SELECT l4.dwmc as \"deptName\",l3.dwid as \"deptId\",l3.xm as \"name\",l3.sfzh as \"idCard\",l3.yhid as \"yhId\" " +
                                    " FROM role_info_sm@TYMH l1 " +
                                    " LEFT JOIN role_user_info_sm@TYMH l2 ON l1.jsid = l2.jsid " +
                                    " LEFT JOIN user_info_sm@TYMH l3 ON l3.yhid = l2.yhid " +
                                    " LEFT JOIN DEPT_INFO_SM@TYMH l4 ON l4.dwid = l3.dwid " +
                                    " WHERE 1 = 1 ";
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "idcard")
    private String idcard;

    @Column(name = "role_id")
    private String roleId;

    @Column(name = "create_time",columnDefinition = "date default sysdate")
    @JSONField(format = "%Y-%m-%d HH:mm")
    private Date createTime;

    @Column(name = "update_time",columnDefinition = "date default sysdate")
    @JSONField(format = "%Y-%m-%d HH:mm")
    private Date updateTime;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_dept")
    private String createDept;

    @Column(name = "update_user")
    private String updateUser;

    @Column(name = "update_dept")
    private String updateDept;

    @Column(name = "deleted")
    private String deleted;

    @Column(name = "role_start_date")
    private String roleStartDate;

    @Column(name = "role_end_date")
    private String roleEndDate;

    @Column(name = "cost_jf")
    private Integer costJf;

    @Basic
    @Column(name = "branch_dept_id")
    @ApiModelProperty(value="管辖的分局部门id",name="branchDeptId",required=false)
    private String branchDeptId;

    @Basic
    @Column(name = "branch_dept_name")
    @ApiModelProperty(value="管辖的分局部门名称",name="branchDeptName",required=false)
    private String branchDeptName;

}
