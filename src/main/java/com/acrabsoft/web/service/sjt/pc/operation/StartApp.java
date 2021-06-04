package com.acrabsoft.web.service.sjt.pc.operation;

import com.acrabsoft.web.App;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppLogService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ScheduledTasks;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.SpringContextUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.net.URL;



/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableJpaAuditing
public class StartApp
{

    public static void main( String[] args )
    {
    	SpringApplication.run( App.class, args);
        SpringContextUtil.getBean( ScheduledTasks.class ).refreshParams();
        SpringContextUtil.getBean( ScheduledTasks.class ).refreshServiceInfo();
        SpringContextUtil.getBean( ScheduledTasks.class ).refreshAppInfo();
        SpringContextUtil.getBean( ScheduledTasks.class ).refreshRowkeyMark();
        System.out.println("===============================>项目启动成功<===============================");
        SpringContextUtil.getBean( AppLogService.class ).appLogReceiver();

    }

}
