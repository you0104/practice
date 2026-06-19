package com.ibm.eastb.harumi.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.ibm.eastb.harumi.dao.CoffeeDAO;
import com.ibm.eastb.harumi.dto.Coffee;
import com.ibm.eastb.harumi.dto.Employee;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * コーヒー管理サーブレット。<br>
 * タンク内コーヒーの「追加（補充）」と「削減（オペレーションミスによる損失）」を行う。
 * 変更後の残量を新しい行としてCoffeeテーブルへINSERTする。
 *
 * @author harumi development team
 * @version 1.00
 */
@WebServlet("/coffee")
public class CoffeeServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!ensureLogin(request, response)) {
			return;
		}
		try {
			Coffee coffee = new CoffeeDAO().findLatest();
			request.setAttribute("coffee", coffee);
			forward(request, response, "coffee.jsp");
		} catch (SQLException e) {
			throw new ServletException("コーヒー残量の取得でデータベースエラーが発生しました。", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!ensureLogin(request, response)) {
			return;
		}
		request.setCharacterEncoding("UTF-8");
		Employee user = loginUser(request);

		String operation = request.getParameter("operation"); // "add" or "reduce"
		int amount = parseAmount(request.getParameter("amount"));

		try {
			CoffeeDAO dao = new CoffeeDAO();
			Coffee coffee = dao.findLatest();
			int current = (coffee != null) ? coffee.getCurrentCapacity() : 0;

			if (amount <= 0) {
				request.setAttribute("errorMsg", "1以上の数量を入力してください。");
				request.setAttribute("coffee", coffee);
				forward(request, response, "coffee.jsp");
				return;
			}

			int change;
			int newCapacity;
			if ("reduce".equals(operation)) {
				// 削減：残量を超える削減はできない
				if (amount > current) {
					request.setAttribute("errorMsg",
							"削減量が現在の残量(" + current + "ml)を超えています。");
					request.setAttribute("coffee", coffee);
					forward(request, response, "coffee.jsp");
					return;
				}
				change = -amount;
				newCapacity = current - amount;
			} else {
				// 追加（補充）
				change = amount;
				newCapacity = current + amount;
			}

			dao.insert(newCapacity, user.getEmpno(), change);

			Coffee updated = dao.findLatest();
			request.setAttribute("coffee", updated);
			request.setAttribute("infoMsg",
					("reduce".equals(operation) ? "削減" : "補充") + "しました。（" + Math.abs(change)
							+ "ml）現在の残量：" + newCapacity + "ml");
			forward(request, response, "coffee.jsp");
		} catch (SQLException e) {
			throw new ServletException("コーヒー残量の更新でデータベースエラーが発生しました。", e);
		}
	}

	/**
	 * 数量パラメータを整数に変換する。未入力・不正値は0として扱う。
	 *
	 * @param value 数量の文字列
	 * @return 0以上の数量
	 */
	private int parseAmount(String value) {
		if (value == null || value.isBlank()) {
			return 0;
		}
		try {
			return Math.max(Integer.parseInt(value.trim()), 0);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
