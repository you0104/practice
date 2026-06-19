package com.ibm.eastb.harumi.dto;

/**
 * 注文明細DTO。
 * 1商品の注文内容（商品情報＋注文個数）を表す。Orderが保持する。
 *
 * @author harumi development team
 * @version 1.00
 */
public class OrderDetail {

	/** 商品番号 */
	private int itemNo;
	/** 商品名 */
	private String name;
	/** 単価（税抜） */
	private int price;
	/** 1点あたりの使用コーヒー量(ml) */
	private int coffeeAmount;
	/** 注文個数 */
	private int quantity;

	public OrderDetail() {
	}

	/**
	 * 商品情報から注文明細を生成する（個数は0で初期化）。
	 * @param item 商品
	 */
	public OrderDetail(Item item) {
		this.itemNo = item.getItemNo();
		this.name = item.getName();
		this.price = item.getPrice();
		this.coffeeAmount = item.getCoffeeAmount();
		this.quantity = 0;
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/** この明細の小計金額（単価×個数） */
	public int getLineSubtotal() {
		return price * quantity;
	}

	/** この明細で使用するコーヒー量(ml)（1点あたり×個数） */
	public int getLineCoffee() {
		return coffeeAmount * quantity;
	}
}
