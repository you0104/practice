package com.harumi.dao;

import com.harumi.model.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品テーブル(Item) へのアクセス。
 */
public class ItemDao {

    /** 販売可能な商品（削除フラグ0）を一覧取得。注文受付画面のボタン表示に使う。 */
    public List<Item> findAllOnSale() throws SQLException {
        String sql = "SELECT itemNo, name, price, coffeeAmount "
                   + "FROM Item WHERE isDeleted = 0 ORDER BY itemNo";
        List<Item> list = new ArrayList<>();
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Item(
                        rs.getInt("itemNo"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getInt("coffeeAmount")));
            }
        }
        return list;
    }

    /** 商品番号で1件取得（コーヒー押下時に押された商品を引く）。 */
    public Item findById(int itemNo) throws SQLException {
        String sql = "SELECT itemNo, name, price, coffeeAmount FROM Item WHERE itemNo = ?";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, itemNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Item(
                            rs.getInt("itemNo"),
                            rs.getString("name"),
                            rs.getInt("price"),
                            rs.getInt("coffeeAmount"));
                }
            }
        }
        return null;
    }
}
