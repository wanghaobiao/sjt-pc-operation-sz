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
* 应用分时人员启动次数统计( AppHourOpenCountEntity )实体抽象类
* @author wanghb
* @since 2021-4-21 18:49:44
*/
@Entity
@Getter
@Setter
@Table(name = "yy_app_hour_open_count")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "应用分时人员启动次数统计( AppHourOpenCountEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class AppHourOpenCountEntity extends  AppHourOpenCountEntityAbstract  implements Serializable {

	public final static String tableName = "yy_app_hour_open_count";
	public final static String insertSql = "INSERT INTO yy_app_hour_open_count (app_name,open_date,area_name,imei,open_count,device_model,id,deleted,create_user,create_time,update_user,update_time) VALUES (:appName,:openDate,:areaName,:imei,:openCount,:deviceModel,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
	public final static String countSql1 = "select DATE_FORMAT(open_date,'%H:%i') \"name\",open_count \"value\" from yy_app_hour_open_count where 1 = 1 ";
	public final static String updateSql = "UPDATE yy_app_hour_open_count SET app_name = :appName,open_date = :openDate,area_name = :areaName,imei = :imei,open_count = :openCount,device_model = :deviceModel,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
	public final static String deleteSql = "DELETE from yy_app_hour_open_count WHERE 1 = 1 ";

	public  AppHourOpenCountEntity(){
		super();
	}

}
