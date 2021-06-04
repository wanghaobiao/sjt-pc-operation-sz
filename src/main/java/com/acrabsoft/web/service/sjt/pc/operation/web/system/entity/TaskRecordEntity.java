package com.acrabsoft.web.service.sjt.pc.operation.web.system.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

import com.acrabsoft.web.service.sjt.pc.operation.web.util.DateUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 任务执行记录( TaskRecordEntity )实体抽象类
* @author wanghb
* @since 2021-4-15 17:23:27
*/
@Entity
@Getter
@Setter
@Table(name = "yy_task_record")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "任务执行记录( TaskRecordEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class TaskRecordEntity extends  TaskRecordEntityAbstract  implements Serializable {

	public final static String tableName = "yy_task_record";
	public final static String selectSum = "select sum(OPEN_COUNT) openCount,APP_NAME appName from yy_app_open_count where 1 = 1 ";
	public final static String selectSum1 = "select sum(OPEN_COUNT) openCount,APP_NAME appName,DATE_FORMAT(open_date,'"+ DateUtil.DATE_SHORT +"') openDate from yy_app_open_count where 1 = 1 ";
	public final static String countSql = "select count(imei) imeiCount,APP_NAME appName from (select distinct imei,APP_NAME,OPEN_DATE from yy_app_open_count) yy_app_open_count where 1 = 1 ";
	public final static String countSql1 = "select count(distinct APP_NAME) \"value\",area_name \"name\" from yy_app_open_count where 1 = 1 ";
	public final static String countSql2 = "select count(distinct imei) \"value\" from yy_app_open_count where 1 = 1 ";
	public final static String insertSql = "INSERT INTO yy_task_record (name,code,config_json,schedule,status,start_date,end_date,duration,id,deleted,create_user,create_time,update_user,update_time) VALUES (:name,:code,:configJson,:schedule,:status,:startDate,:endDate,:duration,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
	public final static String updateSql = "UPDATE yy_task_record SET name = :name,code = :code,config_json = :configJson,schedule = :schedule,status = :status,start_date = :startDate,end_date = :endDate,duration = :duration,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";

	public  TaskRecordEntity(){
		super();
	}

	@Transient
	@ApiModelProperty(value="状态名称",name="statusName",required=false)
	private String statusName;

	public String getStatusName() {
		return ParamEnum.taskRecordStatus.getNameByCode( super.getStatus() );
	}
}
