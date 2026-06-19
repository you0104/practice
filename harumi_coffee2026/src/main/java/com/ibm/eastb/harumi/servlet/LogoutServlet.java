package com.ibm.eastb.harumi.servlet;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ログアウトサーブレット。<br>
 * セッションを破棄してログイン画面へ戻す。表示モードの選択は引き継ぐ。
 *
 * @author harumi development team
 * @version 1.00
 */
@WebServlet("/logout")
public class LogoutServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		// 表示モードはログアウト後も引き継ぐ
		Object mode = session.getAttribute(VIEW_MODE);
		session.invalidate();
		if (mode != null) {
			request.getSession().setAttribute(VIEW_MODE, mode);
		}
		response.sendRedirect(request.getContextPath() + "/login");
	}
}
