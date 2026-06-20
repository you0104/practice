<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>注文受付｜はるみコーヒー販売支援システム</title>
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
			<h1>注文受付</h1>
			<p>
				タンク残量：
				<span class="coffee-gauge ${coffee.low ? 'coffee-low' : ''}">
					<fmt:formatNumber value="${coffee.currentCapacity}" />ml
					（<fmt:formatNumber value="${coffee.currentCapacityLiter}" maxFractionDigits="1" />L）
				</span>
				<c:if test="${coffee.low}">
					<span class="coffee-low">※残量わずか。コーヒー管理から補充してください。</span>
				</c:if>
			</p>

			<c:if test="${not empty errorMsg}">
				<p class="msg-error">${errorMsg}</p>
			</c:if>

			<form action="${URL_PAYMENT_SERVLET}" method="post">
				<table>
					<tr>
						<th class="left">商品</th>
						<th>単価(税抜)</th>
						<th>使用量(ml)</th>
						<th>注文個数</th>
					</tr>
					<c:forEach var="d" items="${order.details}">
						<tr>
							<td class="left">${d.name}</td>
							<td><fmt:formatNumber value="${d.price}" />円</td>
							<td><fmt:formatNumber value="${d.coffeeAmount}" />ml</td>
							<td class="center">
								<input type="number" name="${PARAM_QTY_PREFIX}${d.itemNo}" value="${d.quantity}" min="0" step="1">
							</td>
						</tr>
					</c:forEach>
				</table>
				<div class="actions">
					<button type="submit" class="btn">注文内容を確認する</button>
					<a class="btn secondary" href="${URL_MENU_SERVLET}">メインメニューへ</a>
				</div>
			</form>
		</div>
	</main>
</body>
</html>
