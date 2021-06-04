
package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;

import javax.persistence.*;
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
* 应用分时人员启动次数统计( AppHourOpenCountEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-4-21 18:49:43
*/
@MappedSuperclass
@Getter
@Setter
public class AppHourOpenCountEntityAbstract extends BaseEntity implements Serializable {

    @Basic
    @Column(name = "app_name")
    @ApiModelProperty(value="应用名称",name="appName",required=false)
    private String appName;

    @Basic
    @Column(name = "open_date")
    @ApiModelProperty(value="启动日期(分时)",name="openDate",required=false)
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
}
