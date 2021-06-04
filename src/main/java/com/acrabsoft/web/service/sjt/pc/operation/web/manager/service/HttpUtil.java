package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 与Http协议相关的一些工具
 *
 * @author hasee
 */
@SuppressWarnings("deprecation")
public class HttpUtil {

    private static Logger logger = Logger.getLogger(HttpUtil.class);

    /**
     * 将Http请求中的参数包装为JSONObject格式的对象并返回（action参数除外）
     *
     * @param request
     * @return
     */
    public static JSONObject packHttpRequestParams(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Enumeration<String> namesEnum = request.getParameterNames();
        JSONObject paramJo = new JSONObject();
        while (namesEnum.hasMoreElements()) {

            String paramName = namesEnum.nextElement();
            if (!"action".equals(paramName)) {
                String paramVal = request.getParameter(paramName);
                paramJo.put(paramName, paramVal);
            }
        }
        return paramJo;
    }

    /**
     * 以流得方式提交数据 text_plain格式
     *
     * @param url
     * @param paramJo
     * @param headerJo
     * @return
     * @throws IOException
     */
    public static String sendHttpPostRequestInStream(String url, JSONObject paramJo, JSONObject headerJo) {
        @SuppressWarnings("resource")
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
        String body = null;
        try {
            StringEntity e = new StringEntity(paramJo.toString(), HTTP.UTF_8);
            HttpPost httpost = new HttpPost(url);
            httpost.setEntity(e);
            if (headerJo != null) {
                Iterator<String> headIt = headerJo.keySet().iterator();
                String headKey;
                while (headIt.hasNext()) {
                    headKey = headIt.next();
                    httpost.addHeader(headKey, headerJo.getString(headKey));
                }
            }
            httpost.setEntity(e);
            HttpResponse response = httpclient.execute(httpost);
            if (response == null) {
                return null;
            }
            body = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("sendHttpPostRequestInStream:" + e.getMessage());
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return body;

    }


    /**
     * 发送HttpPost请求
     *
     * @param url
     * @param
     * @param
     * @return
     */
    public static String sendHttpPostRequest(String url, JSONObject paramJo, JSONObject headerJo) {
        @SuppressWarnings("resource")
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
        String body = null;
        List<NameValuePair> params2 = new ArrayList<NameValuePair>();
        try {
            Iterator<String> it = paramJo.keySet().iterator();
            String paramKey;
            while (it.hasNext()) {
                paramKey = it.next();
                params2.add(new BasicNameValuePair(paramKey, paramJo.getString(paramKey)));
            }
            StringEntity e = new UrlEncodedFormEntity(params2, HTTP.UTF_8);
            e.setContentType("application/x-www-form-urlencoded");
            HttpPost httpost = new HttpPost(url);
            if (headerJo != null) {
                Iterator<String> headIt = headerJo.keySet().iterator();
                String headKey;
                while (headIt.hasNext()) {
                    headKey = headIt.next();
                    httpost.addHeader(headKey, headerJo.getString(headKey));
                }
            }

            httpost.setEntity(e);
            HttpResponse response = httpclient.execute(httpost);
            if (response == null) {
                return null;
            }
            body = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("sendHttpPostRequest:" + e.getMessage());
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return body;
    }

    /**
     * 发送HttpPost json请求
     *
     * @param url
     * @param
     * @param
     * @return
     */
    public static String sendHttpPostJsonRequest(String url, String bodyEntity, JSONObject headerJo) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
        String body = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            StringEntity e = new StringEntity(bodyEntity, ContentType.APPLICATION_JSON.getMimeType(), HTTP.UTF_8);
            httpPost.setEntity(e);
            if (headerJo != null) {
                Iterator<String> headIt = headerJo.keySet().iterator();
                String headKey;
                while (headIt.hasNext()) {
                    headKey = headIt.next();
                    httpPost.addHeader(headKey, headerJo.getString(headKey));
                }
            }
            HttpResponse response = httpclient.execute(httpPost);
            if (response == null) {
                return null;
            }
            body = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("sendHttpPostJsonRequest:" + e.getMessage());
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return body;
    }

    /**
     * Content-Type为application/octet-stream文件请求
     *
     * @param url
     * @param headerJo
     * @param file
     * @return
     */
    public static String sendHttpPostOctetFileRequest(String url, Map<String, String> headerJo, File file) {
        String result = "";
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
            result = HttpUtil.sendHttpPostOctetStreamRequest(url, headerJo, buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Content-Type为application/octet-stream流请求
     *
     * @param url
     * @param headerJo
     * @param bytes
     * @return
     */
    public static String sendHttpPostOctetStreamRequest(String url, Map<String, String> headerJo, byte[] bytes) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
        String body = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/octet-stream");
            HttpEntity he = new InputStreamEntity(new ByteArrayInputStream(bytes, 0, bytes.length), bytes.length, ContentType.APPLICATION_OCTET_STREAM);
            httpPost.setEntity(he);
            if (headerJo != null) {
                Iterator<String> headIt = headerJo.keySet().iterator();
                String headKey;
                while (headIt.hasNext()) {
                    headKey = headIt.next();
                    httpPost.addHeader(headKey, headerJo.get(headKey));
                }
            }
            HttpResponse response = httpclient.execute(httpPost);
            if (response == null) {
                return null;
            }
            body = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("sendHttpPostOctetStreamRequest:" + e.getMessage());
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return body;
    }

    /**
     * 发送multipart_form_data形式HttpPost文件请求
     *
     * @param url      url地址
     * @param paramJo  请求参数
     * @param headerJo 请求头参数
     * @param fileMap  文件参数（单个文件的key通常为file，多个文件需要设置不同key，value为File格式）
     * @return
     */
    public static String sendHttpPostFileRequest(String url, Map<String, String> paramJo, Map<String, String> headerJo, Map<String, File> fileMap) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
        String body = null;
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setContentType(ContentType.MULTIPART_FORM_DATA);
            builder.setCharset(Charset.forName("UTF-8"));
            Iterator<String> it = paramJo.keySet().iterator();
            while (it.hasNext()) {
                String paramKey = it.next();
                builder.addTextBody(paramKey, paramJo.get(paramKey), ContentType.MULTIPART_FORM_DATA);
            }
            // 把文件加到HTTP的post请求中
            Iterator<String> fileIt = fileMap.keySet().iterator();
            while (fileIt.hasNext()) {
                String file = fileIt.next();
                File f = fileMap.get(file);
                builder.addBinaryBody(file, f, ContentType.MULTIPART_FORM_DATA, f.getName());
            }
            //提交请求
            HttpPost httpost = new HttpPost(url);
            if (headerJo != null) {
                Iterator<String> headIt = headerJo.keySet().iterator();
                String headKey;
                while (headIt.hasNext()) {
                    headKey = headIt.next();
                    httpost.addHeader(headKey, headerJo.get(headKey));
                }
            }
            httpost.setEntity(builder.build());
            HttpResponse response = httpclient.execute(httpost);
            if (response == null) {
                return null;
            }
            body = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("sendHttpPostFileRequest:" + e.getMessage());
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return body;
    }

    /**
     * 发送multipart_form_data形式HttpPost流请求
     *
     * @param url      url地址
     * @param paramJo  请求参数
     * @param headerJo 请求头参数
     * @param fileMap  文件参数（单个文件的key通常为file，多个文件需要设置不同key，value为byte[]格式）
     * @return
     */
    public static String sendHttpPostStreamRequest(String url, Map<String, String> paramJo, Map<String, String> headerJo, Map<String, byte[]> fileMap) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
        String body = null;
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setContentType(ContentType.MULTIPART_FORM_DATA);
            builder.setCharset(Charset.forName("UTF-8"));
            Iterator<String> it = paramJo.keySet().iterator();
            while (it.hasNext()) {
                String paramKey = it.next();
                builder.addTextBody(paramKey, paramJo.get(paramKey), ContentType.MULTIPART_FORM_DATA);
            }
            // 把文件加到HTTP的post请求中
            Iterator<String> fileIt = fileMap.keySet().iterator();
            while (fileIt.hasNext()) {
                String file = fileIt.next();
                byte[] f = fileMap.get(file);
                builder.addBinaryBody(file, f, ContentType.MULTIPART_FORM_DATA, UUID.randomUUID().toString().replace("-", ""));
            }
            //提交请求
            HttpPost httpost = new HttpPost(url);
            if (headerJo != null) {
                Iterator<String> headIt = headerJo.keySet().iterator();
                String headKey;
                while (headIt.hasNext()) {
                    headKey = headIt.next();
                    httpost.addHeader(headKey, headerJo.get(headKey));
                }
            }
            httpost.setEntity(builder.build());
            HttpResponse response = httpclient.execute(httpost);
            if (response == null) {
                return null;
            }
            body = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("sendHttpPostStreamRequest:" + e.getMessage());
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return body;
    }

