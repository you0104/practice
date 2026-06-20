package com.ibm.eastb.harumi.dto;

/**
 * 商品別売上サマリDTO。
 * 閉店処理での「当日の商品別売上」表示に使用する。
 *
 * @author harumi development team
 * @version 1.00
 */
public class ItemSalesSummary {

	/** 商品番号 */
	private int itemNo;
	/** 商品名 */
	private String name;
	/** 当日の販売個数合計 */
	private int totalQuantity;
	/** 当日の売上金額合計（税抜） */
	private int totalSales;

	public ItemSalesSummary() {
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

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public int getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(int totalSales) {
		this.totalSales = totalSales;
	}
}
