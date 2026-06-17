-- ============================================================
-- はるみコーヒー販売支援システム  クーポン機能 追加DDL＆テストデータ
-- DBMS : IBM DB2 10.4   /   DB名 : harumi
-- 前提 : 01_ddl.sql → 02_testdata.sql を実行済みであること
-- ------------------------------------------------------------
-- 設計方針（確定事項）
--   1. 値引きは「課税前」… 小計から値引いた額を課税対象とし、消費税を引き直す
--   2. 1注文につきクーポンは1枚まで
--   3. クーポンは「券種マスタ」… 期間内は繰り返し利用可能（使い切り管理はしない）
--   4. 定率は扱わず「定額（◯円引き）」のみ／割引は「注文単位」
--
--   計算規約（注文受付・閉店処理で必ずこの順序を守る）
--     課税対象 = subTotal − discount        （discount は適用クーポンの値引額）
--     tax      = floor(課税対象 × 0.08)      （8%・切り捨て）
--     total    = 課税対象 + tax
--     ※ subTotal は「値引き前グロス」のまま保持し、明細合計との突合を保つ
-- ============================================================

-- ------------------------------------------------------------
-- クーポンテーブル（券種マスタ）
--   couponNo は1から自動採番。論理削除は isDeleted で判別（既存マスタと統一）。
-- ------------------------------------------------------------
CREATE TABLE Coupon
(
    couponNo       int          NOT NULL
                   GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE),  -- クーポン番号（PK）
    name           varchar(150) NOT NULL,             -- クーポン名称（例: 100円引き）
    discountAmount int          NOT NULL,             -- 値引額（円・定額）
    minSubTotal    int          DEFAULT 0 NOT NULL,   -- 利用可能な最低小計（税抜）。0なら下限なし
    validFrom      date         NOT NULL,             -- 有効期間（開始）
    validTo        date         NOT NULL,             -- 有効期間（終了）
    isDeleted      int          DEFAULT 0 NOT NULL,   -- 削除フラグ 0:有効 1:削除済み
    PRIMARY KEY (couponNo),
    CONSTRAINT chk_coupon_amount CHECK (discountAmount > 0),
    CONSTRAINT chk_coupon_period CHECK (validFrom <= validTo)
);

-- ------------------------------------------------------------
-- 売上テーブルにクーポン適用列を追加
--   couponNo : 適用したクーポン（未使用の注文は NULL）
--   discount : 実際に値引いた額のスナップショット（マスタ改定の影響を受けないため）
--   ※ 既存行（02_testdataの注文）は discount=0（DEFAULT）で整合する
-- ------------------------------------------------------------
ALTER TABLE Sales ADD COLUMN couponNo int;
ALTER TABLE Sales ADD COLUMN discount int DEFAULT 0 NOT NULL;

ALTER TABLE Sales
    ADD FOREIGN KEY (couponNo)
    REFERENCES Coupon (couponNo)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT;

-- 値引きが小計を超えない／マイナス値引きにしない（total>=0 を保証）
ALTER TABLE Sales ADD CONSTRAINT chk_sales_discount CHECK (discount >= 0 AND discount <= subTotal);

-- ============================================================
-- テストデータ
-- ============================================================

-- 券種マスタ（couponNo = 1,2,3 と自動採番）
INSERT INTO Coupon (name, discountAmount, minSubTotal, validFrom, validTo, isDeleted) VALUES
    ('100円引きクーポン', 100, 300, DATE('2026-06-01'), DATE('2026-12-31'), 0),  -- 小計300円以上で利用可
    ('50円引きクーポン',   50,   0, DATE('2026-06-01'), DATE('2026-12-31'), 0),  -- 下限なし
    ('終了済みクーポン',  200,   0, DATE('2025-01-01'), DATE('2025-12-31'), 0);  -- 期間外（利用不可の確認用）

-- ------------------------------------------------------------
-- 当日シナリオの続き（02_testdataの最終残量 28,740ml から継続）
--   [5] 注文#1002 : トール×2、100円引きクーポン(couponNo=1)を適用
--        小計     = 190 × 2 = 380（minSubTotal 300 を満たす）
--        値引      = 100
--        課税対象  = 380 − 100 = 280
--        消費税    = floor(280 × 0.08) = 22
--        総計      = 280 + 22 = 302   支払 500 → つり 198
--        コーヒー  = 220 × 2 = 440ml 消費 → 残量 28,740 − 440 = 28,300
-- ------------------------------------------------------------
INSERT INTO Sales (empno, subTotal, tax, total, payment, couponNo, discount) VALUES
    ('E002', 380, 22, 302, 500, 1, 100);              -- Sales salesNo=1002
INSERT INTO SalesDetail (salesNo, itemNo, price, quantity, coffeeAmount) VALUES
    (1002, 101, 190, 2, 220);                          -- トール×2
INSERT INTO Coffee (currentCapacity, empno) VALUES (28300, 'E002');   -- Coffee updateNo=1004

-- ============================================================
-- 動作確認用クエリ（任意）
-- ============================================================
-- 注文受付画面で提示できる「現在利用可能なクーポン」
--   SELECT couponNo, name, discountAmount, minSubTotal
--   FROM Coupon
--   WHERE isDeleted = 0
--     AND CURRENT DATE BETWEEN validFrom AND validTo;
--
-- 当日の会計サマリ（グロス／値引計／ネット）— 閉店処理での整合確認
--   SELECT SUM(subTotal) AS gross,        -- 値引き前売上（明細合計と一致）
--          SUM(discount) AS discountTotal,-- 値引き合計
--          SUM(tax)      AS taxTotal,      -- 消費税合計
--          SUM(total)    AS netSales       -- 実売上（総売上）
--   FROM Sales WHERE salesDate = CURRENT DATE;
--
-- 商品別売上（グロス）は従来どおり明細から算出でき、SUM(subTotal) と一致する
--   SELECT i.name, SUM(d.quantity) AS qty, SUM(d.price * d.quantity) AS amount
--   FROM SalesDetail d JOIN Sales s ON d.salesNo = s.salesNo
--                      JOIN Item  i ON d.itemNo  = i.itemNo
--   WHERE s.salesDate = CURRENT DATE
--   GROUP BY i.name;
