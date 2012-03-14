package com.cachirulop.moneybox.manager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.CurrencyValueDef;

public class CurrencyManager {

	public static ArrayList<CurrencyValueDef> getCurrencyDef (String name, Activity parent) {
		ArrayList<CurrencyValueDef> result;
		TypedArray icons;
		TypedArray values;
		Resources res;
		
		result = new ArrayList<CurrencyValueDef>();

		res = parent.getResources();
		
		icons = res.obtainTypedArray(res.getIdentifier(name + "_money_icons", "array", parent.getPackageName()));
		values = res.obtainTypedArray(res.getIdentifier(name + "_money_values", "array", parent.getPackageName()));
		
		for (int i = 0; i < icons.length(); i++) {
			CurrencyValueDef c;
			
			c = new CurrencyValueDef();
			c.setDrawable(icons.getDrawable(i));
			c.setValue(icons.getFloat(i, 0));
		}
		
		return result;
	}
	
}
