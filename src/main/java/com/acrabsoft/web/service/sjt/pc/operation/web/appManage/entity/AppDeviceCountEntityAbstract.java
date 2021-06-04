
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
import com.acrabsoft.web.service.sjt.pc.operation.web.util.MiddleEntity;
import io.swagger.annotations.ApiModelProperty;
import com.alibaba.fastjson.annotation.JSONField;

/**
* 应用终端统计( AppDeviceCountEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-4-27 10:38:54
*/
@MappedSuperclass
@Getter
@Setter
public class AppDeviceCountEntityAbstract extends BaseEntity implements Serializable {

    @Basic
    @Column(name = "app_id")
    @ApiModelProperty(value="应用id",name="appId",required=false)
    private String appId;

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
    @Column(name = "device_model")
    @ApiModelProperty(value="设备型号",name="deviceModel",required=false)
    private String deviceModel;

    @Basic
    @Column(name = "device_count")
    @ApiModelProperty(value="数量",name="deviceCount",required=false)
    private Integer deviceCount;
}
