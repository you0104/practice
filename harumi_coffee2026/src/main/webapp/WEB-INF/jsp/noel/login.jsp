<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// スクリプトレット版：EL/JSTLを使わず、Javaコードで画面を構築する
	String errorMsg = (String) request.getAttribute("errorMsg");
	String empno = (String) request.getAttribute("empno");
	if (empno == null) {
		empno = "";
	}
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>ログイン｜はるみコーヒー販売支援システム</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>
	<header class="app-header">
		<img src="img/logo.png" alt="はるみコーヒー ロゴ">
		<span class="title">はるみコーヒー販売支援システム</span>
		<span class="mode-badge">スクリプトレット版</span>
	</header>
	<main>
		<div class="card">
			<h1>レジ担当者ログイン</h1>

			<% if (errorMsg != null) { %>
				<p class="msg-error"><%= errorMsg %></p>
			<% } %>

			<form action="login" method="post">
				<table>
					<tr>
						<th class="left">従業員番号</th>
						<td class="left">
							<input type="text" name="empno" value="<%= empno %>" autofocus required>
						</td>
					</tr>
					<tr>
						<th class="left">パスワード</th>
						<td class="left">
							<input type="password" name="password" required>
						</td>
					</tr>
				</table>
				<div class="actions">
					<button type="submit" class="btn">ログイン</button>
					<a class="btn secondary" href="index.jsp">パターン選択へ戻る</a>
				</div>
			</form>

			<p class="pattern-note">
				テスト用アカウント例：従業員番号 <code>1111</code> / パスワード <code>1111</code>（レジ担当）<br>
				※ 退職者(<code>5555</code>)はログインできません。
			</p>
		</div>
	</main>
</body>
</html>
