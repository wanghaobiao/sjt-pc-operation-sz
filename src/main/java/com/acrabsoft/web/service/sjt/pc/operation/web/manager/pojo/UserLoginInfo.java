package com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_login_user")
@Getter
@Setter
public class UserLoginInfo {

    /**
     * 主键
     */
    @Id
   // @GenericGenerator(name = "PKUUID", strategy = "uuid2")
   // @GeneratedValue(generator = "PKUUID")
    @Column(name = "OBJ_ID", length = 32)
    private String objId;

    /**
     * 登录账号
     */
    @Column(name = "ACCOUNT", length = 200, nullable = false)
    private String account;

    /**
     * 登录密码，MD5
     */
    @Column(name = "PASSWORD", length = 32, nullable = false)
    private String password;

    /**
     * 最近一次登录时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_LOGIN_TIME")
    private Date lastLoginTime;

    /**
     * 用户id
     */
    @Column(name = "user_id", length = 40, nullable = false)
    private String userId;

    /**
     * 用户idcard
     */
    @Column(name = "idcard", length = 40)
    private String idcard;

    /**
     * 关联管理员主键
     */
    @Column(name = "AUTHLEVELOBJ", length = 50)
    private String authlevelobj;

    /**
     * 是否删除
     */
    @Column(name = "DELETED", length = 2, nullable = false)
    private String deleted;


}
