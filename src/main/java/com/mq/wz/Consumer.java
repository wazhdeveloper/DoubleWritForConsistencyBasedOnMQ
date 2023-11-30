package com.mq.wz;

import com.mq.wz.service.MysqlRequest;
import com.mq.wz.service.RedisRequest;
import com.mq.wz.util.RabbitConstant;
import com.mq.wz.util.RabbitMqPool;
import com.rabbitmq.client.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wazh
 * @since 2023-11-28-17:12
 */
public class Consumer {
    public void redisConsumer() {
        RabbitMqPool rabbitMqPool = new RabbitMqPool();
        Connection conn = rabbitMqPool.getConn();
        try {
            Channel channel = conn.createChannel();
            channel.queueDeclare(RabbitConstant.BACK_REDIS_QUEUE, false, false, false, null);
            channel.queueBind(RabbitConstant.BACK_REDIS_QUEUE, RabbitConstant.BACK_REDIS_EXCHANGE, RabbitConstant.W_BACK_REDIS_ROUTINE_KEY);
            DeliverCallback deliverCallback = (s, delivery) -> {
                byte[] body = delivery.getBody();
                try (
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
                        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)
                ) {
                    Object obj = objectInputStream.readObject();

                    //执行缓存机制
                    RedisRequest.addCache(obj);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };

            Map<String, Object> var5 = new HashMap<>();
            var5.put("x-dead-letter-exchange", RabbitConstant.DEAD_EXCHANGE);//连接的死信交换机
            var5.put("x-message-ttl", 100000);//让生产者决定过期时间
            var5.put("x-dead-letter-routing-key", RabbitConstant.DEAD_EXCHANGE_ROUTINE_KEY);//连接死信交换机的routine_key
            var5.put("x-max-length", 10);//设置最大长度，当队列长度满，剩余的将会成为死信
            channel.basicConsume(RabbitConstant.BACK_REDIS_QUEUE, true, var5, deliverCallback, System.out::println);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void mysqlConsumer() {
        RabbitMqPool rabbitMqPool = new RabbitMqPool();
        Connection conn = rabbitMqPool.getConn();
        try {
            Channel channel = conn.createChannel();
            channel.queueDeclare(RabbitConstant.NORMAL_QUEUE, false, false, false, null);//普通队列声明
            channel.queueDeclare(RabbitConstant.DEAD_QUEUE, false, false, false, null); //死信队列声明
            channel.exchangeDeclare(RabbitConstant.DEAD_EXCHANGE, RabbitConstant.EXCHANGE_TYPE_TOPIC); //死信交换机声明
            channel.queueBind(RabbitConstant.DEAD_QUEUE, RabbitConstant.DEAD_EXCHANGE, RabbitConstant.DEAD_EXCHANGE_ROUTINE_KEY); //死信队列与死信交换机绑定
            channel.queueBind(RabbitConstant.NORMAL_QUEUE, RabbitConstant.NORMAL_EXCHANGE, RabbitConstant.NORMAL_EXCHANGE_ROUTINE_KEY);//普通队列与死信交换机绑定
            channel.queueBind(RabbitConstant.NORMAL_QUEUE, RabbitConstant.DEAD_EXCHANGE, RabbitConstant.DEAD_EXCHANGE_ROUTINE_KEY);//普通交换机与普通队列绑定

            DeliverCallback deliverCallback = (var, delivery) -> {
                byte[] body = delivery.getBody();
                String request = null;
                try (
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
                        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                ) {
                    Object object = objectInputStream.readObject();
                    request = object.toString();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(request);
                if (request.equals(RabbitConstant.MESSAGE_FOR_MYSQL_SELECT)) {
                    String sql = (String) MysqlRequest.putRequest();
                    if (sql == null) {
                        channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);// 失败则进入死信队列
                    } else {
                        Product product = new Product();
                        Object obj = null;
                        if (sql.toLowerCase().startsWith("select")) {
                            obj = MysqlRequest.requestMysql(sql);
                        }
                        product.messageForRedis(obj);
                    }
                }
            };
            Map<String, Object> var5 = new HashMap<>();
            var5.put("x-dead-letter-exchange", RabbitConstant.DEAD_EXCHANGE);//连接的死信交换机
            var5.put("x-message-ttl", 100000);//让生产者决定过期时间
            var5.put("x-dead-letter-routing-key", RabbitConstant.DEAD_EXCHANGE_ROUTINE_KEY);//连接死信交换机的routine_key
            var5.put("x-max-length", 10);//设置最大长度，当队列长度满，剩余的将会成为死信

            channel.basicConsume(RabbitConstant.NORMAL_QUEUE, true, var5, deliverCallback, System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
