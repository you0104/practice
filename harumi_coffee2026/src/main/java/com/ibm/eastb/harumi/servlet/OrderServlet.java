package com.ibm.eastb.harumi.servlet;

import static com.ibm.eastb.harumi.common.WebConst.ATTR_COFFEE;
import static com.ibm.eastb.harumi.common.WebConst.ATTR_ORDER;
import static com.ibm.eastb.harumi.common.WebConst.URL_ORDER_JSP;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.ibm.eastb.harumi.dao.CoffeeDao;
import com.ibm.eastb.harumi.dao.ItemDao;
import com.ibm.eastb.harumi.dto.Coffee;
import com.ibm.eastb.harumi.dto.Employee;
import com.ibm.eastb.harumi.dto.Item;
import com.ibm.eastb.harumi.dto.Order;
import com.ibm.eastb.harumi.dto.OrderDetail;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 注文受付サーブレット。<br>
 * 注文受付画面を表示する。商品リストと現在のコーヒー残量を取得し、
 * 注文情報(Order)を生成してセッションに保持する。<br>
 * （コミュニケーション図の「注文受付Servlet」に対応）
 *
 * @author harumi development team
 * @version 1.00
 */
@WebServlet("/order")
public class OrderServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!ensureLogin(request, response)) {
			return;
		}
		Employee user = loginUser(request);

		try {
			// (1) 商品リストを取得する
			List<Item> items = ItemDao.findOnSale();
			// (2) タンク内に残っているコーヒーを取得する
			Coffee coffee = CoffeeDao.findLatest();

			// (3) 商品リストを基に注文情報を生成する（個数は0で初期化）
			Order order = new Order();
			order.setEmpno(user.getEmpno());
			for (Item item : items) {
				order.getDetails().add(new OrderDetail(item));
			}

			// (4)(5) セッション属性 "order" / "coffee" に設定する
			HttpSession session = request.getSession();
			session.setAttribute(ATTR_ORDER, order);
			session.setAttribute(ATTR_COFFEE, coffee);

			forward(request, response, URL_ORDER_JSP);
		} catch (SQLException e) {
			throw new ServletException("注文受付の初期化でデータベースエラーが発生しました。", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 「次の注文」などPOSTで来た場合も新規の注文受付として扱う
		doGet(request, response);
	}
}
