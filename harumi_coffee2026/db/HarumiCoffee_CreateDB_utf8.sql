-- ============================================================
-- はるみコーヒー販売支援システム DBセットアップ（UTF-8版）
--   DBMS: IBM DB2
--   添付の HarumiCoffee_CreateDB.sql を UTF-8 で文字化けしないよう整理したもの。
--   日本語の初期データ（ロール名・カテゴリ名・商品名）を正しく登録する。
-- ------------------------------------------------------------
-- 事前準備（DB2コマンドラインで実行）:
--   db2 CREATE DATABASE HARUMI USING CODESET UTF-8 TERRITORY JP
--   db2 CONNECT TO HARUMI USER db2admin USING password
-- 接続情報は ConnectionManager.java の設定と合わせること:
--   URL  = jdbc:db2://localhost:50000/harumi
--   USER = db2admin
--   PASS = password
-- ============================================================

/* Drop Tables */
DROP TABLE Coffee;
DROP TABLE FailedAuth;
DROP TABLE SalesDetail;
DROP TABLE Sales;
DROP TABLE Employee;
DROP TABLE Item;
DROP TABLE ItemCategory;
DROP TABLE Roll;

/* Create Tables */
CREATE TABLE Coffee
(
	updateNo int NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1000, INCREMENT BY 1, NO CACHE),
	updateDate date DEFAULT CURRENT_DATE NOT NULL,
	currentCapacity int NOT NULL,
	empno varchar(60) NOT NULL,
	changeAmount int,
	PRIMARY KEY (updateNo)
);

CREATE TABLE Employee
(
	empno varchar(60) NOT NULL,
	name varchar(150) NOT NULL,
	password varchar(512) NOT NULL,
	isManager int DEFAULT 0 NOT NULL,
	isRetire int DEFAULT 0 NOT NULL,
	rollId int NOT NULL,
	PRIMARY KEY (empno)
);

CREATE TABLE FailedAuth
(
	empno varchar(60) NOT NULL,
	FailedTimeStamp timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Item
(
	itemNo int NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 100, INCREMENT BY 1, NO CACHE),
	name varchar(150) NOT NULL,
	price int NOT NULL,
	coffeeAmount int NOT NULL,
	isDeleted int DEFAULT 0 NOT NULL,
	categoryId int NOT NULL,
	PRIMARY KEY (itemNo)
);

CREATE TABLE ItemCategory
(
	categoryId int NOT NULL,
	name varchar(50) NOT NULL,
	PRIMARY KEY (categoryId)
);

CREATE TABLE Roll
(
	rollId int NOT NULL,
	name varchar(50) NOT NULL,
	PRIMARY KEY (rollId)
);

CREATE TABLE Sales
(
	salesNo int NOT NULL GENERATED ALWAYS AS IDENTITY,
	empno varchar(60) NOT NULL,
	salesDate date DEFAULT CURRENT DATE NOT NULL,
	subTotal int NOT NULL,
	tax int NOT NULL,
	total int NOT NULL,
	payment int NOT NULL,
	PRIMARY KEY (salesNo)
);

CREATE TABLE SalesDetail
(
	salesNo int NOT NULL,
	itemNo int NOT NULL,
	price int NOT NULL,
	quantity int NOT NULL,
	coffeeAmount int NOT NULL,
	PRIMARY KEY (salesNo, itemNo)
);

/* Create Foreign Keys */
ALTER TABLE Coffee ADD FOREIGN KEY (empno) REFERENCES Employee (empno) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE FailedAuth ADD FOREIGN KEY (empno) REFERENCES Employee (empno) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE Sales ADD FOREIGN KEY (empno) REFERENCES Employee (empno) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE SalesDetail ADD FOREIGN KEY (itemNo) REFERENCES Item (itemNo) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE Item ADD FOREIGN KEY (categoryId) REFERENCES ItemCategory (categoryId) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE Employee ADD FOREIGN KEY (rollId) REFERENCES Roll (rollId) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE SalesDetail ADD FOREIGN KEY (salesNo) REFERENCES Sales (salesNo) ON UPDATE RESTRICT ON DELETE RESTRICT;

/* ロール */
INSERT INTO Roll (rollId, name) VALUES (1, 'バリスタ');
INSERT INTO Roll (rollId, name) VALUES (2, 'レジ担当');
INSERT INTO Roll (rollId, name) VALUES (3, '店舗責任者');
INSERT INTO Roll (rollId, name) VALUES (4, 'エリア・マネージャー');

/* 商品カテゴリー */
INSERT INTO ItemCategory (categoryId, name) VALUES (1, 'ドリンク');
INSERT INTO ItemCategory (categoryId, name) VALUES (2, 'フード');
INSERT INTO ItemCategory (categoryId, name) VALUES (3, 'セット');
INSERT INTO ItemCategory (categoryId, name) VALUES (4, 'キャンペーン');

/* 商品（スモール/トール/ビッグ。ブレンドは販売停止=削除フラグ1） */
INSERT INTO Item (name, price, coffeeAmount, isDeleted, categoryId) VALUES ('スモール', 120, 120, 0, 1);
INSERT INTO Item (name, price, coffeeAmount, isDeleted, categoryId) VALUES ('トール', 190, 220, 0, 1);
INSERT INTO Item (name, price, coffeeAmount, isDeleted, categoryId) VALUES ('ビッグ', 250, 300, 0, 1);
INSERT INTO Item (name, price, coffeeAmount, isDeleted, categoryId) VALUES ('ブレンド', 330, 400, 1, 1);

/* 従業員（テストデータ。5555は退職者でログイン不可） */
INSERT INTO Employee (empno, name, password, isManager, isRetire, rollId) VALUES ('1111', 'RegiUser', '1111', 0, 0, 2);
INSERT INTO Employee (empno, name, password, isManager, isRetire, rollId) VALUES ('2222', 'BaristaUser', '2222', 0, 0, 1);
INSERT INTO Employee (empno, name, password, isManager, isRetire, rollId) VALUES ('3333', 'ShopManager', '3333', 1, 0, 3);
INSERT INTO Employee (empno, name, password, isManager, isRetire, rollId) VALUES ('4444', 'AreaManager', '4444', 1, 0, 4);
INSERT INTO Employee (empno, name, password, isManager, isRetire, rollId) VALUES ('5555', 'RetireUser', '5555', 1, 1, 3);

/* コーヒー残量初期値（開店時のタンク量 45,000ml = 45L） */
INSERT INTO Coffee (currentCapacity, empno, changeAmount) VALUES (45000, '1111', 45000);
