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
* 应用分时活跃用户统计( AppHourActiveCountEntity )实体抽象类
* @author wanghb
* @since 2021-4-23 14:22:40
*/
@Entity
@Getter
@Setter
@Table(name = "yy_app_hour_active_count")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "应用分时活跃用户统计( AppHourActiveCountEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class AppHourActiveCountEntity extends  AppHourActiveCountEntityAbstract  implements Serializable {

	public final static String tableName = "yy_app_hour_active_count";
	public final static String insertSql = "INSERT INTO yy_app_hour_active_count (open_date,area_name,open_count,device_model,app_name,id,deleted,create_user,create_time,update_user,update_time) VALUES (:openDate,:areaName,:openCount,:deviceModel,:appName,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
	public final static String updateSql = "UPDATE yy_app_hour_active_count SET open_date = :openDate,area_name = :areaName,open_count = :openCount,device_model = :deviceModel,app_name = :appName,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
	public final static String deleteSql = "DELETE from yy_app_hour_active_count WHERE 1 = 1 ";
    public final static String countSql = "select id \"id\" from yy_app_hour_active_count where 1 = 1 ";
	public final static String countSql1 = "select DATE_FORMAT(open_date,'%H:%i') \"name\",open_count \"value\" from yy_app_hour_active_count where 1 = 1 ";


	public  AppHourActiveCountEntity(){
		super();
	}

	@Transient
	@ApiModelProperty(value="imeiSet集合",name="imeiSet",required=false)
	private Set<String> imeiSet;

}
