
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
* 应用信息表( AppInfoEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-4-26 10:48:58
*/
@MappedSuperclass
@Getter
@Setter
public class AppInfoEntityAbstract extends BaseEntity implements Serializable {

    @Basic
    @Column(name = "app_description")
    @ApiModelProperty(value="应用说明",name="appDescription",required=false)
    private String appDescription;

    @Basic
    @Column(name = "app_id")
    @ApiModelProperty(value="应用id",name="appId",required=false)
    private String appId;

    @Basic
    @Column(name = "app_name")
    @ApiModelProperty(value="应用名称",name="appName",required=false)
    private String appName;

    @Basic
    @Column(name = "dept_code")
    @ApiModelProperty(value="部门编号",name="deptCode",required=false)
    private String deptCode;

    @Basic
    @Column(name = "app_type")
    @ApiModelProperty(value="应用类型：1.原生应用；2.H5应用",name="appType",required=false)
    private String appType;

    @Basic
    @Column(name = "app_url")
    @ApiModelProperty(value="应用地址，H5应用必填，为应用打开地址；原生应用为下载文件ID",name="appUrl",required=false)
    private String appUrl;

    @Basic
    @Column(name = "area")
    @ApiModelProperty(value="应用发布地区",name="area",required=false)
    private String area;

    @Basic
    @Column(name = "category")
    @ApiModelProperty(value="应用业务类型分类《苏警通字典附件》-7",name="category",required=false)
    private String category;

    @Basic
    @Column(name = "network_area")
    @ApiModelProperty(value="应用网络区域：1.Ⅰ类区；2.Ⅱ类区；3.Ⅲ类区",name="networkArea",required=false)
    private String networkArea;

    @Basic
    @Column(name = "person_obj_id")
    @ApiModelProperty(value="责任人信息主键",name="personObjId",required=false)
    private String personObjId;

    @Basic
    @Column(name = "power_type")
    @ApiModelProperty(value="应用授权方式",name="powerType",required=false)
    private String powerType;

    @Basic
    @Column(name = "status")
    @ApiModelProperty(value="信息状态：0.正常；1.应用注册",name="status",required=false)
    private String status;

    @Basic
    @Column(name = "type")
    @ApiModelProperty(value="应用种类：      * 1.普通应用：会发布到应用市场      * 2.服务应用：不会发布到应用市场",name="type",required=false)
    private String type;

    @Basic
    @Column(name = "city")
    @ApiModelProperty(value="地市",name="city",required=false)
    private String city;
}
