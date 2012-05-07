/*******************************************************************************
 * Copyright (c) 2012 David Magro Martin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     David Magro Martin - initial API and implementation
 ******************************************************************************/
package com.cachirulop.moneybox.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cachirulop.moneybox.R;

/**
 * Provides the methods to access to the database.
 * 
 * @author dmagro
 * 
 */
public class MoneyboxDataHelper extends SQLiteOpenHelper {
	/** Name of the database */
	private static final String DATABASE_NAME = "moneybox";

	/** Current version */
	private static final int DATABASE_VERSION = 5;

	/** Context where the object is created */
	private final Context _ctx;

	/**
	 * Constructor that receives the context.
	 * 
	 * @param ctx
	 *            Context where the database object is created. Is used to get
	 *            the references to the resources.
	 */
	public MoneyboxDataHelper(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);

		_ctx = ctx;
	}

	/**
	 * Creates the tables of the database, executing the SQL_on_create sentence
	 * defined in the application resources.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] sql = _ctx.getString(R.string.SQL_on_create).split("\n");

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

	/**
	 * Drop the tables and recreate it calling {@link onCreate} method. To drop
	 * the tables uses the SQL_on_upgrade sentences defined in the application
	 * resources.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String[] sql = _ctx.getString(R.string.SQL_on_upgrade).split("\n");

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
