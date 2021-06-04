package com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "T_SYS_DEPT_INFO_VIEW")
public class DeptInfoEntity {

    @Id
    private String obj_id;

    @Column
    private String parentobj;

    @Column
    private String deptid;

    @Column
    private String parentid;

    @Column
    private String deptname;
    @Column
    private String full_name;

    /*
    @Column
    private String alias;
    @Column
    private String updatetime;

    @Column
    private String isparent; // 0 表示无下级  1表示有下级

    @Column
    private String ordercode;

    @Column
    private String areaid;

    @Column
    private String scope_id;

    @Column
    private Integer org_status;*/

    @Transient
    List<DeptInfoEntity> child;
}
