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
@Table(name = "T_USER_NOTICE")
public class UserNotice {
    @Id
    @Column(name = "OBJID", columnDefinition = "varchar2(50) default sys_guid()") //主键
    private String objid;
    @Column(name = "GL_NOTICE_OBJ", columnDefinition = "varchar2(200)") //通知主键
    private String glNoticeObj;
    @Column(name = "IDCARD", columnDefinition = "varchar2(200)") //身份证
    private String idcard;
    @Column(name = "STATE", columnDefinition = "varchar2(2) default 0") //状态（-1：踩 0：空 1：赞）
    private String state;
}
