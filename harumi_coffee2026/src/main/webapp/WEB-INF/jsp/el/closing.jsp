<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>閉店処理｜はるみコーヒー販売支援システム</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>
	<header class="app-header">
		<img src="img/logo.png" alt="はるみコーヒー ロゴ">
		<span class="title">はるみコーヒー販売支援システム</span>
		<span class="mode-badge">EL/JSTL版</span>
		<span class="user-info">
			レジ担当：${user.name}（${user.empno}）
			<a class="btn secondary" href="${URL_LOGOUT_SERVLET}">ログアウト</a>
		</span>
	</header>
	<main>
		<div class="card">
			<h1>閉店処理</h1>

			<c:if test="${closed}">
				<p class="msg-info">閉店処理を行いました。タンク残量を0にしました（本日分のコーヒーを破棄）。</p>
			</c:if>

			<h2>当日の商品別売上</h2>
			<table>
				<tr>
					<th class="left">商品</th>
					<th>販売個数</th>
					<th>売上金額(税抜)</th>
				</tr>
				<c:choose>
					<c:when test="${empty itemSales}">
						<tr><td class="center" colspan="3">本日の売上はありません</td></tr>
					</c:when>
					<c:otherwise>
						<c:forEach var="s" items="${itemSales}">
							<tr>
								<td class="left">${s.name}</td>
								<td><fmt:formatNumber value="${s.totalQuantity}" /></td>
								<td><fmt:formatNumber value="${s.totalSales}" />円</td>
							</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				<tr class="total-line">
					<th class="left" colspan="2">当日の総売上（税込）</th>
					<td><fmt:formatNumber value="${todayTotal}" />円</td>
				</tr>
			</table>

			<h2>コーヒータンクの状況</h2>
			<table>
				<tr>
					<th class="left">タンク総量（当日に注文で使用した全量）</th>
					<td><fmt:formatNumber value="${usedCoffee}" />ml</td>
				</tr>
				<tr>
					<th class="left">残量（閉店時点でタンクに残っている量）</th>
					<td><fmt:formatNumber value="${coffee.currentCapacity}" />ml</td>
				</tr>
			</table>

			<c:if test="${not closed}">
				<form action="${URL_CLOSING_SERVLET}" method="post"
					onsubmit="return confirm('閉店処理を実行し、タンク残量を0にします。よろしいですか？');">
					<div class="actions">
						<button type="submit" class="btn danger">閉店する（タンク残量を0にする）</button>
						<a class="btn secondary" href="${URL_MENU_SERVLET}">メインメニューへ</a>
					</div>
				</form>
			</c:if>
			<c:if test="${closed}">
				<div class="actions">
					<a class="btn secondary" href="${URL_MENU_SERVLET}">メインメニューへ</a>
				</div>
			</c:if>
		</div>
	</main>
</body>
</html>
