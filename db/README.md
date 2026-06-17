# DB セットアップ（はるみコーヒー販売支援システム ver1.0）

DB2 10.4 を前提とした、データベース構築用スクリプト一式です。

## ファイル
| ファイル | 内容 |
|----------|------|
| `01_ddl.sql` | テーブル作成（Employee / Item / Coffee / Sales / SalesDetail） |
| `02_testdata.sql` | テストデータ（従業員・商品・当日1営業日分の売上/コーヒー履歴） |
| `03_coupon.sql` | クーポン機能の追加（Couponテーブル＋Salesへの列追加・テストデータ） |

## 実行手順（例）
```sh
# データベース作成（初回のみ）
db2 "CREATE DATABASE harumi"
db2 "CONNECT TO harumi"

# テーブル作成 → テストデータ投入
db2 -tvf 01_ddl.sql
db2 -tvf 02_testdata.sql
db2 -tvf 03_coupon.sql   # クーポン機能を使う場合
```

## データモデル概要
```
Employee 1──* Sales 1──* SalesDetail *──1 Item
Employee 1──* Coffee
```
- **Employee / Item** … マスタ（退職・削除は論理フラグで保持）
- **Coffee** … タンク残量を**履歴方式**で保持。最新残量は `MAX(updateNo)` の行
- **Sales / SalesDetail** … 注文1件=Sales1行、明細=SalesDetail（価格・コーヒー量を複製保持）

## 補足・既知の論点
- テーブル定義書の従業員テーブル物理名 `Emploee` は綴り誤りのため、本DDLでは `Employee` に統一。
- 消費税は **8%・切り捨て**（`tax = floor(subTotal * 0.08)`）。`total = subTotal + tax` の外税モデル。
- パスワードは確認用に平文。本番運用ではハッシュ化を前提とした桁数 `varchar(512)`。
- 商品マスタ管理・従業員マスタ管理は **ver2.0** で追加予定（`isDeleted` / `isManager` 列は将来用）。

### クーポン機能（`03_coupon.sql`）の設計方針
- **券種マスタ方式**（期間内は繰り返し利用可。使い切り管理はしない。顧客テーブルが無いため）。
- **定額・注文単位**のみ（定率・商品単位は対象外）。
- 値引きは **課税前**：`課税対象 = subTotal − discount` → `tax = floor(課税対象 × 0.08)` → `total = 課税対象 + tax`。
- `Sales.subTotal` は**値引き前グロスのまま**保持し、`discount` を別列でスナップショット保存。
  これにより「商品別売上（明細合計）＝`SUM(subTotal)`」と「総売上＝`SUM(total)`」が両立し、閉店処理の集計が破綻しない。
- 1注文1クーポン（`Sales.couponNo` 単一FK、NULL可）。`CHECK(discount <= subTotal)` で過値引きを防止。

詳細仕様・画面設計は `../docs/画面一覧表.md` および `../docs/画面遷移図.md` を参照。
