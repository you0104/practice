package com.ibm.eastb.harumi.servlet;

import static com.ibm.eastb.harumi.common.WebConst.ATTR_COFFEE;
import static com.ibm.eastb.harumi.common.WebConst.ATTR_ERROR;
import static com.ibm.eastb.harumi.common.WebConst.ATTR_INFO;
import static com.ibm.eastb.harumi.common.WebConst.ERR_AMOUNT_REQUIRED;
import static com.ibm.eastb.harumi.common.WebConst.OP_REDUCE;
import static com.ibm.eastb.harumi.common.WebConst.PARAM_AMOUNT;
import static com.ibm.eastb.harumi.common.WebConst.PARAM_OPERATION;
import static com.ibm.eastb.harumi.common.WebConst.URL_COFFEE_JSP;

import java.io.IOException;
import java.sql.SQLException;

import com.ibm.eastb.harumi.dao.CoffeeDao;
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
			Coffee coffee = CoffeeDao.findLatest();
			request.setAttribute(ATTR_COFFEE, coffee);
			forward(request, response, URL_COFFEE_JSP);
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

		String operation = request.getParameter(PARAM_OPERATION); // "add" or "reduce"
		int amount = parseAmount(request.getParameter(PARAM_AMOUNT));

		try {
			Coffee coffee = CoffeeDao.findLatest();
			int current = (coffee != null) ? coffee.getCurrentCapacity() : 0;

			if (amount <= 0) {
				request.setAttribute(ATTR_ERROR, ERR_AMOUNT_REQUIRED);
				request.setAttribute(ATTR_COFFEE, coffee);
				forward(request, response, URL_COFFEE_JSP);
				return;
			}

			int change;
			int newCapacity;
			if (OP_REDUCE.equals(operation)) {
				// 削減：残量を超える削減はできない
				if (amount > current) {
					request.setAttribute(ATTR_ERROR,
							"削減量が現在の残量(" + current + "ml)を超えています。");
					request.setAttribute(ATTR_COFFEE, coffee);
					forward(request, response, URL_COFFEE_JSP);
					return;
				}
				change = -amount;
				newCapacity = current - amount;
			} else {
				// 追加（補充）
				change = amount;
				newCapacity = current + amount;
			}

			CoffeeDao.insert(newCapacity, user.getEmpno(), change);

			Coffee updated = CoffeeDao.findLatest();
			request.setAttribute(ATTR_COFFEE, updated);
			request.setAttribute(ATTR_INFO,
					(OP_REDUCE.equals(operation) ? "削減" : "補充") + "しました。（" + Math.abs(change)
							+ "ml）現在の残量：" + newCapacity + "ml");
			forward(request, response, URL_COFFEE_JSP);
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
