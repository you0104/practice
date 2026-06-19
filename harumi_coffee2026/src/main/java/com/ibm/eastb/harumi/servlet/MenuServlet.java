package com.ibm.eastb.harumi.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.ibm.eastb.harumi.dao.CoffeeDAO;
import com.ibm.eastb.harumi.dto.Coffee;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * メインメニューサーブレット。<br>
 * ログイン後のメニュー画面を表示する。現在のコーヒー残量も表示する。
 *
 * @author harumi development team
 * @version 1.00
 */
@WebServlet("/menu")
public class MenuServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!ensureLogin(request, response)) {
			return;
		}

		try {
			// 現在のコーヒー残量を表示用に取得する
			Coffee coffee = new CoffeeDAO().findLatest();
			request.setAttribute("coffee", coffee);
			forward(request, response, "menu.jsp");
		} catch (SQLException e) {
			throw new ServletException("コーヒー残量の取得でデータベースエラーが発生しました。", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
