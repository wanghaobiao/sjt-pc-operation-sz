
package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.*;
import javax.persistence.OneToMany;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.alibaba.fastjson.annotation.JSONField;

/**
* 应用每日人员启动次数统计( AppOpenCountEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-4-16 15:58:46
*/
@MappedSuperclass
@Getter
@Setter
public class AppOpenCountEntityAbstract extends BaseEntity implements Serializable {

    @Basic
    @Column(name = "app_name")
    @ApiModelProperty(value="应用名称",name="appName",required=false)
    private String appName;

    @Basic
    @Column(name = "open_date")
    @ApiModelProperty(value="启动日期",name="openDate",required=false)
    @JSONField(format = "%Y-%m-%d HH:mm")
    private Date openDate;

    @Basic
    @Column(name = "area_name")
    @ApiModelProperty(value="地区名称",name="areaName",required=false)
    private String areaName;

    @Basic
    @Column(name = "imei")
    @ApiModelProperty(value="imei",name="imei",required=false)
    private String imei;

    @Basic
    @Column(name = "open_count")
    @ApiModelProperty(value="启动次数",name="openCount",required=false)
    private Integer openCount;

    @Basic
    @Column(name = "device_model")
    @ApiModelProperty(value="终端型号",name="deviceModel",required=false)
    private String deviceModel;

    @Basic
    @Column(name = "police_code")
    @ApiModelProperty(value="警种编号",name="police_code",required=false)
    private String police_code;

    @Basic
    @Column(name = "category_code")
    @ApiModelProperty(value="业务类型编号",name="category_code",required=false)
    private String category_code;

    @Basic
    @Column(name = "police_name")
    @ApiModelProperty(value="警种名称",name="police_name",required=false)
    private String police_name;

    @Basic
    @Column(name = "category_name")
    @ApiModelProperty(value="业务类型名称",name="category_name",required=false)
    private String category_name;
}
