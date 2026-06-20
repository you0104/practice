package com.ibm.eastb.harumi.servlet;

import static com.ibm.eastb.harumi.common.WebConst.ATTR_USER;
import static com.ibm.eastb.harumi.common.WebConst.ATTR_VIEW_MODE;
import static com.ibm.eastb.harumi.common.WebConst.URL_LOGIN_SERVLET;
import static com.ibm.eastb.harumi.common.WebConst.VIEW_EL;

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
 * セッション属性{@code viewMode}（{@code el} または {@code noel}）で参照先フォルダを決定する。<br>
 * 属性名・URLなどの定数は {@link com.ibm.eastb.harumi.common.WebConst} に集約している。
 *
 * @author harumi development team
 * @version 1.00
 */
public abstract class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * 現在の表示モードを取得する。未設定の場合は{@code el}とする。
	 *
	 * @param request リクエスト
	 * @return 表示モード（{@code el} or {@code noel}）
	 */
	protected String viewMode(HttpServletRequest request) {
		Object mode = request.getSession().getAttribute(ATTR_VIEW_MODE);
		return (mode != null) ? mode.toString() : VIEW_EL;
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
		return (Employee) session.getAttribute(ATTR_USER);
	}

	/**
	 * ログイン済みであることを確認する。未ログインならログイン画面へリダイレクトする。<br>
	 * リダイレクト先はコンテキストルートからの相対パス（contextPathは付けない）。
	 *
	 * @param request  リクエスト
	 * @param response レスポンス
	 * @return ログイン済みならtrue、未ログインならfalse（呼び出し側は処理を中断すること）
	 */
	protected boolean ensureLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (loginUser(request) == null) {
			response.sendRedirect(URL_LOGIN_SERVLET);
			return false;
		}
		return true;
	}
}
