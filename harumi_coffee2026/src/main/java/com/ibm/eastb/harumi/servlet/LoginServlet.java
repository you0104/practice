package com.ibm.eastb.harumi.servlet;

import static com.ibm.eastb.harumi.common.WebConst.ATTR_EMPNO;
import static com.ibm.eastb.harumi.common.WebConst.ATTR_ERROR;
import static com.ibm.eastb.harumi.common.WebConst.ATTR_USER;
import static com.ibm.eastb.harumi.common.WebConst.ATTR_VIEW_MODE;
import static com.ibm.eastb.harumi.common.WebConst.ERR_LOGIN_FAILED;
import static com.ibm.eastb.harumi.common.WebConst.PARAM_EMPNO;
import static com.ibm.eastb.harumi.common.WebConst.PARAM_MODE;
import static com.ibm.eastb.harumi.common.WebConst.PARAM_PASSWORD;
import static com.ibm.eastb.harumi.common.WebConst.URL_LOGIN_JSP;
import static com.ibm.eastb.harumi.common.WebConst.URL_MENU_SERVLET;
import static com.ibm.eastb.harumi.common.WebConst.VIEW_EL;
import static com.ibm.eastb.harumi.common.WebConst.VIEW_NOEL;

import java.io.IOException;
import java.sql.SQLException;

import com.ibm.eastb.harumi.dao.EmployeeDao;
import com.ibm.eastb.harumi.dto.Employee;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 認証（ログイン）サーブレット。<br>
 * レジ担当者のID・パスワードを検証し、認証に成功するとメインメニューへ遷移する。
 * 退職者はログインできない。認証状態はログアウトまでセッションで保持する。
 *
 * @author harumi development team
 * @version 1.00
 */
@WebServlet("/login")
public class LoginServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * ログイン画面を表示する。<br>
	 * クエリパラメータmode（el/noel）が指定されていれば表示モードをセッションに設定する。
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();

		// 表示モード（EL/JSTL版 or スクリプトレット版）の選択を反映する
		String mode = request.getParameter(PARAM_MODE);
		if (VIEW_EL.equals(mode) || VIEW_NOEL.equals(mode)) {
			session.setAttribute(ATTR_VIEW_MODE, mode);
		}

		// すでにログイン済みならメインメニューへ
		if (loginUser(request) != null) {
			response.sendRedirect(URL_MENU_SERVLET);
			return;
		}

		forward(request, response, URL_LOGIN_JSP);
	}

	/**
	 * ログイン認証を行う。
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String empno = request.getParameter(PARAM_EMPNO);
		String password = request.getParameter(PARAM_PASSWORD);

		try {
			Employee emp = EmployeeDao.findForAuth(empno, password);
			if (emp == null) {
				// 認証失敗：エラーを表示してログイン画面に戻す
				request.setAttribute(ATTR_ERROR, ERR_LOGIN_FAILED);
				request.setAttribute(ATTR_EMPNO, empno);
				forward(request, response, URL_LOGIN_JSP);
				return;
			}
			// 認証成功：セッションに従業員を保持してメインメニューへ
			request.getSession().setAttribute(ATTR_USER, emp);
			response.sendRedirect(URL_MENU_SERVLET);
		} catch (SQLException e) {
			throw new ServletException("認証処理でデータベースエラーが発生しました。", e);
		}
	}
}
