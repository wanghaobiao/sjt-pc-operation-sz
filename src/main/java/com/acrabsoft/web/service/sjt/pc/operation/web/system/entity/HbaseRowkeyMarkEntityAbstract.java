
package com.acrabsoft.web.service.sjt.pc.operation.web.system.entity;

import com.acrabsoft.web.service.sjt.pc.operation.web.util.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.*;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
* hbaseRowkey标识对照表( HbaseRowkeyMarkEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-4-9 11:57:53
*/
@MappedSuperclass
@Getter
@Setter
public class HbaseRowkeyMarkEntityAbstract extends BaseEntity implements Serializable {

    @Basic
    @Column(name = "type")
    @ApiModelProperty(value="类型",name="type",required=false)
    private String type;

    @Basic
    @Column(name = "type_remark")
    @ApiModelProperty(value="类型说明",name="typeRemark",required=false)
    private String typeRemark;

    @Basic
    @Column(name = "rowkey_code")
    @ApiModelProperty(value="rowkey标识",name="rowkeyCode",required=false)
    private String rowkeyCode;

    @Basic
    @Column(name = "rowkey_value")
    @ApiModelProperty(value="rowkey对应的值",name="rowkeyValue",required=false)
    private String rowkeyValue;
}
