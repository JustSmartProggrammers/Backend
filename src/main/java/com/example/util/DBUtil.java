package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://rds-disaster-db-mysql.clsgwqwc6cx5.ap-northeast-2.rds.amazonaws.com:3306/disasterDB?useUnicode=true&characterEncoding=utf8";
    private static final String USER = "admin";
    private static final String PASSWORD = "root1234";

    static {
        try {
            // MySQL JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL JDBC 드라이버를 로드할 수 없습니다.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
