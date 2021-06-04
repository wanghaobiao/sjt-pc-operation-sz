package com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 角色和菜单的关联表
 */
@Getter
@Setter
@Entity
@Table(name = "ZD_SYS_ROLE_MENU")
public class RoleMenu {

    @Id
    private String id;

    private String roleId;
    private String menuId;

    @Column(columnDefinition = "date")
    private Date createTime;
    @Column(columnDefinition = "date")
    private Date updateTime;
    private String createUser;
    private String updateUser;
    private String deleted;

}
