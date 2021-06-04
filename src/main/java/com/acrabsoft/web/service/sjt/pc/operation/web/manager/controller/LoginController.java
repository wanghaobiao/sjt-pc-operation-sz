package com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller;

import com.acrabsoft.web.annotation.NotAuthorize;
import com.acrabsoft.web.pojo.user.BasicUser;
import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.constant.Constants;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao.UserinfoDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.pojo.UserLoginInfo;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.service.LoginService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.service.RoleService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import com.acrabsoft.web.utils.JwtUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.Constant;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.acrabsoft.utils.security.aes.AESCBCPKCS7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(tags = "登录功能")
@RestController
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private LoginService loginService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private UserinfoDao userinfoDao;


    @Value("${config.aes.issecurity}")
    private boolean aes;

    @Value("${config.aes.skey}")
    private String skey;

    @Value("${config.aes.iv}")
    private String iv;

    @NotAuthorize
    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @ApiOperation(value = "用户登录", nickname = "lvhb", notes = "用户登录")
    @Transactional(rollbackOn = Exception.class)
    public Result login(@RequestParam @ApiParam("账号") String account, @RequestParam @ApiParam("密码") String password) throws NoSuchAlgorithmException {
        BasicUser basicUser = new BasicUser();
        basicUser.setUserid( "13629848747" );
        String jwtstr = JwtUtil.getJwtStrByPreFix( JSON.toJSONString(basicUser));
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, jwtstr);
        /*logger.info("账号登陆===>"+account);
        Map<String, Object> map = this.loginService.checkuser(account);
        UserLoginInfo userLoginInfo = null;
        if (null == map || map.isEmpty()) {
            Map<String, Object> user = userinfoDao.getUserByPoliceNum( account );
            if (null == user) {
                return BuildResult.buildOutResult(ResultEnum.ERROR, "此警号不存在！");
            }else{
                userLoginInfo = loginService.getUserLogin( user );
                String idCard = userLoginInfo.getIdcard();
                if(PowerUtil.isNull( idCard ) || idCard.length() < 6){
                    return BuildResult.buildOutResult( ResultEnum.ERROR,"账号异常无法登陆,请联系管理员。");
                }
                int idCardLength = idCard.length();
                String passwordTemp = idCard.substring(idCardLength - 6 , idCardLength);
                userLoginInfo.setPassword( passwordTemp );
                baseDao.save( userLoginInfo );
                map = this.loginService.checkuser(account);

            }
        }
        if(!PowerUtil.getString( map.get( "password" ) ).equals( password )){
            return BuildResult.buildOutResult(ResultEnum.ERROR, "账号或密码错误！");
        }
        BasicUser user = new BasicUser();
        user.setUserid((String) map.get("objId"));
        user.setUsername((String) map.get("name"));
        Result rs = this.build(user);
        JSONObject result;
        if (rs.getErrcode().equals("0")) {
            result = new JSONObject();
            JSONObject jo = (JSONObject) rs.getData();
            String auth = (String) jo.get( Constants.HEADER_AUTHOR);
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> role = roleService.getRoleByUserId(user.getUserid());
            list.add(role);
            result.put(Constants.HEADER_AUTHOR, auth);
            result.put("Authorization_key", "stzc_Authorization");
            result.put("userinfo",map);
            result.put("roles",list);
            return BuildResult.buildOutResult(ResultEnum.SUCCESS, result);
        }
        return BuildResult.buildOutResult(ResultEnum.ERROR, "登录响应异常");*/
    }


    /**
     * @description  通过身份证号获取登陆信息
     * @param  idCard   身份证号
     * @return
     * @date  2020-11-4 16:17
     * @author  wanghb
     * @edit
     */
    @NotAuthorize
    @RequestMapping(value = "/loginByIdCard", method = {RequestMethod.POST})
    @ApiOperation(value = "用户登录", nickname = "lvhb", notes = "用户登录")
    @Transactional(rollbackOn = Exception.class)
    public Result loginByIdCard(@RequestParam @ApiParam("身份证号") String idCard) throws NoSuchAlgorithmException {
        logger.info("数字证书登陆===>"+idCard);
        Map<String, Object> map = this.loginService.checkuser(idCard);
        UserLoginInfo userLoginInfo = null;
        if (map == null || map.isEmpty()) {
            Map<String, Object> user = userinfoDao.getUserByIdCard( idCard );
            if (null == user) {
                return BuildResult.buildOutResult(ResultEnum.ERROR, "此数字证书不存在！");
            }else{
                userLoginInfo = loginService.getUserLogin( user );
                if(PowerUtil.isNull( idCard ) || idCard.length() < 6){
                    return BuildResult.buildOutResult( ResultEnum.ERROR,"账号异常无法登陆,请联系管理员。");
                }
                int idCardLength = idCard.length();
                String password = idCard.substring(idCardLength - 6 , idCardLength);
                userLoginInfo.setPassword( password );
                baseDao.save( userLoginInfo );
                map = this.loginService.checkuser(idCard);
            }
        }
        BasicUser user = new BasicUser();
        user.setUserid((String) map.get("objId"));
        user.setUsername((String) map.get("name"));
        Result rs = this.build(user);
        JSONObject result;
        if (rs.getErrcode().equals("0")) {
            result = new JSONObject();
            JSONObject jo = (JSONObject) rs.getData();
            String auth = (String) jo.get(Constants.HEADER_AUTHOR);
            List<Map<String, Object>> list = new ArrayList<>();
            result.put(Constants.HEADER_AUTHOR, auth);
            result.put("Authorization_key", "stzc_Authorization");
            result.put("userinfo",map);
            result.put("roles",list);
            return BuildResult.buildOutResult(ResultEnum.SUCCESS, result);
        }
        return BuildResult.buildOutResult(ResultEnum.ERROR, "登录响应异常");
    }


    @NotAuthorize
    @RequestMapping(value = "/zslogin", method = {RequestMethod.POST})
    @ApiOperation(value = "数字证书登录", nickname = "lvhb", notes = "数字证书登录")
    public Result zslogin(@RequestParam @ApiParam("账号") String userIdCard) throws NoSuchAlgorithmException {
        Map<String, Object> map = this.loginService.checkuserZslogin(userIdCard);
        if (null == map) {
            return BuildResult.buildOutResult(ResultEnum.ERROR, "账号不存在！");
        }
        BasicUser user = new BasicUser();
        user.setUserid((String) map.get("objId"));
        user.setUsername((String) map.get("name"));
        Result rs = this.build(user);
        JSONObject result;
        if (rs.getErrcode().equals("0")) {
            result = new JSONObject();
            JSONObject jo = (JSONObject) rs.getData();
            String auth = (String) jo.get(Constants.HEADER_AUTHOR);
            result.put(Constants.HEADER_AUTHOR, auth);
            result.put("Authorization_key", "stzc_Authorization");
            result.put("userinfo",map);
            return BuildResult.buildOutResult(ResultEnum.SUCCESS, result);
        }
        return BuildResult.buildOutResult(ResultEnum.ERROR, "登录响应异常");
    }


    private Result build(BasicUser user) {
        if (user == null) {
            logger.debug("自定义登录接口返回空");
            return BuildResult.buildOutResult(ResultEnum.FAIL, "用户信息不存在");
        }
        String userjson = JSON.toJSONString(user);
        JSONObject jo = JSON.parseObject(userjson);
        if (aes) {
            //对用户信息进行AES加密
            AESCBCPKCS7 aes = new AESCBCPKCS7();
            try {
                userjson = aes.encrypt(userjson, Constant.UTF_8, skey, iv);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.error("加密失败", e);
                return BuildResult.buildOutResult(ResultEnum.FAIL);
            }
        }
        String jwtstr = JwtUtil.getJwtStrByPreFix(userjson);
        jo.put(Constants.HEADER_AUTHOR, jwtstr);
        return BuildResult.buildOutResult(ResultEnum.SUCCESS, jo);
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test() {
        logger.info("aaaa");
    }
}
