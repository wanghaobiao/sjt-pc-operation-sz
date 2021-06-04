package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AppLogPushEntity {
    @ApiModelProperty(value="rowKey",name="rowKey",required=false)
    private String rowKey;
    @ApiModelProperty(value="主键id",name="id",required=false)
    private String id;
    @ApiModelProperty(value="应用名称",name="name",required=false)
    private String name;
    @ApiModelProperty(value="应用编号",name="code",required=false)
    private String code;
    @ApiModelProperty(value="所属地市编号",name="areaCode",required=false)
    private String areaCode;
    @ApiModelProperty(value="所属地市名称",name="areaName",required=false)
    private String areaName;
    @ApiModelProperty(value="所属部门编号",name="deptCode",required=false)
    private String deptCode;
    @ApiModelProperty(value="所属部门名称",name="deptName",required=false)
    private String deptName;
    @ApiModelProperty(value="责任公司编号",name="companyCode",required=false)
    private String companyCode;
    @ApiModelProperty(value="责任公司名称",name="companyName",required=false)
    private String companyName;
    @ApiModelProperty(value="应用类型",name="businessType",required=false)
    private String businessType;
    @ApiModelProperty(value="应用启动时间",name="startTime",required=false)
    private String startTime;
    @ApiModelProperty(value="imsi",name="imsi",required=false)
    private String imsi;
    @ApiModelProperty(value="imei",name="imei",required=false)
    private String imei;
    @ApiModelProperty(value="应用启动人员编号",name="startPersonCode",required=false)
    private String startPersonCode;
    @ApiModelProperty(value="应用启动人员名称",name="startPerson",required=false)
    private String startPerson;
    @ApiModelProperty(value="应用使用设备型号",name="deviceModel",required=false)
    private String deviceModel;
    @ApiModelProperty(value="应用所属链路",name="networkEnv",required=false)
    private String networkEnv;
    @ApiModelProperty(value="应用版本号",name="versionNum",required=false)
    private String versionNum;

}
