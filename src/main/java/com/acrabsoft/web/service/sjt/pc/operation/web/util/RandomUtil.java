package com.acrabsoft.web.service.sjt.pc.operation.web.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class RandomUtil {
    public static String[] range0 = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static String[] range1 = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};


    /**
     * @description  随机生成4个字符
     * 三个字符  共有 46656种组合
     * 四个字符  共有 1679616种组合
     * @param  Len
     * @return  返回结果
     * @date  2021-4-9 16:07
     * @author  wanghb
     * @edit
     */
    public static String getRandomString(String[] range, int Len) {
        Random random = new Random();
        int length= range.length;
        String randomString="";
        for(int i=0;i<length;i++){
            randomString += range[random.nextInt(length)];
        }
        random = new Random(System.currentTimeMillis());
        String resultStr="";
        for (int i = 0; i < Len; i++) {
            resultStr += randomString.charAt(random.nextInt(randomString.length()-1));
        }
        return resultStr;
    }

    /**
     * @description  生成一定范围内的随机数
     * @param  start 开始
     * @param  end  结束
     * @return  返回结果
     * @date  2021-4-20 15:32
     * @author  wanghb
     * @edit
     */
    public static Integer getRandom(Integer start,Integer end) {
        Random random = new Random();
        int i = random.nextInt(end - start + 1) + start;
        return i;
    }

}
