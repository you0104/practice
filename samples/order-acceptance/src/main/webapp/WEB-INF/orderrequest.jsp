<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>注文受付 - はるみコーヒー</title>
</head>
<body>
<h1>注文受付（Order acceptance）</h1>

<%-- 現在のタンク残量。10L(10000ml)以下で警告色 --%>
<p>タンク残量：
    <strong style="color:${latestCapacity <= 10000 ? 'red' : 'black'}">
        <fmt:formatNumber value="${latestCapacity}"/> ml
    </strong>
    <c:if test="${latestCapacity <= 10000}">（残量わずか）</c:if>
</p>

<%-- エラーメッセージ（残量不足・未入力など） --%>
<c:if test="${not empty error}">
    <p style="color:red;">${error}</p>
</c:if>

<h2>注文リスト</h2>
<table border="1" cellpadding="4">
    <tr><th>商品名</th><th>単価</th><th>個数</th><th>金額</th></tr>
    <c:forEach var="line" items="${sessionScope.order.lines}">
        <tr>
            <td>${line.item.name}</td>
            <td style="text-align:right"><fmt:formatNumber value="${line.item.price}"/> 円</td>
            <td style="text-align:right">${line.quantity}</td>
            <td style="text-align:right"><fmt:formatNumber value="${line.lineAmount}"/> 円</td>
        </tr>
    </c:forEach>
</table>
<p>
    小計：<fmt:formatNumber value="${sessionScope.order.subTotal}"/> 円　/
    消費税(8%)：<fmt:formatNumber value="${sessionScope.order.tax}"/> 円　/
    合計：<strong><fmt:formatNumber value="${sessionScope.order.total}"/> 円</strong>
</p>

<h2>注文入力（コーヒーを押下すると加算されます）</h2>
<%-- 商品ボタン：押下ごとに POST → 同じ画面へ戻る --%>
<c:forEach var="item" items="${items}">
    <form method="post" action="${pageContext.request.contextPath}/orderrequest"
          style="display:inline">
        <input type="hidden" name="itemNo" value="${item.itemNo}">
        <button type="submit" name="action" value="addItem">
            ${item.name}（<fmt:formatNumber value="${item.price}"/>円）
        </button>
    </form>
</c:forEach>

<hr>

<%-- 支払いへ進む：このボタンのときだけ次のJSPへ遷移する --%>
<form method="post" action="${pageContext.request.contextPath}/orderrequest">
    <button type="submit" name="action" value="pay">支払いへ進む</button>
</form>

</body>
</html>
