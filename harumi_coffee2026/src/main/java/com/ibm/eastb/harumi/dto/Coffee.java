package com.ibm.eastb.harumi.dto;

/**
 * コーヒー残量DTO。
 * Coffeeテーブルの1行を表す。残量変更のたびに新しい行がINSERTされ、
 * 最新の更新番号(updateNo)の行が現在のタンク残量を表す。
 *
 * @author harumi development team
 * @version 1.00
 */
public class Coffee {

	/** 更新番号（自動採番） */
	private int updateNo;
	/** 更新日付 */
	private java.sql.Date updateDate;
	/** 現在のタンク残量(ml) */
	private int currentCapacity;
	/** 更新を行った従業員番号 */
	private String empno;
	/** 増減量(ml)（補充は正、削減・販売は負） */
	private Integer changeAmount;

	public Coffee() {
	}

	public int getUpdateNo() {
		return updateNo;
	}

	public void setUpdateNo(int updateNo) {
		this.updateNo = updateNo;
	}

	public java.sql.Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(java.sql.Date updateDate) {
		this.updateDate = updateDate;
	}

	public int getCurrentCapacity() {
		return currentCapacity;
	}

	public void setCurrentCapacity(int currentCapacity) {
		this.currentCapacity = currentCapacity;
	}

	public String getEmpno() {
		return empno;
	}

	public void setEmpno(String empno) {
		this.empno = empno;
	}

	public Integer getChangeAmount() {
		return changeAmount;
	}

	public void setChangeAmount(Integer changeAmount) {
		this.changeAmount = changeAmount;
	}

	/** EL表示用：残量をリットル換算（小数1桁）で返す */
	public double getCurrentCapacityLiter() {
		return currentCapacity / 1000.0;
	}

	/** EL表示用：残量が10L(10000ml)以下かどうか（アラート判定） */
	public boolean isLow() {
		return currentCapacity <= 10000;
	}
}
