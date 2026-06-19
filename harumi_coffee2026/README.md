# はるみコーヒー販売支援システム（harumi_coffee2026）

Eclipse + Jakarta EE（動的Webプロジェクト）で開発した、はるみコーヒーショップの
販売支援システム（レジ業務シミュレーション）です。事例研究課題「はるみコーヒー
ショップ（Java プロジェクト開発演習）」の Web アプリ版として実装しています。

## 実行環境

| 項目 | 内容 |
| --- | --- |
| 言語 / 実行基盤 | Java 25 / Jakarta EE（Servlet 6.x, JSP） |
| アプリサーバ | Apache Tomcat 11 |
| DBMS | IBM DB2（`jdbc:db2://localhost:50000/harumi`） |
| ブラウザ | Google Chrome 等 |
| コンテキストルート | `/harumi_coffee` |

## アーキテクチャ（構成方針）

- **DAO / DTO / Servlet / JSP** の4層構成。**Logic（ビジネスロジック専用）クラスは作らず**、
  業務ロジックは各 Servlet に実装しています。
- 画面遷移とセッション設計は、添付の **コミュニケーション図（PPTX）** に準拠しています。
  - `注文受付Servlet` → セッション属性 `order` / `coffee` を生成
  - `支払入力Servlet` → 注文個数を反映し、コーヒー残量の充足を判定
  - `レシートServlet` → 支払金額を判定し、Coffee と Sales/SalesDetail を登録

### パッケージ / ディレクトリ

```
src/main/java/com/ibm/eastb/harumi/
├── dao/    … DBアクセス
│   ├── ConnectionManager.java  （DB接続の共通処理）
│   ├── EmployeeDAO.java        （認証）
│   ├── ItemDAO.java            （販売中商品の取得）
│   ├── CoffeeDAO.java          （コーヒー残量の取得・INSERT）
│   └── SalesDAO.java           （売上の登録・当日集計）
├── dto/    … データ保持
│   ├── Employee / Item / Coffee
│   ├── Order / OrderDetail      （注文情報・注文明細＋金額計算）
│   ├── ItemSalesSummary         （閉店処理の商品別売上）
│   └── ChangeUnit               （つり銭の金種）
└── servlet/ … 画面制御＋業務ロジック
    ├── BaseServlet（共通：表示モード切替・ログインチェック）
    ├── LoginServlet / LogoutServlet / MenuServlet
    ├── OrderServlet（注文受付）
    ├── PaymentServlet（支払入力）
    ├── ReceiptServlet（レシート）
    ├── CoffeeServlet（コーヒー管理）
    └── ClosingServlet（閉店処理）

src/main/webapp/
├── index.jsp                 （表示パターン選択）
├── css/style.css
├── img/logo.png
└── WEB-INF/jsp/
    ├── el/    … EL/JSTL 使用版（${ } と <c:forEach> 等）
    └── noel/  … スクリプトレット版（<% %> / <%= %>、EL/JSTL 未使用）
```

## EL/JSTL 使用版・未使用版の2パターン

学習用に、**同じ機能のJSPを2セット**用意しています。どちらでも一通り動作します。

- **EL/JSTL 使用版**（`WEB-INF/jsp/el/`）：`${order.total}` などの EL と、
  `<c:forEach>` `<c:if>` `<fmt:formatNumber>` などの JSTL タグで構築。
- **スクリプトレット版**（`WEB-INF/jsp/noel/`）：`<% %>` / `<%= %>` の Java コードで構築。

切り替え方法：

- 起動直後の `index.jsp` でパターンを選択
- ログイン後もメニュー下部のリンク、または `/<context>/login?mode=el` /
  `?mode=noel` でいつでも切り替え可能
- 選択結果はセッション属性 `viewMode`（`el` / `noel`）で保持し、`BaseServlet` が
  `/WEB-INF/jsp/<mode>/<画面>.jsp` へフォワードします。

## 主な機能（基本4機能）

1. **認証（ログイン）**：従業員ID・パスワードで認証。退職者はログイン不可。認証状態は
   ログアウトまでセッションで保持。
2. **注文受付**：商品リストとタンク残量を表示。注文個数を入力 → 金額計算（消費税8%外税・
   端数切捨て）→ つり銭と金種を表示。注文確定で Sales/SalesDetail と Coffee（残量減算）を
   登録。残量10L以下はアラート表示、残量を超える注文は受付不可。レシート後は
   メニューに戻らず「続けて次の注文」が可能。
3. **コーヒー管理**：タンクへの補充（追加）／損失（削減）を手入力で反映（Coffee へ INSERT）。
4. **閉店処理**：当日の商品別売上・総売上、当日使用コーヒー総量・残量を表示。閉店実行で
   タンク残量0を INSERT（毎日の破棄）。

## セットアップ手順

1. **DB準備（DB2）**：`db/HarumiCoffee_CreateDB_utf8.sql` を実行してテーブルと
   テストデータを作成します（事前に `HARUMI` データベースを作成・接続）。
2. **接続設定**：`ConnectionManager.java` の URL / USER / PASS を環境に合わせて調整。
3. **Eclipse へインポート**：本プロジェクトを「既存プロジェクトのインポート」で取り込み、
   Tomcat 11 ランタイムに割り当てて起動。
4. **アクセス**：`http://localhost:8080/harumi_coffee/`

### テスト用アカウント

| 従業員番号 | パスワード | 区分 |
| --- | --- | --- |
| 1111 | 1111 | レジ担当 |
| 3333 | 3333 | 店舗責任者 |
| 5555 | 5555 | 退職者（ログイン不可） |
