package com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao;


import com.acrabsoft.web.Configuration.SpringBeanUtil;
import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.db.sys.UserInfoEntity;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
@DependsOn("springBeanUtil")
public class UsageDao {
    Logger logger = LoggerFactory.getLogger( UsageDao.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BaseDao baseDao;

    private JdbcTemplate jdbcTemplateB;

    public UsageDao() {
        if (jdbcTemplateB == null) {
            /*String url = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.url");
            String username = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.username");
            String password = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.password");
            String driverClassName = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.driverClassName");
            String initialSize = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.initialSize");
            String minIdle = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.minIdle");
            String maxActive = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.maxActive");
            String maxWait = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.maxWait");
            String timeBetweenEvictionRunsMillis = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.timeBetweenEvictionRunsMillis");
            String minEvictableIdleTimeMillis = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.minEvictableIdleTimeMillis");
            String validationQuery = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.validationQuery");
            String testWhileIdle = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.testWhileIdle");
            String testOnBorrow = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.testOnBorrow");
            String testOnReturn = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.testOnReturn");
            String poolPreparedStatements = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.poolPreparedStatements");
            String maxPoolPreparedStatementPerConnectionSize = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.datasource2.maxPoolPreparedStatementPerConnectionSize");
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setDriverClassName(driverClassName);
            dataSource.setInitialSize(Integer.parseInt(initialSize));
            dataSource.setMinIdle(Integer.parseInt(minIdle));
            dataSource.setMaxActive(Integer.parseInt(maxActive));
            dataSource.setMaxWait(Integer.parseInt(maxWait));
            dataSource.setTimeBetweenConnectErrorMillis(Integer.parseInt(timeBetweenEvictionRunsMillis));
            dataSource.setMinEvictableIdleTimeMillis(Integer.parseInt(minEvictableIdleTimeMillis));
            dataSource.setValidationQuery(validationQuery);
            dataSource.setTestWhileIdle(Boolean.parseBoolean(testWhileIdle));
            dataSource.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));
            dataSource.setTestOnReturn(Boolean.parseBoolean(testOnReturn));
            dataSource.setPoolPreparedStatements(Boolean.parseBoolean(poolPreparedStatements));
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(maxPoolPreparedStatementPerConnectionSize));
            try {
                dataSource.init();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            jdbcTemplateB = new JdbcTemplate();
            jdbcTemplateB.setDataSource(dataSource);*/
        }
    }


    private void batchInsertPeopleUseNumber(List<Object[]> objectList) {
        String sql = "insert into  T_PEOPLE_USE_INFO (ID, MONTH, USENUMBER, AREAID, IDCARD, ROLETYPE, SCOPEID, USERID) VALUES (?,?,?,?,?,?,?,?)";
        this.jdbcTemplate.batchUpdate(sql, objectList);
    }

    public int getUseNumber(String lastMonth, String currentMonth, UserInfoEntity userInfoEntity) {
        String appid = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("appid");
        String sql = "SELECT COUNT(1) CC \n" +
                "  FROM T_APP_CLICK_LOG@YDFWZX_LOG_88 E \n" +
                " WHERE E.CLICK_TIME >= DATE_FORMAT('" + lastMonth + "01000000', 'yyyymmddhh24miss') and E.CLICK_TIME < DATE_FORMAT('" + currentMonth + "01000000', 'yyyymmddhh24miss') \n" +
                "   AND E.APP_ID = ? AND E.USER_ID = ? ";
        return Integer.parseInt(this.jdbcTemplate.queryForList(sql, new Object[]{appid, userInfoEntity.getIdcard()}).get(0).get("CC").toString());
    }

    public List<Map<String, Object>> getAuthorList() {
        String appobj = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("appobj");
        String sql = "SELECT VC.OBJ_ID\n" +
                "  FROM VW_TXL_MANAGE_CONTENT VC\n" +
                " WHERE VC.DEPTID IN\n" +
                "       (SELECT VD.DEPTID\n" +
                "          FROM VW_TXL_MANAGE_DEPT VD\n" +
                "         START WITH VD.OBJ_ID IN\n" +
                "                    (SELECT AD.DEPTID\n" +
                "                       FROM T_APPSTORE_DEPT AD\n" +
                "                      WHERE AD.GL_ID = ? ) --应用主键\n" +
                "        CONNECT BY PRIOR VD.DEPTID = VD.PARENTID)\n" +
                "   AND VC.USER_TYPE IN\n" +
                "       (SELECT AR.ROLEID\n" +
                "          FROM T_APPSTORE_ROLE AR\n" +
                "         WHERE AR.APPID = ? ) --应用主键\n" +
                "UNION\n" +
                "SELECT VC.OBJ_ID\n" +
                "  FROM VW_TXL_MANAGE_CONTENT VC\n" +
                " WHERE VC.IDCARD IN\n" +
                "       (SELECT AU.M_USERID\n" +
                "          FROM T_APPSTORE_USER AU\n" +
                "         WHERE AU.APPID = ? )";
        return this.jdbcTemplateB.queryForList(sql, new Object[]{appobj, appobj, appobj});
    }


    public UserInfoEntity getUserInfoEntity(String userid) {
        return this.baseDao.getById(UserInfoEntity.class, userid);
    }





    public List<Integer> getData(String module, String type, String month) {
        List<Integer> data = new ArrayList<>();
        String whereSql = "where 1=1";
        if (type.equals("shiyong")) {
            whereSql = "where u.USENUMBER > 0";
        }
        if (type.equals("weiyong")) {
            whereSql = "where u.USENUMBER = 0";
        }
        if (!StringUtil.isNullBlank(month)) {
            whereSql = whereSql + " and u.MONTH = '" + month + "'";
        }
        String sql = "";
        if (module.equals("area")) {
            sql = "select d.CODE,d.RANK,NVL(a.cc,0) ncc from  T_DICT_INFO d ,(select u.AREAID,count(1) cc from  T_PEOPLE_USE_INFO u " + whereSql + " group by u.AREAID) a where a.AREAID(+) = d.CODE and d.MODULE = ? order by d.RANK";
        }
        if (module.equals("role")) {
            sql = "select d.CODE,d.RANK,NVL(a.cc,0) ncc from  T_DICT_INFO d ,(select u.ROLETYPE,count(1) cc from  T_PEOPLE_USE_INFO u " + whereSql + " group by u.ROLETYPE) a where a.ROLETYPE(+) = d.CODE and d.MODULE = ? order by d.RANK";
        }
        List<Map<String, Object>> l = this.jdbcTemplate.queryForList(sql, new Object[]{module});
        logger.info(l.toString());
        if (l.size() > 0) {
            for (int i = 0; i < l.size(); i++) {
                data.add(Integer.parseInt(l.get(i).get("RANK").toString()), Integer.parseInt(l.get(i).get("NCC").toString()));
            }
        }
        return data;
    }

    public Pagination getPeopleUseTable(String module, String month, String type, String name, Pagination p) {
        logger.info(module + "--" + month + "--" + type + "--" + name);
        String sqlEnd = " t.USERID = u.OBJ_ID ";
        if (module.equals("area")) {
            sqlEnd += " and t.AREAID = d.CODE and d.MODULE= '" + module + "' and d.value ='" + type + "'";
        }
        if (module.equals("role")) {
            sqlEnd += " and t.ROLETYPE = d.CODE and d.MODULE= '" + module + "' and d.value ='" + type + "'";
        }
        if (!StringUtil.isNullBlank(month)) {
            sqlEnd += " and t.month='" + month + "'";
        }
        if (name.equals("使用人数")) {
            sqlEnd += " and t.usenumber > 0 ";
        }
        if (name.equals("未使用人数")) {
            sqlEnd += " and t.usenumber = 0 ";
        }
        SQL sql = new SQL();
        sql.SELECT("u.*,t.month,t.usenumber,d.value");
        sql.FROM("T_PEOPLE_USE_INFO t ,T_SYS_USER_INFO_VIEW u,T_DICT_INFO d");
        sql.WHERE(sqlEnd);
        sql.ORDER_BY(" u.DEPTID , u.ordercode,u.ordernum");
        return this.baseDao.getPaginationByNactiveSql(sql, p);
    }

    public String getCodeByValue(String value, String module) {
        String sql = "select d.CODE from T_DICT_INFO d where  d.VALUE = ? and d.MODULE = ? ";
        return this.jdbcTemplate.queryForList(sql, new Object[]{value, module}).get(0).get("CODE").toString();
    }
}
