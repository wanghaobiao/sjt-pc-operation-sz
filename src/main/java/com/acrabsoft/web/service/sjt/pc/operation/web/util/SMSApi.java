package com.acrabsoft.web.service.sjt.pc.operation.web.util;

import io.swagger.annotations.ApiOperation;
import org.acrabsoft.utils.StringUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
/*接口规则：

协议规则：
调用API必须遵循以下规则：
传输方式 为保证传输安全性，内网采用HTTP传输
提交方式 采用POST方法提交
数据格式 返回数据为XML格式
字符编码 统一采用UTF-8字符编码
特别提示： 必须严格按照API的说明进行请求，在未得到系统明确的回复之前不要提交新请求，防止重复发送

参数规则：
1、用户名
2、密码
3、手机号码 多个手机号码以逗号相隔，每次建议手机号码数不超过5000条
4、内容 短信内容长度会自动拆分为长短信，建议内容字数不超过500个，内容需进行URLEncoder.encode编码
示例：*/

/**
 * 短信接口示例
 */
@Component
public class SMSApi {
    Logger logger = LoggerFactory.getLogger( SMSApi.class);
    //短信接口地址
    private String sms_gateway = "http://服务域名或IP地址:服务端口/CPPub/mtsmsremotesend.jsp";
    //用户名
    private String appid = "app2"; //ydjwmdm
    //密码
    private String password = "app2";

    @Value("${spring.profiles.active}")
    private String active;
    @Value("${duanxin.push.url}")
    private String duanxinPushUrl;


    /**
     * 说明：建议使用POST方式，
     *
     * @param receivers 接收号码
     * @param message 短信内容
     * @return
     */
    public String post(String receivers, String message) {
        HttpURLConnection conn = null;
        PrintWriter pw = null;
        BufferedReader rd = null;
        String response = "";

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("username=").append(appid);
            sb.append("&password=").append(password);
            sb.append("&receivers=").append(receivers);
            sb.append("&message=").append(URLEncoder.encode(message, "UTF-8"));

            URL url = new URL(sms_gateway);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setReadTimeout(20000);
            conn.setConnectTimeout(20000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.connect();

            pw = new PrintWriter(conn.getOutputStream());
            pw.print(sb.toString());
            pw.flush();
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = rd.readLine()) != null) {
                response += line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
                if (rd != null) {
                    rd.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }


    /**
     * @description  短信发送接口
     * @param  phone  手机号 多个请用,隔开
     * @param  text  短信内容
     * @return  返回结果
     * @date  2020-12-3 13:58
     * @author  wanghb
     * @edit
     */
    public void send(String phone,String text) {
        String resp = "";
        if (!active.equals( "prd,xx-prd" )) {
            resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<MtSendResult>\n" +
                    "<ReturnCode>0</ReturnCode>\n" +
                    "<ReturnNote>发送成功</ReturnNote>\n" +
                    "<Version>1</Version>\n" +
                    "</MtSendResult>";
        } else {
            try {
                logger.info( new StringBuilder( "执行短信发送=====>手机号:" ).append( phone ).toString() );
                resp = SMSApi.post(duanxinPushUrl, phone, text);
                logger.info( new StringBuilder( "短信发送结果=====>手机号:" ).append( phone ).append( ",返回结果:" ).append( resp ).toString() );
            } catch (Exception e) {
                logger.info( new StringBuilder( "短信发送异常=====>手机号:" ).append( phone ).append( ",异常信息:" ).append( e.getMessage() ).toString() );
                resp = "";
                e.printStackTrace();
            }
        }
        logger.info("registCheckCodeResp:::\n" + resp);
        if (!StringUtil.isNullBlank(resp)) {
            // 创建xml解析器
            SAXReader saxReader = new SAXReader();
            // 加载文件,读取到document中
            Document document = null;
            try {
                document = saxReader.read(new ByteArrayInputStream(resp.getBytes("UTF-8")));
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // 得到xml的根节点(message)
            if (document == null) {
                //return "-1";
            } else {
                Element root = document.getRootElement();
                String code = root.element("ReturnCode").getText();
                if ("0".equals(code)) {
                    //return "1";
                } else {
                    //return "-1";
                }
            }
        }
        //return "0";
    }



    /**
     * 说明：建议使用POST方式，
     *
     * @param receivers 接收号码
     * @param message   短信内容
     * @return
     */
    public static String post(String sms_gateway, String receivers, String message) {
        HttpURLConnection conn = null;
        PrintWriter pw = null;
        BufferedReader rd = null;
        String response = "";
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("username=").append("ydjwmdm");
            sb.append("&password=").append("ydjwmdm");
            sb.append("&receivers=").append(receivers);
            sb.append("&message=").append(URLEncoder.encode(message, "UTF-8"));
            URL url = new URL(sms_gateway);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(20000);
            conn.setConnectTimeout(2000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.connect();
            pw = new PrintWriter(conn.getOutputStream());
            pw.print(sb.toString());
            pw.flush();
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = rd.readLine()) != null) {
                response += line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
                if (rd != null) {
                    rd.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

}
