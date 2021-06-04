package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 应用信息表( AppInfoEntity )实体抽象类
* @author wanghb
* @since 2021-4-26 10:48:57
*/
@Entity
@Getter
@Setter
@Table(name = "yy_app_info")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "应用信息表( AppInfoEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class AppInfoEntity extends  AppInfoEntityAbstract  implements Serializable {

	public final static String tableName = "yy_app_info";
	public final static String insertSql = "INSERT INTO yy_app_info (app_description,app_id,app_name,dept_code,app_type,app_url,area,category,network_area,person_obj_id,power_type,status,type,city,id,deleted,create_user,create_time,update_user,update_time) VALUES (:appDescription,:appId,:appName,:deptCode,:appType,:appUrl,:area,:category,:networkArea,:personObjId,:powerType,:status,:type,:city,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
	public final static String updateSql = "UPDATE yy_app_info SET app_description = :appDescription,app_id = :appId,app_name = :appName,dept_code = :deptCode,app_type = :appType,app_url = :appUrl,area = :area,category = :category,network_area = :networkArea,person_obj_id = :personObjId,power_type = :powerType,status = :status,type = :type,city = :city,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
	public final static String deleteSql = "DELETE from yy_app_info WHERE 1 = 1 ";
    public final static String countSql1 = "select id \"id\" from yy_app_info where 1 = 1 ";

	public  AppInfoEntity(){
		super();
	}

}
