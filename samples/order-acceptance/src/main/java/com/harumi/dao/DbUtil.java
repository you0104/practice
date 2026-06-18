package com.harumi.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DB接続のユーティリティ（学習用に DriverManager で簡易実装）。
 * 実務では DataSource(JNDI) + コネクションプールを推奨。
 * 接続情報は環境に合わせて書き換えてください。
 */
public class DbUtil {

    private static final String URL  = "jdbc:db2://localhost:50000/harumi";
    private static final String USER = "db2inst1";
    private static final String PASS = "password";

    static {
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("DB2 JDBCドライバが見つかりません: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
