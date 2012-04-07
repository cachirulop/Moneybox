package com.cachirulop.moneybox.manager;

import android.content.Context;

public class ContextManager {
	private static Context _context = null;

	public static Context getContext () {
		return _context;
	}
	
	public static void initContext (Context ctx) {
		_context = ctx;
	}
	
}
