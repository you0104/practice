package com.ibm.eastb.harumi.dto;

/**
 * つり銭の金種DTO。
 * つり銭を金種（10000円・5000円…1円）ごとに分解した1金種分を表す。
 *
 * @author harumi development team
 * @version 1.00
 */
public class ChangeUnit {

	/** 金種（額面：10000, 5000, 1000, 500, 100, 50, 10, 5, 1） */
	private int unit;
	/** その金種の枚数 */
	private int count;

	public ChangeUnit() {
	}

	public ChangeUnit(int unit, int count) {
		this.unit = unit;
		this.count = count;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
