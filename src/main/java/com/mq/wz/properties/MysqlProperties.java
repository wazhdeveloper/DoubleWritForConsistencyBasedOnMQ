package com.mq.wz.properties;

import com.mq.wz.util.MysqlConnectionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author wazh
 * @since 2023-11-29-13:57
 */
@Deprecated
public class MysqlProperties {
    private static String username;
    private static String password;
    private static String url;
    private static String driver;

    static {
        InputStream inputStream = MysqlConnectionUtil.class.getClassLoader().getResourceAsStream("mysql.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        username = (String) properties.get("username");
        password = (String) properties.get("password");
        url = (String) properties.get("url");
        driver = (String) properties.get("driver");
    }
}
