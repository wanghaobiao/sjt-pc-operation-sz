package com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo;

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
@Table(name = "t_service_dic_info")
public class ServiceDic {
    @Id
    private String id;

    @Column
    private String code;

    @Column
    private String type;

    @Column
    private String codename;

    @Column
    private Date createTime;
}
