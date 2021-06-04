package com.acrabsoft.web.service.sjt.pc.operation.web.manager.constant;

/**
 * 系统中的一些常量，放在这里
 */
public class Constants {

    /**
     * 所有数据的删除状态
     */
    public static class DELETED_STATE {
        public static final String NOT_DELETED = "0";
        public static final String IS_DELETED = "1";
    }

    //积分相关obj
    public static final String JF_ADD_NUM_LX = "addjf30addusernum";
    public static final String JF_ADD_USER_NUM_LX = "30addusernum";

    public static final String SUPER_DEFAULT_JF_OBJ = "superdefaultjf";
    public static final String HIGH_DEFAULT_JF_OBJ = "highdefaultjf";
    public static final String MIDDLE_DEFAULT_JF_OBJ = "middledefaultjf";
    public static final String LOW_DEFAULT_JF_OBJ = "lowdefaultjf";
    public static final String WEEK_OVERUSE_JF_OBJ = "weekoverusejf";
    public static final String MONTH_OVERUSE_JF_OBJ = "monthoverusejf";

    public static final String HEADER_AUTHOR = "Authorization";

    public static class DEFAULT_VALUES {
        public static final long QUERY_DATA_CACHE_DEFAULT_CACHE_TIME = 600; // 默认需要缓存就缓存600S
    }

}
