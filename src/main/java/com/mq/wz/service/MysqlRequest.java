package com.mq.wz.service;

import com.mq.wz.bean.Category;
import com.mq.wz.util.MysqlConnectionUtil;

import java.sql.*;

/**
 * @author wazh
 * @since 2023-11-29-14:11
 */
public class MysqlRequest {

    public static Object putRequest() {
        String sql = "SELECT * FROM `wz_category` WHERE id = 1;";
        Object requestMysql = requestMysql(sql);
        if (requestMysql != null) {
            return sql;
        }
        return null;
    }

    public static Object requestMysql(String sql) {
        Connection connection = MysqlConnectionUtil.getConnect();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Category category = new Category();
                category.setName(name);
                category.setDescription(description);
                return category;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
