package com.mq.wz.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author wazh
 * @since 2023-11-28-21:29
 */
public class MysqlConnectionUtil {
    private final String username;
    private final String password;
    private final String url;
    private final String driver;

    private MysqlConnectionUtil() {
    }

    {
        InputStream inputStream = MysqlConnectionUtil.class.getClassLoader().getResourceAsStream("mysql.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.username = (String) properties.get("username");
        this.password = (String) properties.get("password");
        this.url = (String) properties.get("url");
        this.driver = (String) properties.get("driver");
    }

    public static Connection getConnect() {
        return new MysqlConnectionUtil().getConnection();
    }

    public static boolean close(Connection connection) {
        try {
            connection.close();
            return connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
