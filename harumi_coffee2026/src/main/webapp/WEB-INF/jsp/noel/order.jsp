<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="com.ibm.eastb.harumi.dto.Employee"%>
<%@ page import="com.ibm.eastb.harumi.dto.Coffee"%>
<%@ page import="com.ibm.eastb.harumi.dto.Order"%>
<%@ page import="com.ibm.eastb.harumi.dto.OrderDetail"%>
<%
	String ctx = request.getContextPath();
	NumberFormat nf = NumberFormat.getIntegerInstance();
	Employee user = (Employee) session.getAttribute("user");
	Coffee coffee = (Coffee) session.getAttribute("coffee");
	Order order = (Order) session.getAttribute("order");
	String errorMsg = (String) request.getAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>注文受付｜はるみコーヒー販売支援システム</title>
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
			<h1>注文受付</h1>
			<p>
				タンク残量：
				<span class="coffee-gauge <%= coffee.isLow() ? "coffee-low" : "" %>">
					<%= nf.format(coffee.getCurrentCapacity()) %>ml
					（<%= String.format("%.1f", coffee.getCurrentCapacityLiter()) %>L）
				</span>
				<% if (coffee.isLow()) { %>
					<span class="coffee-low">※残量わずか。コーヒー管理から補充してください。</span>
				<% } %>
			</p>

			<% if (errorMsg != null) { %>
				<p class="msg-error"><%= errorMsg %></p>
			<% } %>

			<form action="<%= ctx %>/payment" method="post">
				<table>
					<tr>
						<th class="left">商品</th>
						<th>単価(税抜)</th>
						<th>使用量(ml)</th>
						<th>注文個数</th>
					</tr>
					<% for (OrderDetail d : order.getDetails()) { %>
						<tr>
							<td class="left"><%= d.getName() %></td>
							<td><%= nf.format(d.getPrice()) %>円</td>
							<td><%= nf.format(d.getCoffeeAmount()) %>ml</td>
							<td class="center">
								<input type="number" name="qty_<%= d.getItemNo() %>" value="<%= d.getQuantity() %>" min="0" step="1">
							</td>
						</tr>
					<% } %>
				</table>
				<div class="actions">
					<button type="submit" class="btn">注文内容を確認する</button>
					<a class="btn secondary" href="<%= ctx %>/menu">メインメニューへ</a>
				</div>
			</form>
		</div>
	</main>
</body>
</html>
