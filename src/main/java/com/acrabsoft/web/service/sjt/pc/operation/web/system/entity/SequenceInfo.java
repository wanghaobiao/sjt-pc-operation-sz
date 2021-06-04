package com.acrabsoft.web.service.sjt.pc.operation.web.system.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 序列表( SequenceInfo )实体抽象类
* @author wanghb
* @since 2020-12-30 18:25:32
*/
@Entity
@Getter
@Setter
@Table(name = "zd_sequence")
@ApiModel(description= "序列表( SequenceInfo )实体")
@EntityListeners(AuditingEntityListener.class)
public class SequenceInfo extends  SequenceInfoAbstract  implements Serializable {

	public final static String tableName = "zd_sequence";

	public  SequenceInfo(){
		super();
	}

}
