package com.cachirulop.moneybox.entity;

import android.graphics.drawable.Drawable;

public class CurrencyValueDef {
	private Drawable _drawable;
	private double _value;
	
	public Drawable getDrawable() {
		return _drawable;
	}
	public void setDrawable(Drawable _drawable) {
		this._drawable = _drawable;
	}
	public double getValue() {
		return _value;
	}
	public void setValue(double value) {
		this._value = value;
	}	
}
