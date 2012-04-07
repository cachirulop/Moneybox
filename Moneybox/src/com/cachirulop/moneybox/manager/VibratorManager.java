package com.cachirulop.moneybox.manager;

import com.cachirulop.moneybox.entity.CurrencyValueDef;

import android.content.Context;
import android.os.Vibrator;

public class VibratorManager {

	public static void vibrateMoneyDrop(CurrencyValueDef.MoneyType type) {
		if (type == CurrencyValueDef.MoneyType.COIN) {
			vibrateCoinDrop();
		}
		else {
			vibrateBillDrop();
		}
	}
	
	public static void vibrateCoinDrop () {
		Vibrator v;
		long [] pattern = { 800, 200, 300, 100, 200, 50 };
		
		v = (Vibrator) ContextManager.getContext().getSystemService(Context.VIBRATOR_SERVICE);
		
		v.vibrate(pattern, -1);
	}
	
	public static void vibrateBillDrop () {
		Vibrator v;
		long [] pattern = { 1700, 200 };
		
		v = (Vibrator) ContextManager.getContext().getSystemService(Context.VIBRATOR_SERVICE);
		
		v.vibrate(pattern, -1);
	}
}
