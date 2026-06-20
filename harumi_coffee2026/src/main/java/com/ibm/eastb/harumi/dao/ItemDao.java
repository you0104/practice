package com.ibm.eastb.harumi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ibm.eastb.harumi.dto.Item;

/**
 * 商品DAO。
 * Itemテーブルへのアクセスを担当する。
 *
 * @author harumi development team
 * @version 1.00
 */
public class ItemDao {

	/**
	 * 販売中（削除フラグ=0）の商品一覧を商品番号順で取得する。<br>
	 * 商品が追加された場合もプログラム修正なくこのメソッドで反映される。
	 *
	 * @return 販売中商品のリスト
	 * @throws SQLException DBアクセスに失敗した場合
	 */
	public static List<Item> findOnSale() throws SQLException {
		String sql = "SELECT itemNo, name, price, coffeeAmount, isDeleted, categoryId "
				+ "FROM item "
				+ "WHERE isDeleted = 0 "
				+ "ORDER BY itemNo ASC";

		List<Item> list = new ArrayList<>();
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Item item = new Item();
				item.setItemNo(rs.getInt("itemNo"));
				item.setName(rs.getString("name"));
				item.setPrice(rs.getInt("price"));
				item.setCoffeeAmount(rs.getInt("coffeeAmount"));
				item.setIsDeleted(rs.getInt("isDeleted"));
				item.setCategoryId(rs.getInt("categoryId"));
				list.add(item);
			}
		}
		return list;
	}
}
