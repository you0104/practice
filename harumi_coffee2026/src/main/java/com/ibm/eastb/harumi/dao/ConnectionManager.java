package com.ibm.eastb.harumi.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * コネクション・マネージャ
 * 
 * @author Tahara Yasunari
 * @version 1.00 
 */
public class ConnectionManager {
	
	/**DB2でロードすべきJDBCドライバーのクラス*/
	private static final String JDBC_DRIVER = "com.ibm.db2.jcc.DB2Driver";
	/*はるみDBの接続URL*/
	private static final String HARUMI_URL = "jdbc:db2://localhost:50000/harumi";
	/**販売DBの接続ユーザ*/
	private static final String USER = "db2admin";
	/**販売DBの接続パスワード*/
	private static final String PASS = "password";
		
	// staticイニシャライザでJDBCドライバをロードする
	static {
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println("JDBCドライバのロードに失敗しました");
			e.printStackTrace();
		}
	}
	
	/**
	 * DB接続の取得<br>
	 * 各DAOから呼び出されるメソッドでDriverManager#getConnectionをラップしている。<br>
	 * このメソッドの内部を書き換えることでコネクション・プールにも対応可能
	 * @return java.sql.Connection
	 * @throws SQLException DB接続出来なかった場合にスローされる
	 */
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(HARUMI_URL, USER, PASS);
	}
}
