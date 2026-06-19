package com.ibm.eastb.harumi.servlet;

import java.io.IOException;

import com.ibm.eastb.harumi.dto.Employee;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 各サーブレット共通の基底クラス（インフラ処理）。<br>
 * 業務ロジックは持たず、以下の共通処理だけを提供する。
 * <ul>
 * <li>表示モード（EL/JSTL版 or スクリプトレット版）に応じたJSPへのフォワード</li>
 * <li>ログイン中従業員の取得とログインチェック</li>
 * </ul>
 * EL/JSTL版とスクリプトレット版の2セットのJSPを切り替えるため、
 * セッション属性"viewMode"（"el" または "noel"）で参照先フォルダを決定する。
 *
 * @author harumi development team
 * @version 1.00
 */
public abstract class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/** セッション属性名：ログイン中従業員 */
	protected static final String USER = "user";
	/** セッション属性名：作成中の注文情報 */
	protected static final String ORDER = "order";
	/** セッション属性名：最新のコーヒー残量 */
	protected static final String COFFEE = "coffee";
	/** セッション属性名：表示モード（"el" or "noel"） */
	protected static final String VIEW_MODE = "viewMode";

	/**
	 * 現在の表示モードを取得する。未設定の場合は"el"とする。
	 *
	 * @param request リクエスト
	 * @return 表示モード（"el" or "noel"）
	 */
	protected String viewMode(HttpServletRequest request) {
		Object mode = request.getSession().getAttribute(VIEW_MODE);
		return (mode != null) ? mode.toString() : "el";
	}

	/**
	 * 表示モードに応じたJSPへフォワードする。<br>
	 * 例：page="order.jsp" → "/WEB-INF/jsp/el/order.jsp" または "/WEB-INF/jsp/noel/order.jsp"
	 *
	 * @param request  リクエスト
	 * @param response レスポンス
	 * @param page     JSPファイル名
	 */
	protected void forward(HttpServletRequest request, HttpServletResponse response, String page)
			throws ServletException, IOException {
		String path = "/WEB-INF/jsp/" + viewMode(request) + "/" + page;
		request.getRequestDispatcher(path).forward(request, response);
	}

	/**
	 * ログイン中の従業員を取得する。
	 *
	 * @param request リクエスト
	 * @return ログイン中従業員。未ログインの場合はnull
	 */
	protected Employee loginUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (Employee) session.getAttribute(USER);
	}

	/**
	 * ログイン済みであることを確認する。未ログインならログイン画面へリダイレクトする。
	 *
	 * @param request  リクエスト
	 * @param response レスポンス
	 * @return ログイン済みならtrue、未ログインならfalse（呼び出し側は処理を中断すること）
	 */
	protected boolean ensureLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (loginUser(request) == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return false;
		}
		return true;
	}
}
