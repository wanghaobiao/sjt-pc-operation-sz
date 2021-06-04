package com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 部门与数据接口权限关联表
 */
@Getter
@Setter
@Entity
@Table(name = "t_sys_dept_interface", indexes = {@Index(columnList = "deptId"), @Index(columnList = "datainterfaceId"), @Index(columnList = "deleted")})
public class DeptDatainterfaceMapping {

    @Id
    private String id;

    private String deptId;

    private String datainterfaceId;

    @Column(columnDefinition = "date")
    private Date createTime;

    @Column(columnDefinition = "date")
    private Date updateTime;

    private String createUser;

    private String createDept;

    private String updateUser;

    private String updateDept;

    private String deleted;

}
