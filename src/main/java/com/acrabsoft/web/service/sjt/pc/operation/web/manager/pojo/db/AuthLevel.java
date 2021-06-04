package com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name = "T_AUTH_LEVEL")
public class AuthLevel {
    @Id
    @Column(name = "OBJID", columnDefinition = "varchar2(50)")
    private String objid;
    @Column(name = "NAME", columnDefinition = "varchar2(2000)")
    private String name;
    @Column(name = "AUTHDEPTNAME", columnDefinition = "varchar2(2000)")
    private String authdeptname;
    @Column(name = "DEPTCODE", columnDefinition = "varchar2(100)")
    private String deptcode;
    @Column(name = "DEPTOBJ", columnDefinition = "varchar2(100)")
    private String deptobj;
    @Column(name = "CAN_AUTH_SON", columnDefinition = "varchar2(2) default 0")
    private String canAuthSon; //0:不可以 1：可以
    @Column(name = "JF", columnDefinition = "number default 0")
    private String jf;
    @Column(name = "MENU_ID", columnDefinition = "varchar2(2000)")
    private String menuId;
}
