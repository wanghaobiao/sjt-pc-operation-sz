package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

import com.acrabsoft.web.service.sjt.pc.operation.web.util.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 应用终端统计( AppDeviceCountEntity )实体抽象类
* @author wanghb
* @since 2021-4-27 10:38:54
*/
@Entity
@Getter
@Setter
@Table(name = "yy_app_device_count")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "应用终端统计( AppDeviceCountEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class AppDeviceCountEntity extends  AppDeviceCountEntityAbstract  implements Serializable {

	public final static String tableName = "yy_app_device_count";
	public final static String insertSql = "INSERT INTO yy_app_device_count (app_id,app_name,open_date,device_model,device_count,id,deleted,create_user,create_time,update_user,update_time) VALUES (:appId,:appName,:openDate,:deviceModel,:deviceCount,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
	public final static String updateSql = "UPDATE yy_app_device_count SET app_id = :appId,app_name = :appName,open_date = :openDate,device_model = :deviceModel,device_count = :deviceCount,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
	public final static String deleteSql = "DELETE from yy_app_device_count WHERE 1 = 1 ";
    public final static String countSql1 = "select device_model \"deviceModel\",device_count  \"deviceCount\", DATE_FORMAT(open_date,'"+ DateUtil.DATE_SHORT +"')  \"openDate\" from yy_app_device_count where 1 = 1 ";

	public  AppDeviceCountEntity(){
		super();
	}

	@Transient
	@ApiModelProperty(value="imeiSet",name="imeiSet",required=false)
	private Set<String> imeiSet;

}
