package com.harumi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * コーヒーテーブル(Coffee) へのアクセス。
 * 残量は履歴方式なので、最新は MAX(updateNo) の行を読む／更新はINSERTで追記する。
 */
public class CoffeeDao {

    /** 現在のタンク残量(ml)を取得。1行も無ければ0を返す。 */
    public int selectLatestCapacity() throws SQLException {
        String sql = "SELECT currentCapacity FROM Coffee "
                   + "WHERE updateNo = (SELECT MAX(updateNo) FROM Coffee)";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("currentCapacity");
            }
        }
        return 0;
    }

    /**
     * 残量変更を追記する（支払い確定時に「最新残量 − 使用量」を新規INSERT）。
     * 同一トランザクションで売上登録と一緒にコミットしたい場合は、
     * Connection を引数で受け取る版を別途用意して使い回すこと。
     */
    public void insertCapacity(int capacity, String empno) throws SQLException {
        String sql = "INSERT INTO Coffee (currentCapacity, empno) VALUES (?, ?)";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, capacity);
            ps.setString(2, empno);
            ps.executeUpdate();
        }
    }
}
