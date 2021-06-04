package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;

import com.acrabsoft.web.Configuration.SpringBeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.acrabsoft.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LanxinMsgService {
    static Logger logger = LoggerFactory.getLogger( LanxinMsgService.class);

    /**
     * 内网获取token
     *
     * @param appid
     * @param secret
     * @return
     */
    public static String getInnerNetToken(String appid, String secret) throws IOException {
        String innerNetTrueTokenUrl = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("innerNetTrueTokenUrl");
        JSONObject requestParams = new JSONObject();
        requestParams.put("appid", appid);
        requestParams.put("secret", secret);
        logger.info("tokenurl::::::" + innerNetTrueTokenUrl + ":::requestParams:::" + requestParams);
        String tokenResult = HttpUtil.sendHttpPostRequest(innerNetTrueTokenUrl, requestParams, null);
        logger.info("tokenresult::::::" + tokenResult);
        return tokenResult;
    }

    //蓝信消息中心提示
    public static String sysNotice(String customerid, String content) throws IOException {
        String noticeResult = "";
        if (!SpringEnvironmentUtil.isDebugMode()) {
            String appid = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("appid");
            String secret = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("secret");
            String noticeFlag = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("noticeFlag");
            String noticeUsers = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("canNoticeUsers");
            String lxAppid = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("lxAppid");
            String textNotice = SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("textNotice");
            if (StringUtil.isNullBlank(customerid)) {
                customerid = "";
            }
            String canNoticeUsers = "";
            if ("1".equals(noticeFlag)) {
                List<String> noticeUserList = new ArrayList<String>();
                noticeUserList = java.util.Arrays.asList(noticeUsers.split(","));
                List<String> wantNoticeUserList = new ArrayList<String>();
                wantNoticeUserList = java.util.Arrays.asList(customerid.split(","));
                List<String> canNoticeUserList = new ArrayList<String>();
                canNoticeUserList = getTheSameSection(noticeUserList, wantNoticeUserList);
                for (int i = 0; i < canNoticeUserList.size(); i++) {
                    canNoticeUsers += (canNoticeUserList.get(i) + ",");
                }
                if (canNoticeUsers.endsWith(",")) {
                    canNoticeUsers = canNoticeUsers.substring(0, canNoticeUsers.length() - 1);
                }
                logger.info("canNoticeUsers1===" + canNoticeUsers);
            } else {
                canNoticeUsers = customerid;
            }
            if (!StringUtil.isNullBlank(canNoticeUsers)) {
                //获取token
                String token = "";
                String tokenStr = LanxinMsgService.getInnerNetToken(appid, secret);
                JSONObject tokenJo = JSON.parseObject(tokenStr);
                if ("T1010".equals(tokenJo.getString("errcode"))) {
                    token = tokenJo.getString("token");
                }
                logger.info("canNoticeUsers2===" + canNoticeUsers);
                JSONObject a = new JSONObject();
                JSONObject o = new JSONObject();
                o.put("content", content);
                a.put("touser", canNoticeUsers);
                a.put("msgtype", "text");
                a.put("text", o);
                a.put("agentid", lxAppid);
                String url = textNotice + "&access_token=" + token;
                logger.info("noticeUrl::::::" + url + ":::paramJson:::" + a);
                noticeResult = HttpUtil.sendHttpPostJsonRequest(url, a.toJSONString(), null);
                logger.info("noticeresult::::::" + noticeResult);
            }
        }
        return noticeResult;
    }

    public static List getTheSameSection(List<String> list1, List<String> list2) {
        List<String> resultList = new ArrayList<>();
        for (String item : list2) {//遍历list2
            if (list1.contains(item)) {//如果存在这个数
                resultList.add(item);//放进一个resultList里面，这个resultList就是交集
            }
        }
        logger.info("resultList:" + resultList);
        return resultList;
    }
}
