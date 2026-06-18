# Eclipse へのインポート手順

このプロジェクトは **Maven の動的Webプロジェクト（war）** です。Eclipse（Pleiades / Eclipse IDE for Enterprise Java and Web Developers 推奨）で以下の手順でインポートします。

## 1. zip を展開
`harumi-order-sample.zip` を任意のフォルダに展開します（プロジェクトフォルダ `harumi-order-sample` が現れます）。

## 2. Eclipse にインポート
1. メニュー **File → Import...**
2. **Maven → Existing Maven Projects** を選択 → Next
3. **Root Directory** に展開した `harumi-order-sample` フォルダを指定
4. `pom.xml` がチェックされていることを確認 → **Finish**
5. 初回は依存ライブラリ（Servlet API / JSTL / DB2 ドライバ）が自動ダウンロードされます（要ネットワーク）

> 「Existing Projects into Workspace」ではなく **「Existing Maven Projects」** を使ってください（m2e が依存解決とビルドパス設定を行います）。

## 3. サーバー（Tomcat 10.1）に追加して実行
1. **Window → Show View → Servers** でサーバービューを開く
2. Tomcat 10.1 を登録（未登録なら **New → Server** から）
3. プロジェクトを右クリック → **Run As → Run on Server**

## 4. 事前準備
- **JDK 17**（`pom.xml` の `maven.compiler.release=17`）。別バージョンに合わせる場合は値を変更。
- **Tomcat 10.1 以降**（`jakarta.*` 名前空間）。Tomcat 9 以前を使う場合はソースの `jakarta.servlet.*` を `javax.servlet.*` に置換し、JSTL/Servlet API も `javax` 版へ変更が必要。
- **DB2** を `db/01_ddl.sql` → `db/02_testdata.sql`（→ 必要に応じ `db/03_coupon.sql`）で構築。
- `com.harumi.dao.DbUtil` の接続情報（URL/ユーザー/パスワード）を環境に合わせて編集。
- このサンプルはログイン済み（セッション属性 `user`）が前提です。未ログインだと `/login` へリダイレクトします。動作確認だけ行う場合は、`OrderRequestServlet#ensureLoggedIn` を一時的に `return true;` にするか、簡易ログインを用意してください。

## トラブルシュート
- **JSTLのタグが解決されない** … m2e の依存ダウンロードが完了しているか確認。`Maven → Update Project` を実行。
- **`jakarta.servlet` が見つからない** … プロジェクトのターゲットランタイムに Tomcat 10.1 が設定されているか確認。
- **DB2ドライバのダウンロード失敗** … 社内リポジトリ等を使う場合は `pom.xml` の `com.ibm.db2:jcc` を環境のドライバに合わせて変更、またはローカルjarをビルドパスへ追加。
