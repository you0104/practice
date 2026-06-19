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
public class SalesDAO {

	/**
	 * 注文情報を登録する。<br>
	 * Sales（注文）とSalesDetail（注文明細）の両方に追加する。
	 * 1つのトランザクションで実行し、途中で失敗した場合はロールバックする。
	 *
	 * @param order 登録する注文情報
	 * @throws SQLException DBアクセスに失敗した場合
	 */
	public void insertSale(Order order) throws SQLException {
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
	 * 注文情報を登録する（提供資料のSQLに準拠した別実装）。<br>
	 * {@link #insertSale(Order)} と同じく Sales と SalesDetail に登録するが、
	 * 自動採番された注文番号(salesNo)の取得方法が異なる。<br>
	 * この実装では JDBC の getGeneratedKeys を使わず、<br>
	 * <pre>
	 *   INSERT INTO SalesDetail (...)
	 *   SELECT MAX(salesNo), ?, ?, ?, ? FROM sales
	 * </pre>
	 * のように、SalesへINSERTした直後の「最大の注文番号(=最新の注文)」を
	 * SELECTし直して明細に結び付ける。<br>
	 * <br>
	 * 注意：MAX(salesNo)で最新番号を取得するため、同一コネクション・同一
	 * トランザクション内で実行しないと、他の注文の番号を拾う恐れがある。
	 * ここでは1つのトランザクション(setAutoCommit(false))にまとめ、
	 * 直前にINSERTしたSales行を確実に参照できるようにしている。<br>
	 * 同時実行の安全性は {@link #insertSale(Order)}（getGeneratedKeys方式）の方が高い。
	 *
	 * @param order 登録する注文情報
	 * @throws SQLException DBアクセスに失敗した場合
	 */
	public void insertSaleByMaxNo(Order order) throws SQLException {
		// 注文(ヘッダ)をまず登録する
		String insertSales = "INSERT INTO sales (empno, subTotal, tax, total, payment) VALUES (?, ?, ?, ?, ?)";
		// 直前に登録した注文の番号(MAX)を取り直して、明細に結び付ける
		String insertDetail = "INSERT INTO salesDetail (salesNo, itemNo, price, quantity, coffeeAmount) "
				+ "SELECT MAX(salesNo), ?, ?, ?, ? FROM sales";

		Connection con = null;
		try {
			con = ConnectionManager.getConnection();
			con.setAutoCommit(false);

			// (1) 注文テーブルにまず追加する
			try (PreparedStatement ps = con.prepareStatement(insertSales)) {
				ps.setString(1, order.getEmpno());
				ps.setInt(2, order.getSubtotal());
				ps.setInt(3, order.getTax());
				ps.setInt(4, order.getTotal());
				ps.setInt(5, order.getPayment());
				ps.executeUpdate();
			}

			// (2) 各商品の明細を追加する。salesNoはSELECT MAX(salesNo)で取得する
			//     （個数が0の商品は登録しない）
			try (PreparedStatement ps = con.prepareStatement(insertDetail)) {
				for (OrderDetail d : order.getOrderedDetails()) {
					ps.setInt(1, d.getItemNo());
					ps.setInt(2, d.getPrice());
					ps.setInt(3, d.getQuantity());
					ps.setInt(4, d.getCoffeeAmount());
					ps.executeUpdate();
				}
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
	public int findTodayTotal() throws SQLException {
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
	public int findTodayUsedCoffee() throws SQLException {
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
	public List<ItemSalesSummary> findTodayItemSales() throws SQLException {
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
