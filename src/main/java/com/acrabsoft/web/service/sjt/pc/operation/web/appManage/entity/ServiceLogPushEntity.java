package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServiceLogPushEntity {
    @ApiModelProperty(value="rowKey",name="rowKey",required=false)
    private String rowKey;
    @ApiModelProperty(value="主键id",name="id",required=false)
    private String id;
    @ApiModelProperty(value="调用方名称",name="appName",required=false)
    private String appName;
    @ApiModelProperty(value="应用ID",name="appId",required=false)
    private String appId;
    @ApiModelProperty(value="调用人员",name="userId",required=false)
    private String userId;
    @ApiModelProperty(value="服务方",name="serviceId",required=false)
    private String serviceId;
    @ApiModelProperty(value="负载编号",name="loadNum",required=false)
    private String loadNum;
    @ApiModelProperty(value="调用时间",name="callTime",required=false)
    private String callTime;
    @ApiModelProperty(value="服务地址 ",name="serviceAddress",required=false)
    private String serviceAddress;
    @ApiModelProperty(value="请求参数",name="requestParam",required=false)
    private String requestParam;
    @ApiModelProperty(value="请求头",name="requestHeader",required=false)
    private String requestHeader;
    @ApiModelProperty(value="响应时间",name="responseTime",required=false)
    private String responseTime;
    @ApiModelProperty(value="调用时长",name="callDuration",required=false)
    private BigDecimal callDuration;
    @ApiModelProperty(value="是否异常",name="isError",required=false)
    private String isError;
    @ApiModelProperty(value="异常信息",name="errorInfo",required=false)
    private String errorInfo;
    @ApiModelProperty(value="响应内容",name="responseContent",required=false)
    private String responseContent;
    @ApiModelProperty(value="响应头",name="responseHeader",required=false)
    private String responseHeader;


}
