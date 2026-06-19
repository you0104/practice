<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="com.ibm.eastb.harumi.dto.Employee"%>
<%@ page import="com.ibm.eastb.harumi.dto.Coffee"%>
<%
	String ctx = request.getContextPath();
	NumberFormat nf = NumberFormat.getIntegerInstance();
	Employee user = (Employee) session.getAttribute("user");
	Coffee coffee = (Coffee) request.getAttribute("coffee");
	String infoMsg = (String) request.getAttribute("infoMsg");
	String errorMsg = (String) request.getAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>コーヒー管理｜はるみコーヒー販売支援システム</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
	<header class="app-header">
		<img src="<%= ctx %>/img/logo.png" alt="はるみコーヒー ロゴ">
		<span class="title">はるみコーヒー販売支援システム</span>
		<span class="mode-badge">スクリプトレット版</span>
		<span class="user-info">
			レジ担当：<%= user.getName() %>（<%= user.getEmpno() %>）
			<a class="btn secondary" href="<%= ctx %>/logout">ログアウト</a>
		</span>
	</header>
	<main>
		<div class="card">
			<h1>コーヒー管理（補充・削減）</h1>

			<% if (infoMsg != null) { %>
				<p class="msg-info"><%= infoMsg %></p>
			<% } %>
			<% if (errorMsg != null) { %>
				<p class="msg-error"><%= errorMsg %></p>
			<% } %>

			<p>
				現在のタンク残量：
				<% if (coffee != null) { %>
					<span class="coffee-gauge <%= coffee.isLow() ? "coffee-low" : "" %>">
						<%= nf.format(coffee.getCurrentCapacity()) %>ml
						（<%= String.format("%.1f", coffee.getCurrentCapacityLiter()) %>L）
					</span>
					<% if (coffee.isLow()) { %>
						<span class="coffee-low">※残量わずか</span>
					<% } %>
				<% } else { %>
					未登録
				<% } %>
			</p>

			<form action="<%= ctx %>/coffee" method="post">
				<table>
					<tr>
						<th class="left">操作</th>
						<td class="left">
							<label><input type="radio" name="operation" value="add" checked> 補充（追加）</label>
							&nbsp;&nbsp;
							<label><input type="radio" name="operation" value="reduce"> 削減（損失）</label>
						</td>
					</tr>
					<tr>
						<th class="left">数量(ml)</th>
						<td class="left">
							<input type="number" name="amount" min="1" step="1" required>ml
						</td>
					</tr>
				</table>
				<div class="actions">
					<button type="submit" class="btn">残量を更新する</button>
					<a class="btn secondary" href="<%= ctx %>/menu">メインメニューへ</a>
				</div>
			</form>
		</div>
	</main>
</body>
</html>
