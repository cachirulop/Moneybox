package com.cachirulop.moneybox.manager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.activity.MoneyboxActivity;
import com.cachirulop.moneybox.entity.CurrencyValueDef;
import com.cachirulop.moneybox.entity.CurrencyValueDef.MoneyType;

public class CurrencyManager {

	public static ArrayList<CurrencyValueDef> getCurrencyDef (String name) {
		ArrayList<CurrencyValueDef> result;
		TypedArray icons;
		TypedArray values;
		TypedArray types;
		Resources res;
		String packageName;
		
		result = new ArrayList<CurrencyValueDef>();

		res = MoneyboxActivity.getContext().getResources();
		packageName = MoneyboxActivity.getContext().getPackageName();
		
		icons = res.obtainTypedArray(res.getIdentifier(name + "_money_icons", "array", packageName));
		values = res.obtainTypedArray(res.getIdentifier(name + "_money_values", "array", packageName));
		types = res.obtainTypedArray(res.getIdentifier(name + "_money_types", "array", packageName));
		
		for (int i = 0; i < icons.length(); i++) {
			CurrencyValueDef c;
			
			c = new CurrencyValueDef();
			c.setDrawable(icons.getDrawable(i));
			c.setAmount(values.getFloat(i, 0));
			c.setType(getType (types.getString (i), res));
			
			result.add(c);
		}
		
		return result;
	}
	
	private static CurrencyValueDef.MoneyType getType (String type, Resources res) {
		if (res.getString(R.string.money_coin).equals(type)) {
			return MoneyType.COIN;
		}
		else if (res.getString(R.string.money_bill).equals(type)) {
			return MoneyType.BILL;
		}
		else {
			return null;
		}
	}
}
