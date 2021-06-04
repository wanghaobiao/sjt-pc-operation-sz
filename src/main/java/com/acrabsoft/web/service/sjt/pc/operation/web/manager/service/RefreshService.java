package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;

import com.alibaba.fastjson.JSON;
import okhttp3.Response;
import org.acrabsoft.utils.okhttp.OkHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@EnableAsync
public class RefreshService {
    Logger logger = LoggerFactory.getLogger( RefreshService.class);

    @Value("${refreshUserRoleCache}")
    private String refreshUserRoleCache;

    @Value("${refreshUserUrl}")
    private String refreshUserUrl;

    @Value("${refreshDeptUrl}")
    private String refreshDeptUrl;

    @Value("${refreshBlackListUrl}")
    private String refreshBlackListUrl;

    @Async
    public void refreshUserRoleCache(String userId) {
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String url = refreshUserRoleCache;
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        try {
            logger.info("refreshUserRoleCache:::" + JSON.toJSONString(map));
            Response response = OkHttpUtil.postForm(url, map);
            logger.info(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void refreshUser(String userId) {
        String url = refreshUserUrl;
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        try {
            logger.info("refreshUser:::" + JSON.toJSONString(map));
            Response response = OkHttpUtil.postForm(url, map);
            logger.info(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void refreshDept(String deptId) {
        String url = refreshDeptUrl;
        Map<String, String> map = new HashMap<>();
        map.put("deptId", deptId);
        try {
            logger.info("refreshDept:::" + JSON.toJSONString(map));
            Response response = OkHttpUtil.postForm(url, map);
            logger.info(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void refreshBlackList(String userId) {
        String url = refreshBlackListUrl;
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        try {
            logger.info("refreshBlackList:::" + JSON.toJSONString(map));
            Response response = OkHttpUtil.postForm(url, map);
            logger.info(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
