
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
import com.acrabsoft.web.service.sjt.pc.operation.web.util.MiddleEntity;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
* 序列表( SequenceInfoAbstract )实体抽象类
* @author wanghb
* @since 2020-12-30 18:36:24
*/
@MappedSuperclass
@Getter
@Setter
public class SequenceInfoAbstract extends BaseEntity implements Serializable {

    @Basic
    @Column(name = "sequence_name")
    @ApiModelProperty(value="序列编号",name="sequenceName",required=false)
    private String sequenceName;

    @Basic
    @Column(name = "sequence_index")
    @ApiModelProperty(value="序列下标",name="sequenceIndex",required=false)
    private Integer sequenceIndex;
}
