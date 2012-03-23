
package com.cachirulop.moneybox.entity;

import java.util.Date;

public class Movement {
	
	private int _idMovement;
	private double _amount;
	private Date _insertDate;
	private String _description;
	private boolean _breakMoneybox;
	
	public int getIdMovement() {
		return _idMovement;
	}
	public void setIdMovement(int idMovement) {
		this._idMovement = idMovement;
	}
	public double getAmount() {
		return _amount;
	}
	public void setAmount(double amount) {
		this._amount = amount;
	}
	public Date getInsertDate() {
		return _insertDate;
	}
	
	public long getInsertDateDB() {
		return _insertDate.getTime() / 1000;
	}
	
	public void setInsertDate(Date insertDate) {
		this._insertDate = insertDate;
	}
	public String getDescription() {
		return _description;
	}
	public void setDescription(String description) {
		this._description = description;
	}
	public boolean isBreakMoneybox() {
		return _breakMoneybox;
	}
	public int isBreakMoneyboxAsInt() {
		if (!_breakMoneybox) {
			return 0;
		}
		else {
			return 1;
		}
	}
	public void setBreakMoneybox(boolean breakMoneybox) {
		this._breakMoneybox = breakMoneybox;
	}
	public void setBreakMoneyboxAsInt(int breakMoneybox) {
		this._breakMoneybox = (breakMoneybox != 0);
	}
	
	
}
