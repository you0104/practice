<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="java.util.List"%>
<%@ page import="com.ibm.eastb.harumi.dto.Employee"%>
<%@ page import="com.ibm.eastb.harumi.dto.Coffee"%>
<%@ page import="com.ibm.eastb.harumi.dto.ItemSalesSummary"%>
<%
	String ctx = request.getContextPath();
	NumberFormat nf = NumberFormat.getIntegerInstance();
	Employee user = (Employee) session.getAttribute("user");
	@SuppressWarnings("unchecked")
	List<ItemSalesSummary> itemSales = (List<ItemSalesSummary>) request.getAttribute("itemSales");
	Integer todayTotal = (Integer) request.getAttribute("todayTotal");
	Integer usedCoffee = (Integer) request.getAttribute("usedCoffee");
	Coffee coffee = (Coffee) request.getAttribute("coffee");
	boolean closed = Boolean.TRUE.equals(request.getAttribute("closed"));
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>閉店処理｜はるみコーヒー販売支援システム</title>
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
			<h1>閉店処理</h1>

			<% if (closed) { %>
				<p class="msg-info">閉店処理を行いました。タンク残量を0にしました（本日分のコーヒーを破棄）。</p>
			<% } %>

			<h2>当日の商品別売上</h2>
			<table>
				<tr>
					<th class="left">商品</th>
					<th>販売個数</th>
					<th>売上金額(税抜)</th>
				</tr>
				<% if (itemSales == null || itemSales.isEmpty()) { %>
					<tr><td class="center" colspan="3">本日の売上はありません</td></tr>
				<% } else {
					for (ItemSalesSummary s : itemSales) { %>
						<tr>
							<td class="left"><%= s.getName() %></td>
							<td><%= nf.format(s.getTotalQuantity()) %></td>
							<td><%= nf.format(s.getTotalSales()) %>円</td>
						</tr>
				<%	}
				} %>
				<tr class="total-line">
					<th class="left" colspan="2">当日の総売上（税込）</th>
					<td><%= nf.format(todayTotal == null ? 0 : todayTotal) %>円</td>
				</tr>
			</table>

			<h2>コーヒータンクの状況</h2>
			<table>
				<tr>
					<th class="left">タンク総量（当日に注文で使用した全量）</th>
					<td><%= nf.format(usedCoffee == null ? 0 : usedCoffee) %>ml</td>
				</tr>
				<tr>
					<th class="left">残量（閉店時点でタンクに残っている量）</th>
					<td><%= nf.format(coffee == null ? 0 : coffee.getCurrentCapacity()) %>ml</td>
				</tr>
			</table>

			<% if (!closed) { %>
				<form action="<%= ctx %>/closing" method="post"
					onsubmit="return confirm('閉店処理を実行し、タンク残量を0にします。よろしいですか？');">
					<div class="actions">
						<button type="submit" class="btn danger">閉店する（タンク残量を0にする）</button>
						<a class="btn secondary" href="<%= ctx %>/menu">メインメニューへ</a>
					</div>
				</form>
			<% } else { %>
				<div class="actions">
					<a class="btn secondary" href="<%= ctx %>/menu">メインメニューへ</a>
				</div>
			<% } %>
		</div>
	</main>
</body>
</html>
