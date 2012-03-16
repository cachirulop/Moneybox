package com.cachirulop.moneybox.entity;

import android.graphics.drawable.Drawable;

public class CurrencyValueDef {
	private Drawable _drawable;
	private double _amount;
	private MoneyType _type;
	
	public enum MoneyType{
		COIN,
		BILL
	};
	
	public Drawable getDrawable() {
		return _drawable;
	}
	public void setDrawable(Drawable _drawable) {
		this._drawable = _drawable;
	}
	public double getAmount() {
		return _amount;
	}
	public void setAmount(double value) {
		this._amount = value;
	}	
	public MoneyType getType() {
		return _type;
	}
	public void setType(MoneyType type) {
		this._type = type;
	}
}
