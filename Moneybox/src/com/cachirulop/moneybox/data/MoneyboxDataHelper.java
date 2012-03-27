package com.cachirulop.moneybox.data;

import com.cachirulop.moneybox.R;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MoneyboxDataHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "moneybox";
	private static final int DATABASE_VERSION = 3;

	private final Context _ctx;

	public MoneyboxDataHelper(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);

		_ctx = ctx;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] sql = _ctx.getString(R.string.SQL_oncreate)
				.split("\n");

		db.beginTransaction();
		try {
			// Create tables
			execMultipleSQL(db, sql);

			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e("Error creating tables and debug data", e.toString());
			throw e;
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("Moneybox", "Upgrading database from version " + oldVersion
				+ " to " + newVersion + ", which will destroy all old data");

		String[] sql = _ctx.getString(R.string.SQL_onUpgrade)
				.split("\n");
		
		db.beginTransaction();
		try {
			execMultipleSQL(db, sql);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e("Error upgrading tables and debug data", e.toString());
			throw e;
		} finally {
			db.endTransaction();
		}

		// This is cheating. In the real world, you'll need to add columns, not
		// rebuild from scratch.
		onCreate(db);
	}

	/**
	 * Execute all of the SQL statements in the String[] array
	 * 
	 * @param db
	 *            The database on which to execute the statements
	 * @param sql
	 *            An array of SQL statements to execute
	 */
	private void execMultipleSQL(SQLiteDatabase db, String[] sql) {
		for (String s : sql) {
			if (s.trim().length() > 0) {
				db.execSQL(s);
			}
		}
	}
}
