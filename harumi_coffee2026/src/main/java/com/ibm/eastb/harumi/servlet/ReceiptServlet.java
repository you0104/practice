package com.ibm.eastb.harumi.servlet;

import static com.ibm.eastb.harumi.common.WebConst.ATTR_CHANGE_UNITS;
import static com.ibm.eastb.harumi.common.WebConst.ATTR_COFFEE;
import static com.ibm.eastb.harumi.common.WebConst.ATTR_ERROR;
import static com.ibm.eastb.harumi.common.WebConst.ATTR_ORDER;
import static com.ibm.eastb.harumi.common.WebConst.ERR_COFFEE_SHORT_CONFIRM;
import static com.ibm.eastb.harumi.common.WebConst.PARAM_PAYMENT;
import static com.ibm.eastb.harumi.common.WebConst.URL_ORDER_SERVLET;
import static com.ibm.eastb.harumi.common.WebConst.URL_PAYMENT_JSP;
import static com.ibm.eastb.harumi.common.WebConst.URL_RECEIPT_JSP;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ibm.eastb.harumi.dao.CoffeeDao;
import com.ibm.eastb.harumi.dao.SalesDao;
import com.ibm.eastb.harumi.dto.ChangeUnit;
import com.ibm.eastb.harumi.dto.Coffee;
import com.ibm.eastb.harumi.dto.Employee;
import com.ibm.eastb.harumi.dto.Order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * レシートサーブレット。<br>
 * 支払金額を受け取って注文情報に反映し、金額の妥当性を確認したうえで、
 * コーヒー残量の減算（Coffee）と注文情報（Sales/SalesDetail）をDBへ登録する。
 * つり銭を金種に分解してレシート画面へ遷移する。<br>
 * （コミュニケーション図の「レシートServlet」に対応）
 *
 * @author harumi development team
 * @version 1.00
 */
@WebServlet("/receipt")
public class ReceiptServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	/** つり銭の金種（大きい順） */
	private static final int[] UNITS = { 10000, 5000, 1000, 500, 100, 50, 10, 5, 1 };

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!ensureLogin(request, response)) {
			return;
		}
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();

		// (1)(2)(3) セッションから注文情報・コーヒー残量・従業員を取得する
		Order order = (Order) session.getAttribute(ATTR_ORDER);
		Coffee coffee = (Coffee) session.getAttribute(ATTR_COFFEE);
		Employee user = loginUser(request);
		if (order == null || coffee == null) {
			response.sendRedirect(URL_ORDER_SERVLET);
			return;
		}

		// (4) 注文情報に支払金額を設定する
		int payment = parsePayment(request.getParameter(PARAM_PAYMENT));
		order.setPayment(payment);

		// (5) 支払金額が不足していないか判定する
		if (payment < order.getTotal()) {
			request.setAttribute(ATTR_ERROR,
					"預かり金額が合計金額に不足しています。（合計 " + order.getTotal() + "円）");
			forward(request, response, URL_PAYMENT_JSP);
			return;
		}

		// コーヒー残量が不足していないか最終確認する（二重送信対策）
		if (order.getRequiredCoffee() > coffee.getCurrentCapacity()) {
			request.setAttribute(ATTR_ERROR, ERR_COFFEE_SHORT_CONFIRM);
			forward(request, response, URL_PAYMENT_JSP);
			return;
		}

		try {
			// (6) 今回の注文で減ったコーヒーをDBにINSERTする（Coffee）
			int newCapacity = coffee.getCurrentCapacity() - order.getRequiredCoffee();
			CoffeeDao.insert(newCapacity, user.getEmpno(), -order.getRequiredCoffee());

			// (7) 今回の注文情報をDBにINSERTする（Sales, SalesDetail）
			SalesDao.insertSale(order);

			// セッションのコーヒー残量を最新化する（連続注文に備える）
			Coffee updated = CoffeeDao.findLatest();
			session.setAttribute(ATTR_COFFEE, updated);

			// つり銭の金種を計算してレシート表示用に設定する
			request.setAttribute(ATTR_CHANGE_UNITS, calcChangeUnits(order.getChange()));

			forward(request, response, URL_RECEIPT_JSP);
		} catch (SQLException e) {
			throw new ServletException("注文確定（DB登録）でデータベースエラーが発生しました。", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 直接GETされた場合は注文受付からやり直す
		response.sendRedirect(URL_ORDER_SERVLET);
	}

	/**
	 * 支払金額パラメータを整数に変換する。未入力・不正値は0として扱う。
	 *
	 * @param value 支払金額の文字列
	 * @return 0以上の支払金額
	 */
	private int parsePayment(String value) {
		if (value == null || value.isBlank()) {
			return 0;
		}
		try {
			return Math.max(Integer.parseInt(value.trim()), 0);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * つり銭を金種ごとに分解する。
	 *
	 * @param change つり銭金額
	 * @return 枚数が1以上の金種リスト（金額の大きい順）
	 */
	private List<ChangeUnit> calcChangeUnits(int change) {
		List<ChangeUnit> list = new ArrayList<>();
		int remain = change;
		for (int unit : UNITS) {
			int count = remain / unit;
			if (count > 0) {
				list.add(new ChangeUnit(unit, count));
				remain -= unit * count;
			}
		}
		return list;
	}
}
