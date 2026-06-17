-- ============================================================
-- はるみコーヒー販売支援システム ver1.0  DDL
-- DBMS : IBM DB2 10.4   /   DB名 : harumi
-- テーブル定義書（DB設計 テーブル定義書.pdf）に基づくたたき台
-- ============================================================
--
-- 【注意：原本の表記揺れについて】
--   テーブル定義書では従業員テーブルの物理名が "Emploee"（綴り誤り）と
--   記載されていますが、外部キーの参照先は "Employee" となっており不整合です。
--   本DDLでは正しい綴り "Employee" に統一しています。
--   テーブル定義書どおり "Emploee" を使う場合は、本ファイルと
--   02_testdata.sql の Employee をすべて Emploee に置換してください。
--
-- 実行順序：親テーブル → 子テーブル（FK制約のため本ファイルの順序で実行）
-- ============================================================

-- 既存テーブルがある場合は子から削除（再実行用。初回は無視してよい）
-- DROP TABLE SalesDetail;
-- DROP TABLE Sales;
-- DROP TABLE Coffee;
-- DROP TABLE Item;
-- DROP TABLE Employee;

-- ------------------------------------------------------------
-- 1. 従業員テーブル（マスタ）
--    レジ担当者・店舗マネージャを保持。退職者も論理削除で保持。
-- ------------------------------------------------------------
CREATE TABLE Employee
(
    empno     varchar(60)  NOT NULL,                 -- 従業員ID（主キー）
    name      varchar(150) NOT NULL,                 -- 氏名
    password  varchar(512) NOT NULL,                 -- パスワード（本番はハッシュ化を推奨）
    isManager int          DEFAULT 0 NOT NULL,       -- 管理者権限 0:なし 1:あり（ver2.0で使用）
    isRetire  int          DEFAULT 0 NOT NULL,       -- 退職フラグ 0:現役 1:退職済み
    PRIMARY KEY (empno)
);

-- ------------------------------------------------------------
-- 2. 商品テーブル（マスタ）
--    商品番号は100から自動採番。販売停止は削除フラグで判別。
-- ------------------------------------------------------------
CREATE TABLE Item
(
    itemNo       int          NOT NULL
                 GENERATED ALWAYS AS IDENTITY (START WITH 100, INCREMENT BY 1, NO CACHE),  -- 商品番号（PK,自動採番100〜）
    name         varchar(150) NOT NULL,             -- 商品名
    price        int          NOT NULL,             -- 販売単価（税抜）
    coffeeAmount int          NOT NULL,             -- コーヒー使用量(ml)
    isDeleted    int          DEFAULT 0 NOT NULL,   -- 削除フラグ 0:販売中 1:削除済み
    PRIMARY KEY (itemNo)
);

-- ------------------------------------------------------------
-- 3. コーヒーテーブル（トランザクション）
--    タンク残量(ml)を履歴方式で保持。残量変更のたびに新規行をINSERTし、
--    最新残量は MAX(updateNo) の行で参照する。容量上限は45,000ml。
-- ------------------------------------------------------------
CREATE TABLE Coffee
(
    updateNo        int         NOT NULL
                    GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1, NO CACHE), -- 項番（PK,自動採番1000〜）
    updateDate      date        DEFAULT CURRENT DATE NOT NULL,  -- 変更日
    currentCapacity int         NOT NULL,                       -- 変更後のコーヒー残量(ml)
    empno           varchar(60) NOT NULL,                       -- 操作した従業員ID（FK）
    PRIMARY KEY (updateNo)
);

ALTER TABLE Coffee
    ADD FOREIGN KEY (empno)
    REFERENCES Employee (empno)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT;

-- ------------------------------------------------------------
-- 4. 売上テーブル（トランザクション）
--    注文1件 = 1行。売上番号は1000から自動採番。
-- ------------------------------------------------------------
CREATE TABLE Sales
(
    salesNo   int         NOT NULL
              GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1, NO CACHE),  -- 売上番号（PK,自動採番1000〜）
    empno     varchar(60) NOT NULL,                  -- レジ担当従業員ID（FK）
    salesDate date        DEFAULT CURRENT DATE NOT NULL,  -- 売上日
    subTotal  int         NOT NULL,                  -- 小計（税抜合計）
    tax       int         NOT NULL,                  -- 消費税（8%・切り捨て）
    total     int         NOT NULL,                  -- 総計（小計＋消費税）
    payment   int         NOT NULL,                  -- 支払額（預り金）
    PRIMARY KEY (salesNo)
);

ALTER TABLE Sales
    ADD FOREIGN KEY (empno)
    REFERENCES Employee (empno)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT;

-- ------------------------------------------------------------
-- 5. 売上明細テーブル（トランザクション）
--    注文内の商品ごとの明細。複合主キー(salesNo, itemNo)。
--    価格・コーヒー使用量はver2.0の商品変更の影響を受けないよう複製保持。
-- ------------------------------------------------------------
CREATE TABLE SalesDetail
(
    salesNo      int NOT NULL,                       -- 売上番号（PK,FK）
    itemNo       int NOT NULL,                       -- 商品番号（PK,FK）
    price        int NOT NULL,                       -- 販売時の単価（税抜）
    quantity     int NOT NULL,                       -- 個数
    coffeeAmount int NOT NULL,                        -- 販売時の商品1個あたりコーヒー使用量(ml)
    PRIMARY KEY (salesNo, itemNo)
);

ALTER TABLE SalesDetail
    ADD FOREIGN KEY (salesNo)
    REFERENCES Sales (salesNo)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT;

ALTER TABLE SalesDetail
    ADD FOREIGN KEY (itemNo)
    REFERENCES Item (itemNo)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT;
