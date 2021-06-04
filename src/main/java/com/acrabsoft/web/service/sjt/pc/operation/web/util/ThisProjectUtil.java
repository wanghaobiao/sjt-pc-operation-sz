package com.acrabsoft.web.service.sjt.pc.operation.web.util;

import io.swagger.annotations.ApiOperation;

import java.io.UnsupportedEncodingException;

public class ThisProjectUtil {

    /**
     * @description  字符格式化  超长用...表示
     * @param  str  要格式化的字符
     * @param  length  格式化长度
     * @return
     * @date  2020-11-18 20:28
     * @author  wanghb
     * @edit
     */
    public static String subForLength(String str, Integer length){
        if (length < 4 || str == null) {
            return "";
        }
        int stringLength = stringLength( str );
        if (stringLength > length) {
            str = byteSubstring( str,length - 4 )+ "...";
        }
        return str;
    }

    /**
     * 返回字符串的字符长度
     * @param value
     * @return
     */
    public static int stringLength(String value) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * @description  根据字符长度截取
     * @param  str
     * @param  length
     * @return
     * @date  2020-11-18 21:09
     * @author  wanghb
     * @edit
     */
    public static String byteSubstring(String str, int length){
        if (str == null) {
            return "";
        }
        byte[] bytes = new byte[0];
        try {
            bytes = str.getBytes("Unicode");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++)
        {
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1)
            {
                n++; // 在UCS2第二个字节时n加1
            }
            else
            {
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0)
                {
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1)

        {
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0) {
                i = i - 1;
                // 该UCS2字符是字母或数字，则保留该字符
            }else{
                i = i + 1;
            }
        }
        String unicode = "";
        try {
            unicode = new String( bytes, 0, i, "Unicode" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return unicode;
    }


}
