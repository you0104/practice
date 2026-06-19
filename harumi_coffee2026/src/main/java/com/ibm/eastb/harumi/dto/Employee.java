package com.ibm.eastb.harumi.dto;

/**
 * 従業員（レジ担当者）DTO。
 * Employeeテーブルの1行を表す。
 *
 * @author harumi development team
 * @version 1.00
 */
public class Employee {

	/** 従業員番号 */
	private String empno;
	/** 氏名 */
	private String name;
	/** パスワード */
	private String password;
	/** 管理者権限（1:管理者 0:一般） */
	private int isManager;
	/** 退職フラグ（1:退職 0:在職） */
	private int isRetire;
	/** ロールID */
	private int rollId;

	public Employee() {
	}

	public String getEmpno() {
		return empno;
	}

	public void setEmpno(String empno) {
		this.empno = empno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getIsManager() {
		return isManager;
	}

	public void setIsManager(int isManager) {
		this.isManager = isManager;
	}

	/** EL用：管理者かどうか */
	public boolean isManager() {
		return isManager == 1;
	}

	public int getIsRetire() {
		return isRetire;
	}

	public void setIsRetire(int isRetire) {
		this.isRetire = isRetire;
	}

	public int getRollId() {
		return rollId;
	}

	public void setRollId(int rollId) {
		this.rollId = rollId;
	}
}
