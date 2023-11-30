package com.mq.wz;

import com.mq.wz.util.RabbitConstant;

import java.util.concurrent.TimeUnit;

/**
 * @author wazh
 * @since 2023-11-29-14:26
 */
public class DoubleWriteApplication {
    public static void main(String[] args) {
        System.out.println("数据库插入指令，请求过来了");
        Product product = new Product();
        Consumer consumer = new Consumer();
        product.doubleWriteForSame(RabbitConstant.MESSAGE_FOR_MYSQL_SELECT);
        consumer.mysqlConsumer();
        try{ TimeUnit.SECONDS.sleep(1); } catch(Exception e) {e.printStackTrace();}
        consumer.redisConsumer();
    }
}
