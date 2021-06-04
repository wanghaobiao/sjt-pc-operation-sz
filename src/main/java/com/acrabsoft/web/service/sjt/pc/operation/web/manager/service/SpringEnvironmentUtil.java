package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;


import com.acrabsoft.web.Configuration.SpringBeanUtil;

public class SpringEnvironmentUtil {

    /**
     * 当前系统是否在开发模式下
     *
     * @return
     */
    public static boolean isDebugMode() {
        String activeProfiles = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("spring.profiles.active");
        return activeProfiles != null && activeProfiles.contains("dev");
    }

}
