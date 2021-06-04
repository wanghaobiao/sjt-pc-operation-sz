
package com.acrabsoft.web.service.sjt.pc.operation.web.system.entity;

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
* 任务执行记录( TaskRecordEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-4-15 17:23:27
*/
@MappedSuperclass
@Getter
@Setter
public class TaskRecordEntityAbstract extends BaseEntity implements Serializable {

    @Basic
    @Column(name = "name")
    @ApiModelProperty(value="任务名称",name="name",required=false)
    private String name;

    @Basic
    @Column(name = "code")
    @ApiModelProperty(value="任务编号",name="code",required=false)
    private String code;

    @Basic
    @Column(name = "config_json")
    @ApiModelProperty(value="任务配置JSON",name="configJson",required=false)
    private String configJson;

    @Basic
    @Column(name = "schedule")
    @ApiModelProperty(value="任务进度(百分比)",name="schedule",required=false)
    private Integer schedule;

    @Basic
    @Column(name = "status")
    @ApiModelProperty(value="任务状态  0  待执行  1执行中  2  执行完毕  3  执行失败",name="status",required=false)
    private String status;

    @Basic
    @Column(name = "start_date")
    @ApiModelProperty(value="任务执行开始时间",name="startDate",required=false)
    @JSONField(format = "%Y-%m-%d HH:mm")
    private Date startDate;

    @Basic
    @Column(name = "end_date")
    @ApiModelProperty(value="任务执行结束时间",name="endDate",required=false)
    @JSONField(format = "%Y-%m-%d HH:mm")
    private Date endDate;

    @Basic
    @Column(name = "duration")
    @ApiModelProperty(value="任务执行时长(秒)",name="duration",required=false)
    private BigDecimal duration;

    @Basic
    @Column(name = "error_info")
    @ApiModelProperty(value="异常信息",name="errorInfo",required=false)
    private String errorInfo;
}
