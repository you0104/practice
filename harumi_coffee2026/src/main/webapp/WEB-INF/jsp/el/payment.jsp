<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
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
		<span class="mode-badge">EL/JSTL版</span>
		<span class="user-info">
			レジ担当：${user.name}（${user.empno}）
			<a class="btn secondary" href="${URL_LOGOUT_SERVLET}">ログアウト</a>
		</span>
	</header>
	<main>
		<div class="card">
			<h1>注文内容の確認・支払入力</h1>

			<c:if test="${not empty errorMsg}">
				<p class="msg-error">${errorMsg}</p>
			</c:if>

			<table>
				<tr>
					<th class="left">商品</th>
					<th>単価(税抜)</th>
					<th>個数</th>
					<th>金額(税抜)</th>
				</tr>
				<c:forEach var="d" items="${order.orderedDetails}">
					<tr>
						<td class="left">${d.name}</td>
						<td><fmt:formatNumber value="${d.price}" />円</td>
						<td><fmt:formatNumber value="${d.quantity}" /></td>
						<td><fmt:formatNumber value="${d.lineSubtotal}" />円</td>
					</tr>
				</c:forEach>
				<tr>
					<th class="left" colspan="3">小計（税抜）</th>
					<td><fmt:formatNumber value="${order.subtotal}" />円</td>
				</tr>
				<tr>
					<th class="left" colspan="3">消費税（8%）</th>
					<td><fmt:formatNumber value="${order.tax}" />円</td>
				</tr>
				<tr class="total-line">
					<th class="left" colspan="3">合計（税込）</th>
					<td><fmt:formatNumber value="${order.total}" />円</td>
				</tr>
				<tr>
					<th class="left" colspan="3">使用コーヒー量</th>
					<td><fmt:formatNumber value="${order.requiredCoffee}" />ml</td>
				</tr>
			</table>

			<form action="${URL_RECEIPT_SERVLET}" method="post">
				<table>
					<tr>
						<th class="left">預かり金額</th>
						<td class="left">
							<input type="number" name="${PARAM_PAYMENT}" min="${order.total}" step="1" required autofocus>円
						</td>
					</tr>
				</table>
				<div class="actions">
					<button type="submit" class="btn">会計する（レシート発行）</button>
					<a class="btn secondary" href="${URL_ORDER_SERVLET}">注文受付に戻る</a>
				</div>
			</form>
		</div>
	</main>
</body>
</html>
