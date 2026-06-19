package com.ibm.eastb.harumi.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.ibm.eastb.harumi.dao.EmployeeDAO;
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
		String mode = request.getParameter("mode");
		if ("el".equals(mode) || "noel".equals(mode)) {
			session.setAttribute(VIEW_MODE, mode);
		}

		// すでにログイン済みならメインメニューへ
		if (loginUser(request) != null) {
			response.sendRedirect(request.getContextPath() + "/menu");
			return;
		}

		forward(request, response, "login.jsp");
	}

	/**
	 * ログイン認証を行う。
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String empno = request.getParameter("empno");
		String password = request.getParameter("password");

		try {
			Employee emp = new EmployeeDAO().findForAuth(empno, password);
			if (emp == null) {
				// 認証失敗：エラーを表示してログイン画面に戻す
				request.setAttribute("errorMsg", "従業員番号またはパスワードが正しくありません。");
				request.setAttribute("empno", empno);
				forward(request, response, "login.jsp");
				return;
			}
			// 認証成功：セッションに従業員を保持してメインメニューへ
			request.getSession().setAttribute(USER, emp);
			response.sendRedirect(request.getContextPath() + "/menu");
		} catch (SQLException e) {
			throw new ServletException("認証処理でデータベースエラーが発生しました。", e);
		}
	}
}
