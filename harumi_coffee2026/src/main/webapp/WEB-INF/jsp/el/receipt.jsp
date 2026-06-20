<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
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
		<span class="mode-badge">EL/JSTL版</span>
		<span class="user-info">
			レジ担当：${user.name}（${user.empno}）
			<a class="btn secondary" href="${URL_LOGOUT_SERVLET}">ログアウト</a>
		</span>
	</header>
	<main>
		<div class="card">
			<p class="msg-info">会計が完了しました。レシートを表示します。</p>

			<div class="receipt">
				<h2>はるみコーヒー</h2>
				<p style="text-align:center;">― RECEIPT ―</p>
				<table>
					<c:forEach var="d" items="${order.orderedDetails}">
						<tr>
							<td class="left">${d.name} × ${d.quantity}</td>
							<td><fmt:formatNumber value="${d.lineSubtotal}" />円</td>
						</tr>
					</c:forEach>
					<tr>
						<td class="left">小計（税抜）</td>
						<td><fmt:formatNumber value="${order.subtotal}" />円</td>
					</tr>
					<tr>
						<td class="left">消費税(8%)</td>
						<td><fmt:formatNumber value="${order.tax}" />円</td>
					</tr>
					<tr class="total-line">
						<td class="left">合計</td>
						<td><fmt:formatNumber value="${order.total}" />円</td>
					</tr>
					<tr>
						<td class="left">お預かり</td>
						<td><fmt:formatNumber value="${order.payment}" />円</td>
					</tr>
					<tr class="total-line">
						<td class="left">お釣り</td>
						<td><fmt:formatNumber value="${order.change}" />円</td>
					</tr>
				</table>

				<p style="margin:8px 0 2px;">― お釣り金種 ―</p>
				<c:choose>
					<c:when test="${empty changeUnits}">
						<p style="text-align:center;">お釣りはありません</p>
					</c:when>
					<c:otherwise>
						<table>
							<c:forEach var="u" items="${changeUnits}">
								<tr>
									<td class="left"><fmt:formatNumber value="${u.unit}" />円</td>
									<td>${u.count}枚</td>
								</tr>
							</c:forEach>
						</table>
					</c:otherwise>
				</c:choose>
				<p style="text-align:center; margin-top:8px;">ありがとうございました</p>
			</div>

			<div class="actions">
				<form action="${URL_ORDER_SERVLET}" method="post" style="display:inline;">
					<button type="submit" class="btn">続けて次の注文を受ける</button>
				</form>
				<a class="btn secondary" href="${URL_MENU_SERVLET}">メインメニューへ</a>
			</div>
		</div>
	</main>
</body>
</html>
