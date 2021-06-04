package com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import org.acrabsoft.utils.StringUtil;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "T_SYS_USER_INFO_VIEW")
public class UserInfoEntity {

    // TODO 根据江苏省厅实际的用户数据信息，实现一下这边的用户实体类

    @Column
    private String name;

    @Id
    private String obj_id;

    @Column
    private String idcard;

    @Column
    private String deptid;

    @Column
    private String deptobj;

    @Column
    private String deptname;

    @Column
    private String full_name;
    @Column
    private String policenum;
    @Column
    private String bgsdh;


   /* @Column
    private String gender;


    @Column
    private String duty;

    @Column
    private String email;

    @Column
    private String mobile_long_yidong;

    @Column
    private String mobile_long_dianxin;

    @Column
    private String mobile_other;

    @Column
    private String mobile_short_yidong;

    @Column
    private String mobile_short_dianxin;

    @Column
    private String mobilenumber;

    @Column
    private String mobileshort;
    @Column
    private String alias;

    @Column
    private String isparent;


    @Column
    private String updatetime;

    @Column
    private String weixinid;

    @Column
    private String sync_z;

    @Column
    private int part_type;

    @Column
    private String parentid;

    @Column
    private String m_userid;

    @Column
    private String flag;

    @Column
    private Integer order_index;

    @Column
    private Integer user_type;

    @Column
    private String ordercode;

    @Column
    private String flag_lanxin;

    @Column
    private String areaid;

    @Column
    private String scope_id;

    @Column
    private String is_developer;

    @Column
    private Integer ordernum;

    @Column
    private String type;

    @Column
    private String user_jdb_desc;*/

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String serializeToString() {
        return JSON.toJSONString(this);
    }

    public static UserInfoEntity deSerialize(String cacheString) {
        if (StringUtil.isNullBlank(cacheString)) return null;
        return JSON.parseObject(cacheString, UserInfoEntity.class);
    }

}
