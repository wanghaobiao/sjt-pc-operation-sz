package com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public static String md532(String source) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
        byte[] resultBytes = messageDigest.digest(sourceBytes);

        StringBuilder builder = new StringBuilder();
        for (byte b : resultBytes) {
            int val = b & 0xff;
            if (val < 16) {
                builder.append("0");
            }
            builder.append(Integer.toHexString(val));
        }
        return builder.toString();
    }

    public static String md532(byte[] source) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] resultBytes = messageDigest.digest(source);

        StringBuilder builder = new StringBuilder();
        for (byte b : resultBytes) {
            int val = b & 0xff;
            if (val < 16) {
                builder.append("0");
            }
            builder.append(Integer.toHexString(val));
        }
        return builder.toString();
    }

    public static String md516(String source) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
        byte[] resultBytes = messageDigest.digest(sourceBytes);

        StringBuilder builder = new StringBuilder();
        for (byte b : resultBytes) {
            builder.append(Integer.toHexString(b & 0x0f));
        }

        return builder.toString();
    }
}
