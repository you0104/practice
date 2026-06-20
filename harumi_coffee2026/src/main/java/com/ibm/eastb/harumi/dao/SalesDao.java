package com.ibm.eastb.harumi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ibm.eastb.harumi.dto.ItemSalesSummary;
import com.ibm.eastb.harumi.dto.Order;
import com.ibm.eastb.harumi.dto.OrderDetail;

/**
 * 売上DAO。
 * Sales・SalesDetailテーブルへのアクセスを担当する。
 *
 * @author harumi development team
 * @version 1.00
 */
public class SalesDao {

	/**
	 * 注文情報を登録する。<br>
	 * Sales（注文）とSalesDetail（注文明細）の両方に追加する。
	 * 1つのトランザクションで実行し、途中で失敗した場合はロールバックする。
	 *
	 * @param order 登録する注文情報
	 * @throws SQLException DBアクセスに失敗した場合
	 */
	public static void insertSale(Order order) throws SQLException {
		String insertSales = "INSERT INTO sales (empno, subTotal, tax, total, payment) VALUES (?, ?, ?, ?, ?)";
		String insertDetail = "INSERT INTO salesDetail (salesNo, itemNo, price, quantity, coffeeAmount) VALUES (?, ?, ?, ?, ?)";

		Connection con = null;
		try {
			con = ConnectionManager.getConnection();
			con.setAutoCommit(false);

			// 注文テーブルにまず追加し、自動採番された注文番号を取得する
			int salesNo;
			try (PreparedStatement ps = con.prepareStatement(insertSales, Statement.RETURN_GENERATED_KEYS)) {
				ps.setString(1, order.getEmpno());
				ps.setInt(2, order.getSubtotal());
				ps.setInt(3, order.getTax());
				ps.setInt(4, order.getTotal());
				ps.setInt(5, order.getPayment());
				ps.executeUpdate();
				try (ResultSet keys = ps.getGeneratedKeys()) {
					keys.next();
					salesNo = keys.getInt(1);
				}
			}

			// 注文明細を商品ごとに追加する（個数が0の商品は登録しない）
			try (PreparedStatement ps = con.prepareStatement(insertDetail)) {
				for (OrderDetail d : order.getOrderedDetails()) {
					ps.setInt(1, salesNo);
					ps.setInt(2, d.getItemNo());
					ps.setInt(3, d.getPrice());
					ps.setInt(4, d.getQuantity());
					ps.setInt(5, d.getCoffeeAmount());
					ps.addBatch();
				}
				ps.executeBatch();
			}

			con.commit();
		} catch (SQLException e) {
			if (con != null) {
				con.rollback();
			}
			throw e;
		} finally {
			if (con != null) {
				con.setAutoCommit(true);
				con.close();
			}
		}
	}

	/**
	 * 当日の総売上（税込合計の合計）を取得する。
	 *
	 * @return 当日の総売上。売上がない場合は0
	 * @throws SQLException DBアクセスに失敗した場合
	 */
	public static int findTodayTotal() throws SQLException {
		String sql = "SELECT SUM(total) AS todaysTotal FROM sales WHERE salesDate = CURRENT DATE";

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				return rs.getInt("todaysTotal");
			}
			return 0;
		}
	}

	/**
	 * 当日に注文で使用したコーヒー総量(ml)を取得する。<br>
	 * 閉店処理での「タンク総量（その日に注文で使用した全コーヒー量）」表示に使用する。
	 *
	 * @return 当日の使用コーヒー総量(ml)。使用がない場合は0
	 * @throws SQLException DBアクセスに失敗した場合
	 */
	public static int findTodayUsedCoffee() throws SQLException {
		String sql = "SELECT SUM(sd.coffeeAmount * sd.quantity) AS usedCoffee "
				+ "FROM sales s "
				+ "INNER JOIN salesDetail sd ON s.salesNo = sd.salesNo "
				+ "WHERE s.salesDate = CURRENT DATE";

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				return rs.getInt("usedCoffee");
			}
			return 0;
		}
	}

	/**
	 * 当日の商品別売上（商品ごとの販売個数と売上金額）を取得する。
	 *
	 * @return 商品別売上サマリのリスト（商品番号順）
	 * @throws SQLException DBアクセスに失敗した場合
	 */
	public static List<ItemSalesSummary> findTodayItemSales() throws SQLException {
		String sql = "SELECT i.itemNo, i.name, "
				+ "SUM(sd.quantity) AS itemTotalQuantity, "
				+ "SUM(i.price * sd.quantity) AS itemTotalSales "
				+ "FROM sales s "
				+ "INNER JOIN salesDetail sd ON s.salesNo = sd.salesNo "
				+ "INNER JOIN item i ON sd.itemNo = i.itemNo "
				+ "WHERE s.salesDate = CURRENT DATE "
				+ "GROUP BY i.itemNo, i.name "
				+ "ORDER BY i.itemNo ASC";

		List<ItemSalesSummary> list = new ArrayList<>();
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				ItemSalesSummary s = new ItemSalesSummary();
				s.setItemNo(rs.getInt("itemNo"));
				s.setName(rs.getString("name"));
				s.setTotalQuantity(rs.getInt("itemTotalQuantity"));
				s.setTotalSales(rs.getInt("itemTotalSales"));
				list.add(s);
			}
		}
		return list;
	}
}
