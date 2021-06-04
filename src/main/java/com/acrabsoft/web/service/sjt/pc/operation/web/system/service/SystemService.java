package com.acrabsoft.web.service.sjt.pc.operation.web.system.service;

import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
* 系统服务( SystemService )服务类
* @author wanghb
* @since 2020-9-4 15:04:54
*/
@Service("systemService")
public class SystemService {
    @Resource
    private JdbcTemplate jdbcTemplate;
    /**
     * @description  根据前缀获取系列
     * @param  prefix
     * @return
     * @date  2020-10-28 16:58
     * @author  wanghb
     * @edit
     */


    //SIM卡编号前缀
    public static final String sequenceCode = "CODE_";
    //终端采购前缀
    public static final String applicationDeviceCode = "CODE_AD_";
    //终端采购前缀
    public static final String yxPushCode = "YX_PUSH_";
    //终端采购前缀
    public static final String yxPushDelCode = "YX_PUSH_DEL";

    public String getNumberByPrefix(String code,String prefix){
        Integer minDigits = 4;
        String sequenceName = new StringBuilder( code ).append( prefix ).toString();
        //查询序列是否存在
        Integer count = jdbcTemplate.queryForObject( "SELECT count(*) FROM zd_sequence where SEQUENCE_NAME =  ?",new String [] {sequenceName},Integer.class );
        if(count == 0){
            jdbcTemplate.update( "INSERT INTO ZD_SEQUENCE(ID, SEQUENCE_NAME, SEQUENCE_INDEX, DELETED) VALUES ('"+ CodeUtils.getUUID32() +"', '"+sequenceName+"', '1', '0')" );
        }

        String nextval = jdbcTemplate.queryForObject( "select SEQUENCE_INDEX from ZD_SEQUENCE where SEQUENCE_NAME = '"+sequenceName+"'",String.class );
        if (nextval.length() < minDigits) {
            if (nextval.length() == 1) {
                nextval = new StringBuilder( "000" ).append( nextval ).toString();
            }else if (nextval.length() == 2) {
                nextval = new StringBuilder( "00" ).append( nextval ).toString();
            }else if (nextval.length() == 3) {
                nextval = new StringBuilder( "0" ).append( nextval ).toString();
            }
        }
        jdbcTemplate.update( "UPDATE ZD_SEQUENCE SET SEQUENCE_INDEX = "+ (PowerUtil.getInt( nextval ) + 1) +" WHERE SEQUENCE_NAME = '" +sequenceName+ "'");
        nextval = new StringBuilder( prefix ).append( nextval ).toString();
        return nextval;
    }


}
