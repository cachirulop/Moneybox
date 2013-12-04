package com.cachirulop.moneybox.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	
	private static final String LAST_MONEYBOX_ID = "LAST_MONEYBOX_ID";
	
	/**
	 * Gets the value of the last moneybox selected, saved in the shared preferences
	 * 
	 * @param a Activity to read the preferences file
	 * @return The value of the LAST_MONEYBOX_ID in the shared preferences file
	 */
	public static int getLastMoneyboxId(Activity a) {
		return a.getPreferences(Context.MODE_PRIVATE).getInt(LAST_MONEYBOX_ID, 1);
	}

	/**
	 * Sets the value of the last moneybox selected in the shared preferences file.
	 * @param a Activity to write the preferences file
	 * @param value New value of the LAST_MONEYBOX_ID key in the preferences file
	 */
	public static void setLastMoneyboxId(Activity a, int value) {
		SharedPreferences.Editor editor;
		
		editor = a.getPreferences(Context.MODE_PRIVATE).edit();
		editor.putInt(LAST_MONEYBOX_ID, value);
		editor.commit();
	}
}
