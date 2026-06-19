package com.ibm.eastb.harumi.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 注文情報DTO。
 * 1回の注文（複数の注文明細＋支払金額）を表す。
 * 金額計算（小計・消費税・合計・つり銭）と必要コーヒー量の算出を保持する。
 * 消費税は8％外税・端数切捨てとする。
 *
 * @author harumi development team
 * @version 1.00
 */
public class Order {

	/** 消費税率（8％外税） */
	public static final double TAX_RATE = 0.08;

	/** 注文明細のリスト（販売中の全商品分） */
	private List<OrderDetail> details = new ArrayList<>();
	/** 注文を受け付けた従業員番号 */
	private String empno;
	/** 預かり金額（支払金額） */
	private int payment;

	public Order() {
	}

	public List<OrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<OrderDetail> details) {
		this.details = details;
	}

	public String getEmpno() {
		return empno;
	}

	public void setEmpno(String empno) {
		this.empno = empno;
	}

	public int getPayment() {
		return payment;
	}

	public void setPayment(int payment) {
		this.payment = payment;
	}

	/** 小計（税抜） */
	public int getSubtotal() {
		int subtotal = 0;
		for (OrderDetail d : details) {
			subtotal += d.getLineSubtotal();
		}
		return subtotal;
	}

	/** 消費税額（8％外税・端数切捨て） */
	public int getTax() {
		return (int) Math.floor(getSubtotal() * TAX_RATE);
	}

	/** 合計（税込）＝小計＋消費税 */
	public int getTotal() {
		return getSubtotal() + getTax();
	}

	/** つり銭＝預かり金額－合計 */
	public int getChange() {
		return payment - getTotal();
	}

	/** この注文で使用するコーヒー総量(ml) */
	public int getRequiredCoffee() {
		int coffee = 0;
		for (OrderDetail d : details) {
			coffee += d.getLineCoffee();
		}
		return coffee;
	}

	/** 注文された商品が1点でもあるか（全て0個でないか） */
	public boolean hasOrder() {
		for (OrderDetail d : details) {
			if (d.getQuantity() > 0) {
				return true;
			}
		}
		return false;
	}

	/** 個数が0より大きい明細だけを返す（レシート表示用） */
	public List<OrderDetail> getOrderedDetails() {
		List<OrderDetail> ordered = new ArrayList<>();
		for (OrderDetail d : details) {
			if (d.getQuantity() > 0) {
				ordered.add(d);
			}
		}
		return ordered;
	}
}
