package com.ibm.eastb.harumi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.eastb.harumi.dto.Employee;

/**
 * 従業員DAO。
 * Employeeテーブルへのアクセスを担当する。
 *
 * @author harumi development team
 * @version 1.00
 */
public class EmployeeDAO {

	/**
	 * ID・パスワードが一致する従業員（在職者）を取得する。<br>
	 * ユーザー認証に使用する。退職者(isRetire=1)はログインできない。
	 *
	 * @param empno    従業員番号
	 * @param password パスワード
	 * @return 一致した従業員。該当しない場合はnull
	 * @throws SQLException DBアクセスに失敗した場合
	 */
	public Employee findForAuth(String empno, String password) throws SQLException {
		String sql = "SELECT empno, name, password, isManager, isRetire, rollId "
				+ "FROM employee "
				+ "WHERE empno = ? AND password = ? AND isRetire = 0";

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, empno);
			ps.setString(2, password);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Employee emp = new Employee();
					emp.setEmpno(rs.getString("empno"));
					emp.setName(rs.getString("name"));
					emp.setPassword(rs.getString("password"));
					emp.setIsManager(rs.getInt("isManager"));
					emp.setIsRetire(rs.getInt("isRetire"));
					emp.setRollId(rs.getInt("rollId"));
					return emp;
				}
				return null;
			}
		}
	}
}
