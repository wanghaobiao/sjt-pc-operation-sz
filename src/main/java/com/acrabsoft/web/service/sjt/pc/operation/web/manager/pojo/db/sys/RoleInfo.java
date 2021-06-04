package com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "ZD_SYS_ROLE")
public class RoleInfo {

    @Id
    private String roleId;

    private String roleName;

    private String roleDescription;

    @Column(columnDefinition = "date")
    private Date createTime;

    private String deleted;

    private String ordernum;

}
