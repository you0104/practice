-- ============================================================
-- はるみコーヒー販売支援システム ver1.0  テストデータ
-- DBMS : IBM DB2 10.4   /   DB名 : harumi
-- 01_ddl.sql を実行してテーブルを作成した後に実行してください。
-- ============================================================
--
-- 【ポイント】
--  ・Item / Coffee / Sales の主キーは自動採番(GENERATED ALWAYS)のため、
--    値を指定せずINSERTします。空テーブルへ本ファイルの順で投入すると、
--    itemNo=100,101,102 / Coffee updateNo=1000.. / Sales salesNo=1000.. が
--    決定的に採番されるので、SalesDetail からその番号を参照できます。
--  ・本データは「ある1営業日」を時系列で再現したシナリオです。
--    閉店処理画面(SC06)の当日売上集計・コーヒー管理画面(SC05)の残量確認を
--    そのまま動作確認できます。
--  ・パスワードは確認用に平文にしています。本番ではハッシュ化してください。
-- ============================================================

-- ------------------------------------------------------------
-- 従業員（マスタ）
--   E001:店舗マネージャ / E002,E003:レジ担当者 / E004:退職者（ログイン拒否確認用）
-- ------------------------------------------------------------
INSERT INTO Employee (empno, name, password, isManager, isRetire) VALUES
    ('E001', '田原 尉也', 'pass001', 1, 0),
    ('E002', '西山 元治', 'pass002', 0, 0),
    ('E003', '奈良 達也', 'pass003', 0, 0),
    ('E004', '退職 太郎', 'pass004', 0, 1);

-- ------------------------------------------------------------
-- 商品（マスタ）  itemNo は 100,101,102 と自動採番される
--   スモール120ml/120円, トール220ml/190円, ビッグ300ml/250円
-- ------------------------------------------------------------
INSERT INTO Item (name, price, coffeeAmount, isDeleted) VALUES
    ('スモール', 120, 120, 0),
    ('トール',   190, 220, 0),
    ('ビッグ',   250, 300, 0);

-- 販売停止商品の表示確認用（isDeleted=1 → 注文受付画面には出さない）。itemNo=103
INSERT INTO Item (name, price, coffeeAmount, isDeleted) VALUES
    ('旧スモール(販売停止)', 150, 120, 1);

-- ============================================================
-- 当日シナリオ（時系列）
--   [1] 開店時 E002 がタンクに 30,000ml 補充
--   [2] 注文#1000 : スモール×2 + トール×1  → コーヒー 460ml 消費
--   [3] 注文#1001 : ビッグ×1               → コーヒー 300ml 消費
--   [4] オペミスで E003 が 500ml 削減
--   最終残量 = 30000 - 460 - 300 - 500 = 28,740 ml
-- ============================================================

-- [1] 開店時の補充（Coffee updateNo=1000）
INSERT INTO Coffee (currentCapacity, empno) VALUES (30000, 'E002');

-- [2] 注文#1000（Sales salesNo=1000）
--     小計=120*2+190=430, 消費税=floor(430*0.08)=34, 総計=464, 支払=500（つり36）
INSERT INTO Sales (empno, subTotal, tax, total, payment) VALUES
    ('E002', 430, 34, 464, 500);
INSERT INTO SalesDetail (salesNo, itemNo, price, quantity, coffeeAmount) VALUES
    (1000, 100, 120, 2, 120),   -- スモール×2
    (1000, 101, 190, 1, 220);   -- トール×1
-- 注文確定に伴うコーヒー残量更新（Coffee updateNo=1001）: 30000 - 460 = 29540
INSERT INTO Coffee (currentCapacity, empno) VALUES (29540, 'E002');

-- [3] 注文#1001（Sales salesNo=1001）
--     小計=250, 消費税=floor(250*0.08)=20, 総計=270, 支払=300（つり30）
INSERT INTO Sales (empno, subTotal, tax, total, payment) VALUES
    ('E002', 250, 20, 270, 300);
INSERT INTO SalesDetail (salesNo, itemNo, price, quantity, coffeeAmount) VALUES
    (1001, 102, 250, 1, 300);   -- ビッグ×1
-- 注文確定に伴うコーヒー残量更新（Coffee updateNo=1002）: 29540 - 300 = 29240
INSERT INTO Coffee (currentCapacity, empno) VALUES (29240, 'E002');

-- [4] オペレーションミスによる削減（Coffee updateNo=1003）: 29240 - 500 = 28740
INSERT INTO Coffee (currentCapacity, empno) VALUES (28740, 'E003');

-- ============================================================
-- 動作確認用クエリ（任意）
-- ============================================================
-- 最新のタンク残量（注文受付・コーヒー管理画面で使用）
--   SELECT currentCapacity FROM Coffee
--   WHERE updateNo = (SELECT MAX(updateNo) FROM Coffee);
--
-- 当日の商品別売上個数・売上金額（閉店処理画面で使用）
--   SELECT i.name, SUM(d.quantity) AS qty, SUM(d.price * d.quantity) AS amount
--   FROM SalesDetail d
--   JOIN Sales s ON d.salesNo = s.salesNo
--   JOIN Item  i ON d.itemNo  = i.itemNo
--   WHERE s.salesDate = CURRENT DATE
--   GROUP BY i.name;
--
-- 当日の総売上（税込）
--   SELECT SUM(total) AS totalSales FROM Sales WHERE salesDate = CURRENT DATE;
--
-- 注文受付画面に表示する販売可能な商品
--   SELECT itemNo, name, price, coffeeAmount FROM Item WHERE isDeleted = 0 ORDER BY itemNo;
