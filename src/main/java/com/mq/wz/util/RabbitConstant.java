package com.mq.wz.util;

/**
 * @author wazh
 * @since 2023-11-28-19:47
 */
public class RabbitConstant {
    public static final String EXCHANGE_TYPE_TOPIC = "topic";
    public static final String EXCHANGE_TYPE_DEFAULT = "";
    public static final String EXCHANGE_TYPE_DIRECT = "direct";
    public static final String EXCHANGE_TYPE_FANOUT = "fanout";
    public static final String DEAD_EXCHANGE = "dead_exchange";
    public static final String DEAD_QUEUE = "dead_queue";
    public static final String NORMAL_EXCHANGE_ROUTINE_KEY = "normal_exchange";
    public static final String NORMAL_QUEUE = "DoubleWriteForSame";
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    public static final String DEAD_EXCHANGE_ROUTINE_KEY = "dead_e";
    public static final String W_BACK_REDIS_ROUTINE_KEY = "w_back_redis";
    public static final String BACK_REDIS_EXCHANGE = "back_redis";
    public static final String BACK_REDIS_QUEUE = "back_redis_queue";
    public static final String MESSAGE_TO_REDIS = "";
    public static final String MESSAGE_FOR_MYSQL = "mysql_insert";
    public static final String MESSAGE_FOR_MYSQL_SELECT = "mysql_select";
}

