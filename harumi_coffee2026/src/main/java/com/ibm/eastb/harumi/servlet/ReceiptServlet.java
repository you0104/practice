package com.ibm.eastb.harumi.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ibm.eastb.harumi.dao.CoffeeDAO;
import com.ibm.eastb.harumi.dao.SalesDAO;
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
		Order order = (Order) session.getAttribute(ORDER);
		Coffee coffee = (Coffee) session.getAttribute(COFFEE);
		Employee user = loginUser(request);
		if (order == null || coffee == null) {
			response.sendRedirect(request.getContextPath() + "/order");
			return;
		}

		// (4) 注文情報に支払金額を設定する
		int payment = parsePayment(request.getParameter("payment"));
		order.setPayment(payment);

		// (5) 支払金額が不足していないか判定する
		if (payment < order.getTotal()) {
			request.setAttribute("errorMsg",
					"預かり金額が合計金額に不足しています。（合計 " + order.getTotal() + "円）");
			forward(request, response, "payment.jsp");
			return;
		}

		// コーヒー残量が不足していないか最終確認する（二重送信対策）
		if (order.getRequiredCoffee() > coffee.getCurrentCapacity()) {
			request.setAttribute("errorMsg", "コーヒー残量が不足しているため、注文を確定できません。");
			forward(request, response, "payment.jsp");
			return;
		}

		try {
			// (6) 今回の注文で減ったコーヒーをDBにINSERTする（Coffee）
			int newCapacity = coffee.getCurrentCapacity() - order.getRequiredCoffee();
			new CoffeeDAO().insert(newCapacity, user.getEmpno(), -order.getRequiredCoffee());

			// (7) 今回の注文情報をDBにINSERTする（Sales, SalesDetail）
			new SalesDAO().insertSale(order);

			// セッションのコーヒー残量を最新化する（連続注文に備える）
			Coffee updated = new CoffeeDAO().findLatest();
			session.setAttribute(COFFEE, updated);

			// つり銭の金種を計算してレシート表示用に設定する
			request.setAttribute("changeUnits", calcChangeUnits(order.getChange()));

			forward(request, response, "receipt.jsp");
		} catch (SQLException e) {
			throw new ServletException("注文確定（DB登録）でデータベースエラーが発生しました。", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 直接GETされた場合は注文受付からやり直す
		response.sendRedirect(request.getContextPath() + "/order");
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
