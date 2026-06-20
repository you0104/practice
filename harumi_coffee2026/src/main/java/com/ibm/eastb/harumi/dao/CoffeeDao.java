package com.ibm.eastb.harumi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.eastb.harumi.dto.Coffee;

/**
 * コーヒー残量DAO。
 * Coffeeテーブルへのアクセスを担当する。<br>
 * 残量は変更のたびに新しい行をINSERTして履歴管理し、
 * 最新の更新番号(updateNo)の行が現在のタンク残量を表す。
 *
 * @author harumi development team
 * @version 1.00
 */
public class CoffeeDao {

	/**
	 * システムが保持している最新のコーヒー残量を取得する。<br>
	 * 最大の更新番号を持つ行を返す。
	 *
	 * @return 最新のコーヒー残量。1件もない場合はnull
	 * @throws SQLException DBアクセスに失敗した場合
	 */
	public static Coffee findLatest() throws SQLException {
		String sql = "SELECT updateNo, updateDate, currentCapacity, empno, changeAmount "
				+ "FROM coffee "
				+ "WHERE updateNo = (SELECT MAX(updateNo) FROM coffee)";

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				Coffee coffee = new Coffee();
				coffee.setUpdateNo(rs.getInt("updateNo"));
				coffee.setUpdateDate(rs.getDate("updateDate"));
				coffee.setCurrentCapacity(rs.getInt("currentCapacity"));
				coffee.setEmpno(rs.getString("empno"));
				int change = rs.getInt("changeAmount");
				coffee.setChangeAmount(rs.wasNull() ? null : change);
				return coffee;
			}
			return null;
		}
	}

	/**
	 * 新しいコーヒー残量を1行INSERTする。<br>
	 * 補充・削減・注文による減算・閉店時の破棄(0)など、残量変更すべてに使用する。
	 *
	 * @param currentCapacity 変更後のタンク残量(ml)
	 * @param empno           更新を行った従業員番号
	 * @param changeAmount    増減量(ml)（補充は正、削減・販売は負）
	 * @throws SQLException DBアクセスに失敗した場合
	 */
	public static void insert(int currentCapacity, String empno, int changeAmount) throws SQLException {
		String sql = "INSERT INTO coffee (currentCapacity, empno, changeAmount) VALUES (?, ?, ?)";

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, currentCapacity);
			ps.setString(2, empno);
			ps.setInt(3, changeAmount);
			ps.executeUpdate();
		}
	}
}
