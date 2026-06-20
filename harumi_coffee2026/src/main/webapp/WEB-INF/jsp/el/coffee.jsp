<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>コーヒー管理｜はるみコーヒー販売支援システム</title>
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
			<h1>コーヒー管理（補充・削減）</h1>

			<c:if test="${not empty infoMsg}">
				<p class="msg-info">${infoMsg}</p>
			</c:if>
			<c:if test="${not empty errorMsg}">
				<p class="msg-error">${errorMsg}</p>
			</c:if>

			<p>
				現在のタンク残量：
				<span class="coffee-gauge ${coffee.low ? 'coffee-low' : ''}">
					<fmt:formatNumber value="${coffee.currentCapacity}" />ml
					（<fmt:formatNumber value="${coffee.currentCapacityLiter}" maxFractionDigits="1" />L）
				</span>
				<c:if test="${coffee.low}">
					<span class="coffee-low">※残量わずか</span>
				</c:if>
			</p>

			<form action="${URL_COFFEE_SERVLET}" method="post">
				<table>
					<tr>
						<th class="left">操作</th>
						<td class="left">
							<label><input type="radio" name="operation" value="add" checked> 補充（追加）</label>
							&nbsp;&nbsp;
							<label><input type="radio" name="operation" value="reduce"> 削減（損失）</label>
						</td>
					</tr>
					<tr>
						<th class="left">数量(ml)</th>
						<td class="left">
							<input type="number" name="amount" min="1" step="1" required>ml
						</td>
					</tr>
				</table>
				<div class="actions">
					<button type="submit" class="btn">残量を更新する</button>
					<a class="btn secondary" href="${URL_MENU_SERVLET}">メインメニューへ</a>
				</div>
			</form>
		</div>
	</main>
</body>
</html>
