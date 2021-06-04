package com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller;

import com.acrabsoft.web.pojo.user.BasicUser;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.constant.Constants;
import com.acrabsoft.web.utils.JwtUtil;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import org.acrabsoft.utils.security.aes.AESCBCPKCS7;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;

public class BaseController {
    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    @Value("${config.aes.skey}")
    private String skey;
    @Value("${config.aes.iv}")
    private String iv;
    @Value("${config.aes.issecurity}")
    private boolean aes;
    public static final String USER_INFO_HEADER_KEY = "Authorization";


    protected BasicUser getBaseUser() {
        return (BasicUser) request.getAttribute( Constants.HEADER_AUTHOR);
    }

    public BasicUser getBasicUserFromCookie(){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (USER_INFO_HEADER_KEY.equals(cookie.getName())) {
                String headStr = URLDecoder.decode(cookie.getValue());
                String subject = null;

                try {
                    String token = headStr.substring(7);
                    Claims claims = JwtUtil.parseJWT(token);
                    subject = claims.getSubject();

                    if (!StringUtils.isEmpty(subject) && this.aes) {
                        AESCBCPKCS7 aes = new AESCBCPKCS7();
                        subject = aes.decrypt(subject, "UTF-8", this.skey, this.iv);
                    }
                    return (BasicUser) JSON.parseObject(subject, BasicUser.class);
                } catch (GeneralSecurityException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
