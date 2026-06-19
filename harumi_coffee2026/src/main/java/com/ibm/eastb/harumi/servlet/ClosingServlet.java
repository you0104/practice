package com.ibm.eastb.harumi.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.ibm.eastb.harumi.dao.CoffeeDAO;
import com.ibm.eastb.harumi.dao.SalesDAO;
import com.ibm.eastb.harumi.dto.Coffee;
import com.ibm.eastb.harumi.dto.Employee;
import com.ibm.eastb.harumi.dto.ItemSalesSummary;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 閉店処理サーブレット。<br>
 * 当日の商品別売上・総売上、当日に使用したコーヒー総量とタンク残量を表示する。
 * 閉店を実行すると、タンク残量を0としてCoffeeテーブルへINSERTする（毎日の破棄）。
 *
 * @author harumi development team
 * @version 1.00
 */
@WebServlet("/closing")
public class ClosingServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!ensureLogin(request, response)) {
			return;
		}
		showClosing(request, response, false);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!ensureLogin(request, response)) {
			return;
		}
		Employee user = loginUser(request);
		try {
			// 閉店実行：タンク残量を0にする（破棄は行ごと削除ではなく残量0をINSERT）
			Coffee coffee = new CoffeeDAO().findLatest();
			int current = (coffee != null) ? coffee.getCurrentCapacity() : 0;
			if (current > 0) {
				new CoffeeDAO().insert(0, user.getEmpno(), -current);
			}
			request.setAttribute("closed", Boolean.TRUE);
			showClosing(request, response, true);
		} catch (SQLException e) {
			throw new ServletException("閉店処理（コーヒー破棄）でデータベースエラーが発生しました。", e);
		}
	}

	/**
	 * 当日の売上情報とコーヒー残量を集計して閉店処理画面を表示する。
	 *
	 * @param closed 閉店実行済みかどうか
	 */
	private void showClosing(HttpServletRequest request, HttpServletResponse response, boolean closed)
			throws ServletException, IOException {
		try {
			SalesDAO salesDao = new SalesDAO();
			List<ItemSalesSummary> itemSales = salesDao.findTodayItemSales();
			int todayTotal = salesDao.findTodayTotal();
			int usedCoffee = salesDao.findTodayUsedCoffee(); // タンク総量（当日使用した全コーヒー量）
			Coffee coffee = new CoffeeDAO().findLatest(); // 残量（閉店時に破棄するタンク残量）

			request.setAttribute("itemSales", itemSales);
			request.setAttribute("todayTotal", todayTotal);
			request.setAttribute("usedCoffee", usedCoffee);
			request.setAttribute("coffee", coffee);
			if (closed) {
				request.setAttribute("closed", Boolean.TRUE);
			}
			forward(request, response, "closing.jsp");
		} catch (SQLException e) {
			throw new ServletException("閉店処理の集計でデータベースエラーが発生しました。", e);
		}
	}
}
