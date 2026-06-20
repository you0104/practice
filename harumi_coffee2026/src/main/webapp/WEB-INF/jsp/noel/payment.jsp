<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="com.ibm.eastb.harumi.dto.Employee"%>
<%@ page import="com.ibm.eastb.harumi.dto.Order"%>
<%@ page import="com.ibm.eastb.harumi.dto.OrderDetail"%>
<%
	NumberFormat nf = NumberFormat.getIntegerInstance();
	Employee user = (Employee) session.getAttribute("user");
	Order order = (Order) session.getAttribute("order");
	String errorMsg = (String) request.getAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>注文内容確認・支払入力｜はるみコーヒー販売支援システム</title>
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
			<h1>注文内容の確認・支払入力</h1>

			<% if (errorMsg != null) { %>
				<p class="msg-error"><%= errorMsg %></p>
			<% } %>

			<table>
				<tr>
					<th class="left">商品</th>
					<th>単価(税抜)</th>
					<th>個数</th>
					<th>金額(税抜)</th>
				</tr>
				<% for (OrderDetail d : order.getOrderedDetails()) { %>
					<tr>
						<td class="left"><%= d.getName() %></td>
						<td><%= nf.format(d.getPrice()) %>円</td>
						<td><%= nf.format(d.getQuantity()) %></td>
						<td><%= nf.format(d.getLineSubtotal()) %>円</td>
					</tr>
				<% } %>
				<tr>
					<th class="left" colspan="3">小計（税抜）</th>
					<td><%= nf.format(order.getSubtotal()) %>円</td>
				</tr>
				<tr>
					<th class="left" colspan="3">消費税（8%）</th>
					<td><%= nf.format(order.getTax()) %>円</td>
				</tr>
				<tr class="total-line">
					<th class="left" colspan="3">合計（税込）</th>
					<td><%= nf.format(order.getTotal()) %>円</td>
				</tr>
				<tr>
					<th class="left" colspan="3">使用コーヒー量</th>
					<td><%= nf.format(order.getRequiredCoffee()) %>ml</td>
				</tr>
			</table>

			<form action="receipt" method="post">
				<table>
					<tr>
						<th class="left">預かり金額</th>
						<td class="left">
							<input type="number" name="payment" min="<%= order.getTotal() %>" step="1" required autofocus>円
						</td>
					</tr>
				</table>
				<div class="actions">
					<button type="submit" class="btn">会計する（レシート発行）</button>
					<a class="btn secondary" href="order">注文受付に戻る</a>
				</div>
			</form>
		</div>
	</main>
</body>
</html>
