package com.acrabsoft.web.service.sjt.pc.operation.web.system.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* hbaseRowkey标识对照表( HbaseRowkeyMarkEntity )实体抽象类
* @author wanghb
* @since 2021-4-9 11:57:53
*/
@Entity
@Getter
@Setter
@Table(name = "yy_hbase_rowkey_mark")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "hbaseRowkey标识对照表( HbaseRowkeyMarkEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class HbaseRowkeyMarkEntity extends  HbaseRowkeyMarkEntityAbstract  implements Serializable {

	public final static String tableName = "yy_hbase_rowkey_mark";
	public final static String insertSql = "INSERT INTO yy_hbase_rowkey_mark (type,type_remark,rowkey_code,rowkey_value,id,deleted,create_user,create_time,update_user,update_time) VALUES (:type,:typeRemark,:rowkeyCode,:rowkeyValue,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
	public final static String updateSql = "UPDATE yy_hbase_rowkey_mark SET type = :type,type_remark = :typeRemark,rowkey_code = :rowkeyCode,rowkey_value = :rowkeyValue,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
	//更新 rowkey_value  通过 rowkey_code 和 type
	public final static String updateSql01 = "UPDATE yy_hbase_rowkey_mark SET rowkey_value = :rowkeyValue,update_user= :updateUser,update_time= :updateTime WHERE  type = :type and rowkey_code = :rowkeyCode and rowkey_value is null ";

	public  HbaseRowkeyMarkEntity(){
		super();
	}


}
