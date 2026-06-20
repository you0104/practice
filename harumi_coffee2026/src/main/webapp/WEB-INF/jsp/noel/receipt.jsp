<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="java.util.List"%>
<%@ page import="com.ibm.eastb.harumi.dto.Employee"%>
<%@ page import="com.ibm.eastb.harumi.dto.Order"%>
<%@ page import="com.ibm.eastb.harumi.dto.OrderDetail"%>
<%@ page import="com.ibm.eastb.harumi.dto.ChangeUnit"%>
<%
	NumberFormat nf = NumberFormat.getIntegerInstance();
	Employee user = (Employee) session.getAttribute("user");
	Order order = (Order) session.getAttribute("order");
	@SuppressWarnings("unchecked")
	List<ChangeUnit> changeUnits = (List<ChangeUnit>) request.getAttribute("changeUnits");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>レシート｜はるみコーヒー販売支援システム</title>
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
			<p class="msg-info">会計が完了しました。レシートを表示します。</p>

			<div class="receipt">
				<h2>はるみコーヒー</h2>
				<p style="text-align:center;">― RECEIPT ―</p>
				<table>
					<% for (OrderDetail d : order.getOrderedDetails()) { %>
						<tr>
							<td class="left"><%= d.getName() %> × <%= d.getQuantity() %></td>
							<td><%= nf.format(d.getLineSubtotal()) %>円</td>
						</tr>
					<% } %>
					<tr>
						<td class="left">小計（税抜）</td>
						<td><%= nf.format(order.getSubtotal()) %>円</td>
					</tr>
					<tr>
						<td class="left">消費税(8%)</td>
						<td><%= nf.format(order.getTax()) %>円</td>
					</tr>
					<tr class="total-line">
						<td class="left">合計</td>
						<td><%= nf.format(order.getTotal()) %>円</td>
					</tr>
					<tr>
						<td class="left">お預かり</td>
						<td><%= nf.format(order.getPayment()) %>円</td>
					</tr>
					<tr class="total-line">
						<td class="left">お釣り</td>
						<td><%= nf.format(order.getChange()) %>円</td>
					</tr>
				</table>

				<p style="margin:8px 0 2px;">― お釣り金種 ―</p>
				<% if (changeUnits == null || changeUnits.isEmpty()) { %>
					<p style="text-align:center;">お釣りはありません</p>
				<% } else { %>
					<table>
						<% for (ChangeUnit u : changeUnits) { %>
							<tr>
								<td class="left"><%= nf.format(u.getUnit()) %>円</td>
								<td><%= u.getCount() %>枚</td>
							</tr>
						<% } %>
					</table>
				<% } %>
				<p style="text-align:center; margin-top:8px;">ありがとうございました</p>
			</div>

			<div class="actions">
				<form action="order" method="post" style="display:inline;">
					<button type="submit" class="btn">続けて次の注文を受ける</button>
				</form>
				<a class="btn secondary" href="menu">メインメニューへ</a>
			</div>
		</div>
	</main>
</body>
</html>
