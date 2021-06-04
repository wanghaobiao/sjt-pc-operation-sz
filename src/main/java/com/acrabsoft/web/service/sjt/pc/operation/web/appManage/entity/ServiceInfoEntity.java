package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 服务信息( ServiceInfoEntity )实体抽象类
* @author wanghb
* @since 2021-4-26 10:56:24
*/
@Entity
@Getter
@Setter
@Table(name = "yy_service_info")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "服务信息( ServiceInfoEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class ServiceInfoEntity extends  ServiceInfoEntityAbstract  implements Serializable {

	public final static String tableName = "yy_service_info";
	public final static String insertSql = "INSERT INTO yy_service_info (app_id,approval_state,approval_time,approver_identifier,available_platform,call_frequency,cmc_id,collect_time,dep_code,description,formal_table_id,interface_address,interface_type,open_path,operator_type,owner_identifier,platform,power_type, regionalism_code,release_range,report_type,service_area,service_center,service_id,service_level,service_name,service_type,service_version,status,time_out,update_status,visibility,api_file_ids,id,deleted,create_user,create_time,update_user,update_time) VALUES (:appId,:approvalState,:approvalTime,:approverIdentifier,:availablePlatform,:callFrequency,:cmcId,:collectTime,:depCode,:description,:formalTableId,:interfaceAddress,:interfaceType,:openPath,:operatorType,:ownerIdentifier,:platform,:powerType,: regionalismCode,:releaseRange,:reportType,:serviceArea,:serviceCenter,:serviceId,:serviceLevel,:serviceName,:serviceType,:serviceVersion,:status,:timeOut,:updateStatus,:visibility,:apiFileIds,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
	public final static String updateSql = "UPDATE yy_service_info SET app_id = :appId,approval_state = :approvalState,approval_time = :approvalTime,approver_identifier = :approverIdentifier,available_platform = :availablePlatform,call_frequency = :callFrequency,cmc_id = :cmcId,collect_time = :collectTime,dep_code = :depCode,description = :description,formal_table_id = :formalTableId,interface_address = :interfaceAddress,interface_type = :interfaceType,open_path = :openPath,operator_type = :operatorType,owner_identifier = :ownerIdentifier,platform = :platform,power_type = :powerType, regionalism_code = : regionalismCode,release_range = :releaseRange,report_type = :reportType,service_area = :serviceArea,service_center = :serviceCenter,service_id = :serviceId,service_level = :serviceLevel,service_name = :serviceName,service_type = :serviceType,service_version = :serviceVersion,status = :status,time_out = :timeOut,update_status = :updateStatus,visibility = :visibility,api_file_ids = :apiFileIds,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
	public final static String deleteSql = "DELETE from yy_service_info WHERE 1 = 1 ";
    public final static String countSql1 = "select id \"id\" from yy_service_info where 1 = 1 ";

	public  ServiceInfoEntity(){
		super();
	}

}
