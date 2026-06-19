<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>メインメニュー｜はるみコーヒー販売支援システム</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<header class="app-header">
		<img src="${pageContext.request.contextPath}/img/logo.png" alt="はるみコーヒー ロゴ">
		<span class="title">はるみコーヒー販売支援システム</span>
		<span class="mode-badge">EL/JSTL版</span>
		<span class="user-info">
			レジ担当：${user.name}（${user.empno}）
			<a class="btn secondary" href="${pageContext.request.contextPath}/logout">ログアウト</a>
		</span>
	</header>
	<main>
		<div class="card">
			<h1>メインメニュー</h1>
			<p>
				現在のコーヒー残量：
				<c:choose>
					<c:when test="${not empty coffee}">
						<span class="coffee-gauge ${coffee.low ? 'coffee-low' : ''}">
							<fmt:formatNumber value="${coffee.currentCapacity}" />ml
							（<fmt:formatNumber value="${coffee.currentCapacityLiter}" maxFractionDigits="1" />L）
						</span>
						<c:if test="${coffee.low}">
							<span class="coffee-low">※残量が少なくなっています</span>
						</c:if>
					</c:when>
					<c:otherwise>未登録</c:otherwise>
				</c:choose>
			</p>
		</div>

		<div class="card">
			<ul class="menu-list">
				<li><a class="btn" href="${pageContext.request.contextPath}/order">注文受付</a></li>
				<li><a class="btn" href="${pageContext.request.contextPath}/coffee">コーヒー管理（補充・削減）</a></li>
				<li><a class="btn" href="${pageContext.request.contextPath}/closing">閉店処理</a></li>
			</ul>
		</div>

		<p class="pattern-note">
			表示パターンの切り替え：
			<a href="${pageContext.request.contextPath}/login?mode=el">EL/JSTL版</a> ／
			<a href="${pageContext.request.contextPath}/login?mode=noel">スクリプトレット版</a>
		</p>
	</main>
</body>
</html>
