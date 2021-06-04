package com.acrabsoft.web.service.sjt.pc.operation.web.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
public class MiddleEntity extends BaseEntity implements Serializable {
    @Basic
    @Column(name = "parent_id")
    @ApiModelProperty(value="父对象关联id",name="parentId",required=false)
    private String parentId;
}



