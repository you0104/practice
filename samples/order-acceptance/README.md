# 注文受付サンプル（コーヒー加算ループ → 支払い遷移）

「コーヒーボタン押下 → サーブレット経由で加算 → 同じJSPへ自己遷移」「支払いへ進む → 次のJSPへ遷移」を
JakartaEE（Servlet/JSP）で実装した最小サンプルです。ファイルB（コミュニケーション図サンプル）スライド6の設計に対応します。

## 動作の流れ

```
[GET]  /orderrequest                 … メインメニューから注文受付画面を表示
[POST] /orderrequest action=addItem  … 注文に1個加算 → orderrequest.jsp へ forward（自己ループ）
[POST] /orderrequest action=pay      … payment.jsp へ forward（次の画面）
```

- 注文中の内容はセッション属性 `order`（`com.harumi.model.Order`）に蓄積。
- タンク残量は毎回 `CoffeeDao.selectLatestCapacity()` でDBから取得し直して表示・残量チェック。
- DBへの売上・残量登録は「支払い確定」時に行う想定（本サンプルは payment.jsp 表示まで）。

## ファイル構成
```
src/main/java/com/harumi/
  model/Item.java          商品
  model/OrderLine.java     注文明細1行（商品＋数量）
  model/Order.java         注文（加算・小計/消費税/合計・コーヒー使用量）
  dao/DbUtil.java          DB接続（学習用にDriverManager。接続情報は要書き換え）
  dao/ItemDao.java         商品取得（販売可能商品の一覧・1件取得）
  dao/CoffeeDao.java       タンク残量の取得／追記
  servlet/OrderRequestServlet.java  注文受付（addItem＝自己ループ / pay＝次画面）
src/main/webapp/WEB-INF/
  orderrequest.jsp         注文受付画面（注文リスト・商品ボタン・支払いへ進む）
  payment.jsp              支払い画面（次のJSP）
```

## 前提・動かし方（概要）
- Tomcat 10 以降（`jakarta.*` 名前空間）。Tomcat 9 以前(`javax.*`)で動かす場合は import を `javax.servlet.*` に置換。
- DB2 JDBCドライバ（`com.ibm.db2.jcc.DB2Driver`）をクラスパスに追加し、`DbUtil` の接続情報を環境に合わせて変更。
- DBは `db/01_ddl.sql` → `db/02_testdata.sql`（→ 必要なら `db/03_coupon.sql`）で構築。
- ログイン状態（セッション属性 `user`）が前提。認証サーブレットは別途用意し、ログイン後に `/orderrequest` へ。
- JSTL（`jakarta.tags.core` / `jakarta.tags.fmt`）を利用。

## 設計上のポイント
- **自己ループは forward**：`addItem` 後に同じ `orderrequest.jsp` へ forward して加算後の状態を再表示。
- **二重送信に注意**：forward はURLが変わらないため、加算後にブラウザ更新するとPOST再送で二重加算になる。
  これを避けるなら **PRG（加算後 `sendRedirect("/orderrequest")` → `doGet` で表示）** に変更する。
- **残量チェックは加算前**にDBの最新残量と比較し、超過時は加算せず `error` を表示。
- **支払いのときだけ**次画面へ遷移（`action=pay`）。注文が空なら遷移させない。

詳細な遷移は `../../docs/注文受付_画面遷移.md` を参照。
