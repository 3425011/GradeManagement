package com.score.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * JDBC 工具类
 * 职责：读取 classpath 下的 db.properties 配置文件，提供数据库连接与资源关闭方法。
 */
public class DBUtil {

    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    // 静态代码块：类加载时执行一次，读取配置并加载驱动
    static {
        try {
            InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            Properties prop = new Properties();
            prop.load(in);
            driver = prop.getProperty("jdbc.driver");
            url = prop.getProperty("jdbc.url");
            username = prop.getProperty("jdbc.username");
            password = prop.getProperty("jdbc.password");
            Class.forName(driver); // 加载 JDBC 驱动
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("读取 db.properties 或加载数据库驱动失败", e);
        }
    }

    /** 获取数据库连接 */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取数据库连接失败", e);
        }
    }

    /** 关闭资源（用于查询操作：ResultSet + Statement + Connection） */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /** 关闭资源（用于增删改操作：Statement + Connection） */
    public static void close(Statement stmt, Connection conn) {
        close(null, stmt, conn);
    }
}
