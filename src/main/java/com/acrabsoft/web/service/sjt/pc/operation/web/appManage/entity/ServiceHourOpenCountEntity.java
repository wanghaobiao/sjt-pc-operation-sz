package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

import com.acrabsoft.web.service.sjt.pc.operation.web.util.DateUtil;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 服务分时应用调用次数统计( ServiceHourOpenCountEntity )实体抽象类
* @author wanghb
* @since 2021-4-21 14:46:58
*/
@Entity
@Getter
@Setter
@Table(name = "yy_service_hour_open_count")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "服务分时应用调用次数统计( ServiceHourOpenCountEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class ServiceHourOpenCountEntity extends  ServiceHourOpenCountEntityAbstract  implements Serializable {

	public final static String tableName = "yy_service_hour_open_count";
	public final static String insertSql = "INSERT INTO yy_service_hour_open_count (app_id,app_name,service_id,open_date,area_name,open_count,error_open_count,id,deleted,create_user,create_time,update_user,update_time) VALUES (:appId,:appName,:serviceId,:openDate,:areaName,:openCount,:errorOpenCount,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
	public final static String countSql1 = "select DATE_FORMAT(open_date,'%H:%i') \"name\",open_count \"value\",error_open_count \"errorValue\" from yy_service_hour_open_count where 1 = 1 ";
	public final static String updateSql = "UPDATE yy_service_hour_open_count SET app_id = :appId,app_name = :appName,service_id = :serviceId,open_date = :openDate,area_name = :areaName,open_count = :openCount,error_open_count = :errorOpenCount,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
	public final static String deleteSql = "DELETE from yy_service_hour_open_count WHERE 1 = 1 ";

	public  ServiceHourOpenCountEntity(){
		super();
	}

}