    public static String sendHttpsGetRequest(String url) {
        String result = "";
        try {
            HttpClient httpClient = new SSLClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
            HttpPost httpGet = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "utf-8");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送http get请求
     * 第二个参数定位header,当然也可以不传这个参数
     *
     * @param url
     * @return
     */
    public static String sendHttpGetRequest(String url, JSONObject... params) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            connection.setReadTimeout(60000);
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            if (!StringUtils.isEmpty(params) && params.length > 0) {
                JSONObject header = params[0];
                if (header != null) {
                    Iterator<String> headIt = header.keySet().iterator();
                    String headKey;
                    while (headIt.hasNext()) {
                        headKey = headIt.next();
                        connection.setRequestProperty(headKey, header.getString(headKey));
                    }
                }
            }
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                logger.info(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.info("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 根据URL获取二进制数据，一般用来下载文件
     *
     * @param strUrl 下载的url
     * @return
     */
    public static byte[] getByteArrByUrl(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(60 * 1000);
            InputStream inStream = conn.getInputStream();//通过 输入流获取图片数据
            byte[] btImg = readInputStream(inStream);//得到图片的二进制数据
            inStream.close();
            return btImg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从输入流获取数据（接收）
     *
     * @param inStream 输入流
     * @return
     * @throws Exception
     */
    private static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    /**
     * 从输入流保存至目录文件（接收）
     *
     * @param inStream 输入流
     * @param path     文件目录（包含文件名）
     * @return
     */
    public static boolean saveFileInputStream(InputStream inStream, String path) {
        Path dest = Paths.get(path);
        try {
            FileCopyUtils.copy(inStream, Files.newOutputStream(dest));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将json格式的数据的每个值，encode！主要用于防止请求头乱码
     *
     * @param jo
     * @return
     */
    public static JSONObject JSONUrlEncode(JSONObject jo) {
        if (jo == null) {
            return null;
        }
        Iterator<String> keyIt = jo.keySet().iterator();
        JSONObject newHeaderJo = new JSONObject();
        while (keyIt.hasNext()) {
            String key = keyIt.next();
            try {
                newHeaderJo.put(key, URLEncoder.encode(jo.getString(key), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return newHeaderJo;
    }

    /**
     * 上传文件到服务器，服务器以FilePart形式接收；
     *
     * @param url
     * @param imgFile
     * @return
     */
    public static String uploadFileInMultiPartRequest(String url, File imgFile) {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
        try {
            HttpPost httppost = new HttpPost(url);
            FileBody bin = new FileBody(imgFile);
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("file", bin);//file1为请求后台的File upload;属性
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                return EntityUtils.toString(resEntity);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
