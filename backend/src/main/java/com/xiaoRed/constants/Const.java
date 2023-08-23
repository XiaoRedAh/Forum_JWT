package com.xiaoRed.constants;
/**
 * JWT常量类
 */
public class Const {

    //JWT相关常量
    public static final String JWT_SECRET = "asdadevvefe";
    public static final Long JWT_EXPIRATION = 24 * 60 * 60 * 1000L;
    public static final String JWT_BLACK_LIST = "jwt:blacklist:";

    //邮箱发送相关常量
    public static final String VERIFY_EMAIL_DATA = "verify:email:data:";
    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";

    //限流相关的常量
    public static final String FLOW_LIMIT_COUNTER = "flow:limit:counter";
    public static final String FLOW_LIMIT_BLOCK = "flow:limit:block";

    //过滤器优先级
    public final static int ORDER_FLOW_LIMIT = -101;
    public final static int ORDER_CORS = -102;
}
