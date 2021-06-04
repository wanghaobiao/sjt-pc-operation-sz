package com.acrabsoft.web.service.sjt.pc.operation.web.util;


import com.google.common.collect.ImmutableMap;

import java.util.*;

/**
 * @Title: 对应数据字典的枚举类
 * @author wanghb
 * @date 2019-07-18
 */
public interface ParamEnum {

    /**
     * 字典项type
     * @author wanghb
     */
    enum dicType {
        simLimit( "simLimit","SIM卡限额"),
        deviceLimit( "deviceLimit","终端限额"),
        device( "device","终端品牌"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        dicType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (dicType item : dicType.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (dicType item : dicType.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }


    /**
     * 逻辑删除字段
     * @author wanghb
     */
    enum deleted {
        noDel( "0","未删除"),
        yesDel( "1","已删除"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        deleted(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (deleted item : deleted.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (deleted item : deleted.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }


    /**
     * 逻辑删除字段
     * @author wanghb
     */
    enum rowkeyType {
        type1( "city","地市"),
        type2( "imei","imei"),
        type3( "app","应用"),
        type4( "service","服务"),
        type5( "load","负载"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        rowkeyType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (rowkeyType item : rowkeyType.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (rowkeyType item : rowkeyType.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }


    /**
     * 逻辑删除字段
     * @author wanghb
     */
    enum taskRecord {
        appOpenCount( "appOpenCount","启动次数统计"),
        serviceOpenCount( "serviceOpenCount","启动次数统计"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        taskRecord(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (taskRecord item : taskRecord.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (taskRecord item : taskRecord.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

    /**
     * 逻辑删除字段
     * @author wanghb
     */
    enum taskRecordStatus {
        status0( "0","待执行"),
        status1( "1","执行中"),
        status2( "2","执行完毕"),
        status3( "3","执行失败"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        taskRecordStatus(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (taskRecordStatus item : taskRecordStatus.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (taskRecordStatus item : taskRecordStatus.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

    /**
     * 逻辑删除字段
     * @author wanghb
     */
    enum sequenceType {
        app( "app","应用类型"),
        service( "service","服务类型"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        sequenceType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (sequenceType item : sequenceType.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (sequenceType item : sequenceType.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

}

