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
* 应用版本统计( AppVersionCountEntity )实体抽象类
* @author wanghb
* @since 2021-4-27 10:39:02
*/
@Entity
@Getter
@Setter
@Table(name = "yy_app_version_count")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "应用版本统计( AppVersionCountEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class AppVersionCountEntity extends  AppVersionCountEntityAbstract  implements Serializable {

	public final static String tableName = "yy_app_version_count";
	public final static String insertSql = "INSERT INTO yy_app_version_count (app_id,app_name,open_date,version_num,version_count,id,deleted,create_user,create_time,update_user,update_time) VALUES (:appId,:appName,:openDate,:versionNum,:versionCount,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
	public final static String updateSql = "UPDATE yy_app_version_count SET app_id = :appId,app_name = :appName,open_date = :openDate,version_num = :versionNum,version_count = :versionCount,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
	public final static String deleteSql = "DELETE from yy_app_version_count WHERE 1 = 1 ";
	public final static String countSql1 = "select version_num \"versionNum\",version_count  \"versionCount\", DATE_FORMAT(open_date,'"+ DateUtil.DATE_SHORT +"')  \"openDate\" from yy_app_version_count where 1 = 1 ";

	public  AppVersionCountEntity(){
		super();
	}

	@Transient
	@ApiModelProperty(value="imeiSet",name="imeiSet",required=false)
	private Set<String> imeiSet;

}
