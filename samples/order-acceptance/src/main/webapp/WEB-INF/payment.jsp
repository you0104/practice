<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>支払い - はるみコーヒー</title>
</head>
<body>
<h1>支払い（Payment）</h1>

<%-- 注文受付画面から「支払いへ進む」で遷移してきた次のJSP。
     注文内容はセッションの order をそのまま参照して表示できる。 --%>
<h2>ご注文内容</h2>
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

<p>小計：<fmt:formatNumber value="${sessionScope.order.subTotal}"/> 円</p>
<c:if test="${sessionScope.order.discount > 0}">
    <p>クーポン値引：- <fmt:formatNumber value="${sessionScope.order.discount}"/> 円</p>
</c:if>
<p>消費税(8%・切捨)：<fmt:formatNumber value="${sessionScope.order.tax}"/> 円</p>
<p>合計：<strong><fmt:formatNumber value="${sessionScope.order.total}"/> 円</strong></p>

<%-- 預り金入力 → 確定で 売上/売上明細/コーヒー残量 をDB登録する流れ（このサンプルでは画面のみ） --%>
<form method="post" action="${pageContext.request.contextPath}/payment">
    預り金：<input type="number" name="payment" min="0" required> 円
    <button type="submit" name="action" value="confirm">会計を確定する</button>
</form>

<p><a href="${pageContext.request.contextPath}/orderrequest">← 注文に戻る</a></p>

</body>
</html>
