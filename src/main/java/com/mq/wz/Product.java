package com.mq.wz;

import com.mq.wz.util.RabbitConstant;
import com.mq.wz.util.RabbitMqPool;
import com.rabbitmq.client.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author wazh
 * @since 2023-11-28-16:45
 */
public class Product {
    public void doubleWriteForSame(Object message) {
        RabbitMqPool rabbitMqPool = new RabbitMqPool();
        Connection conn = rabbitMqPool.getConn();
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        ) {
            Channel channel = conn.createChannel();
            channel.exchangeDeclare(RabbitConstant.NORMAL_EXCHANGE, RabbitConstant.EXCHANGE_TYPE_TOPIC);  //普通交换机声明
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().expiration("10000").build();
            channel.basicPublish(RabbitConstant.NORMAL_EXCHANGE, RabbitConstant.NORMAL_EXCHANGE_ROUTINE_KEY, basicProperties, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void messageForRedis(Object message) {
        RabbitMqPool rabbitMqPool = new RabbitMqPool();
        Connection conn = rabbitMqPool.getConn();
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
        ) {
            Channel channel = conn.createChannel();
            channel.exchangeDeclare(RabbitConstant.BACK_REDIS_EXCHANGE, RabbitConstant.EXCHANGE_TYPE_TOPIC);// cache任务交换机
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            channel.basicPublish(RabbitConstant.BACK_REDIS_EXCHANGE, RabbitConstant.W_BACK_REDIS_ROUTINE_KEY, MessageProperties.MINIMAL_BASIC, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
