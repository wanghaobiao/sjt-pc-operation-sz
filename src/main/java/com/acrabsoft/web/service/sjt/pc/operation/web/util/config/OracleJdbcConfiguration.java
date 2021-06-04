package com.acrabsoft.web.service.sjt.pc.operation.web.util.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class OracleJdbcConfiguration {

    @Bean( "oracleJdbcTemplate")
    @Qualifier("oracleJdbcTemplate")
    @Primary
    public JdbcTemplate jdbcTemplate1(@Qualifier("dataSource") DataSource dataSource){
        JdbcTemplate  jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

}
