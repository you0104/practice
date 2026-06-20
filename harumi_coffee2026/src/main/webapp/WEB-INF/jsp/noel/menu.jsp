<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="com.ibm.eastb.harumi.dto.Employee"%>
<%@ page import="com.ibm.eastb.harumi.dto.Coffee"%>
<%
	NumberFormat nf = NumberFormat.getIntegerInstance();
	Employee user = (Employee) session.getAttribute("user");
	Coffee coffee = (Coffee) request.getAttribute("coffee");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>メインメニュー｜はるみコーヒー販売支援システム</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>
	<header class="app-header">
		<img src="img/logo.png" alt="はるみコーヒー ロゴ">
		<span class="title">はるみコーヒー販売支援システム</span>
		<span class="mode-badge">スクリプトレット版</span>
		<span class="user-info">
			レジ担当：<%= user.getName() %>（<%= user.getEmpno() %>）
			<a class="btn secondary" href="logout">ログアウト</a>
		</span>
	</header>
	<main>
		<div class="card">
			<h1>メインメニュー</h1>
			<p>
				現在のコーヒー残量：
				<% if (coffee != null) { %>
					<span class="coffee-gauge <%= coffee.isLow() ? "coffee-low" : "" %>">
						<%= nf.format(coffee.getCurrentCapacity()) %>ml
						（<%= String.format("%.1f", coffee.getCurrentCapacityLiter()) %>L）
					</span>
					<% if (coffee.isLow()) { %>
						<span class="coffee-low">※残量が少なくなっています</span>
					<% } %>
				<% } else { %>
					未登録
				<% } %>
			</p>
		</div>

		<div class="card">
			<ul class="menu-list">
				<li><a class="btn" href="order">注文受付</a></li>
				<li><a class="btn" href="coffee">コーヒー管理（補充・削減）</a></li>
				<li><a class="btn" href="closing">閉店処理</a></li>
			</ul>
		</div>

		<p class="pattern-note">
			表示パターンの切り替え：
			<a href="login?mode=el">EL/JSTL版</a> ／
			<a href="login?mode=noel">スクリプトレット版</a>
		</p>
	</main>
</body>
</html>
