package com.mq.wz.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/**
 * @author wazh
 * @since 2023-11-28-18:22
 */
public class RabbitMqPool {
    private int size;
    private final List<Connection> connPoll;
    public static ConnectionFactory connectionFactory;

    {
        connectionFactory = new ConnectionFactory();
        configConnectionFactory();
    }

    public RabbitMqPool(int size) {
        this.size = size;
        connPoll = new ArrayList<>(size);
        for (int i = 0; i <= size; i++) {
            try {
                Connection connection = connectionFactory.newConnection();
                connPoll.add(connection);
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public RabbitMqPool() {
        try {
            connPoll = new ArrayList<>();
            connPoll.add(connectionFactory.newConnection());
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConn() {
        for (Connection connection : connPoll) {
            if (connection.isOpen()) {
                return connection;
            }
        }
        int oldCapacity = size;
        expand(oldCapacity,  size << 1 + 1);
        return getConn();
    }

    public void expand(int oldCapacity, int newCapacity) {
        for (int i = 0; i < newCapacity - oldCapacity; i++) {
            try {
                connPoll.add(connectionFactory.newConnection());
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void configConnectionFactory() {
        Properties properties = new Properties();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("D:\\360MoveData\\Users\\王政\\Desktop\\项目\\MQ-DoubleWriteForSame\\src\\main\\resources\\rabbitmq.properties");
            properties.load(inputStream);
            String host = (String) properties.get("host");
            int port = Integer.parseInt((String) properties.get("port"));
            connectionFactory.setHost(host);
            connectionFactory.setPort(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
