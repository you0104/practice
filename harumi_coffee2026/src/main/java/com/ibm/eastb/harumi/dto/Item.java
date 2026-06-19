package com.ibm.eastb.harumi.dto;

/**
 * 商品DTO。
 * Itemテーブルの1行を表す。コーヒー3商品（スモール・トール・ビッグ）など。
 *
 * @author harumi development team
 * @version 1.00
 */
public class Item {

	/** 商品番号 */
	private int itemNo;
	/** 商品名 */
	private String name;
	/** 価格（税抜） */
	private int price;
	/** 1点あたりの使用コーヒー量(ml) */
	private int coffeeAmount;
	/** 削除フラグ（1:削除 0:販売中） */
	private int isDeleted;
	/** カテゴリID */
	private int categoryId;

	public Item() {
	}

	public int getItemNo() {
		return itemNo;
	}

	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getCoffeeAmount() {
		return coffeeAmount;
	}

	public void setCoffeeAmount(int coffeeAmount) {
		this.coffeeAmount = coffeeAmount;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
}
