package com.ibm.eastb.harumi.servlet;

import static com.ibm.eastb.harumi.common.WebConst.ATTR_COFFEE;
import static com.ibm.eastb.harumi.common.WebConst.ATTR_ERROR;
import static com.ibm.eastb.harumi.common.WebConst.ATTR_ORDER;
import static com.ibm.eastb.harumi.common.WebConst.ERR_NO_ITEM_SELECTED;
import static com.ibm.eastb.harumi.common.WebConst.PARAM_QTY_PREFIX;
import static com.ibm.eastb.harumi.common.WebConst.URL_ORDER_JSP;
import static com.ibm.eastb.harumi.common.WebConst.URL_ORDER_SERVLET;
import static com.ibm.eastb.harumi.common.WebConst.URL_PAYMENT_JSP;

import java.io.IOException;

import com.ibm.eastb.harumi.dto.Coffee;
import com.ibm.eastb.harumi.dto.Order;
import com.ibm.eastb.harumi.dto.OrderDetail;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 支払入力サーブレット。<br>
 * 注文受付画面で入力された各商品の注文個数を注文情報に反映し、
 * コーヒー残量が足りるかを判定して、注文内容確認＆支払入力画面へ遷移する。<br>
 * （コミュニケーション図の「支払入力Servlet」に対応）
 *
 * @author harumi development team
 * @version 1.00
 */
@WebServlet("/payment")
public class PaymentServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!ensureLogin(request, response)) {
			return;
		}
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();

		// (1)(2) セッションから注文情報・コーヒー残量を取得する
		Order order = (Order) session.getAttribute(ATTR_ORDER);
		Coffee coffee = (Coffee) session.getAttribute(ATTR_COFFEE);
		if (order == null || coffee == null) {
			// セッション切れ等：注文受付からやり直す
			response.sendRedirect(URL_ORDER_SERVLET);
			return;
		}

		// (3) 各商品の注文個数を注文情報内の商品に設定する
		for (OrderDetail d : order.getDetails()) {
			int qty = parseQuantity(request.getParameter(PARAM_QTY_PREFIX + d.getItemNo()));
			d.setQuantity(qty);
		}

		// 1点も注文されていない場合は注文受付に戻す
		if (!order.hasOrder()) {
			request.setAttribute(ATTR_ERROR, ERR_NO_ITEM_SELECTED);
			forward(request, response, URL_ORDER_JSP);
			return;
		}

		// (4) 注文内容分のコーヒーがタンクに残っているか判断する
		if (order.getRequiredCoffee() > coffee.getCurrentCapacity()) {
			request.setAttribute(ATTR_ERROR,
					"コーヒー残量が不足しています。（必要 " + order.getRequiredCoffee()
							+ "ml / 残量 " + coffee.getCurrentCapacity() + "ml）個数を見直してください。");
			forward(request, response, URL_ORDER_JSP);
			return;
		}

		// 注文内容確認＆支払入力画面へ
		forward(request, response, URL_PAYMENT_JSP);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 直接GETされた場合は注文受付からやり直す
		response.sendRedirect(URL_ORDER_SERVLET);
	}

	/**
	 * 個数パラメータを整数に変換する。未入力・不正値は0として扱う。
	 *
	 * @param value 個数の文字列
	 * @return 0以上の個数
	 */
	private int parseQuantity(String value) {
		if (value == null || value.isBlank()) {
			return 0;
		}
		try {
			int q = Integer.parseInt(value.trim());
			return Math.max(q, 0);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
