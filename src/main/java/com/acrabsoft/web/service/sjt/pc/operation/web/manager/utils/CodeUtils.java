package com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @author fangzm@xifuang.com 2018年3月2日 16:45:15
 */
public class CodeUtils {

    private static final String SOURCE_STRING = "qwertyuiopasdfghjklzxcvbnm7894561230QWERTYUIOPASDFGHJKLZXCVBNM1234567890";
    private static final Random RANDOM = new Random();

    /**
     * 获取32位全大写uuid
     *
     * @return
     */
    public static String getUUID32() {
        String uuid32 = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid32.toUpperCase();
    }

    /**
     * 获取随机code字符串
     *
     * @param codeLength 目标code字符串长度
     * @return
     */
    public static String getRandomCode(int codeLength) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            builder.append(SOURCE_STRING.charAt(RANDOM.nextInt(72)));
        }

        return builder.toString();
    }
}
