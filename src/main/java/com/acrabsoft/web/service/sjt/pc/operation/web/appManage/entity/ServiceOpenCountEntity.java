package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 服务每日应用调用次数统计( ServiceOpenCountEntity )实体抽象类
* @author wanghb
* @since 2021-4-21 14:46:49
*/
@Entity
@Getter
@Setter
@Table(name = "yy_service_open_count")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "服务每日应用调用次数统计( ServiceOpenCountEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class ServiceOpenCountEntity extends  ServiceOpenCountEntityAbstract  implements Serializable {

	public final static String tableName = "yy_service_open_count";
	public final static String insertSql = "INSERT INTO yy_service_open_count (app_id,app_name,service_name,service_id,open_date,area_name,open_count,error_open_count,load_num,id,deleted,create_user,create_time,update_user,update_time) VALUES (:appId,:appName,:serviceName,:serviceId,:openDate,:areaName,:openCount,:errorOpenCount,:loadNum,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
	public final static String countSql1 = "select area_name \"name\",sum(open_count) \"value\" from YY_SERVICE_OPEN_COUNT where 1 = 1 ";
	public final static String countSql2 = "select sum(open_count) \"value\" from YY_SERVICE_OPEN_COUNT where 1 = 1 ";
	public final static String countSql3 = "select sum(open_count) \"value\",sum(error_open_count) \"errorValue\",load_num \"loadNum\" from YY_SERVICE_OPEN_COUNT where 1 = 1 ";
	public final static String updateSql = "UPDATE yy_service_open_count SET app_id = :appId,app_name = :appName,service_name = :serviceName,service_id = :serviceId,open_date = :openDate,area_name = :areaName,open_count = :openCount,error_open_count = :errorOpenCount,load_num = :loadNum,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
	public final static String deleteSql = "DELETE from yy_service_open_count WHERE 1 = 1 ";

	public  ServiceOpenCountEntity(){
		super();
	}

}
