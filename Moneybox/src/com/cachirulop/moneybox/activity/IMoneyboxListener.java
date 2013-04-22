package com.cachirulop.moneybox.activity;

public interface IMoneyboxListener {
	void onUpdateTotal();
	void onSetTotal(double value);
	void onUpdateMoneybox();
	void onUpdateMovements();
}
