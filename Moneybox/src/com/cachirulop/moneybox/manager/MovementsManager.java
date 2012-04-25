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
package com.cachirulop.moneybox.manager;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.data.MoneyboxDataHelper;
import com.cachirulop.moneybox.entity.Movement;

public class MovementsManager {

	public static ArrayList<Movement> getAllMovements() {
		Cursor c;
		SQLiteDatabase db = null;

		try {
			db = new MoneyboxDataHelper(ContextManager.getContext())
					.getReadableDatabase();

			c = db.query("movements", null, null, null, null, null,
					"insert_date ASC");

			return createMovementList(c);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static void deleteAllMovements() {
		SQLiteDatabase db = null;

		try {
			db = new MoneyboxDataHelper(ContextManager.getContext())
					.getWritableDatabase();

			db.delete("movements", "", null);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static ArrayList<Movement> getActiveMovements() {
		Movement lastBreak;
		Cursor c;
		Context ctx;
		SQLiteDatabase db = null;

		try {
			ctx = ContextManager.getContext();
			db = new MoneyboxDataHelper(ctx).getReadableDatabase();

			lastBreak = getLastBreakMoneybox();
			if (lastBreak != null) {
				c = db.rawQuery(ctx
						.getString(R.string.SQL_active_movements_by_date),
						new String[] { Long.toString(lastBreak
								.getInsertDateDB()) });

			} else {
				c = db.rawQuery(ctx.getString(R.string.SQL_active_movements),
						null);
			}

			return createMovementList(c);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * Return the last time when the moneybox was broken or null if the moneybox
	 * is never broken.
	 * 
	 * @return The last movement when the moneybox was broken.
	 */
	public static Movement getLastBreakMoneybox() {
		Cursor c;
		SQLiteDatabase db = null;
		Context ctx;

		try {
			ctx = ContextManager.getContext();
			db = new MoneyboxDataHelper(ctx).getReadableDatabase();

			c = db.rawQuery(ctx.getString(R.string.SQL_last_break_movement),
					null);

			if (c.moveToFirst()) {
				return createMovement(c);
			} else {
				return null;
			}
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * Returns the next break moneybox movement from a date
	 * 
	 * @param reference
	 *            Date from search a break moneybox movement.
	 * @return The next break movement from the specified date
	 */
	public static Movement getNextBreakMoneybox(Movement reference) {
		Cursor c;
		SQLiteDatabase db = null;
		Context ctx;

		try {
			ctx = ContextManager.getContext();
			db = new MoneyboxDataHelper(ctx).getReadableDatabase();

			c = db.rawQuery(ctx.getString(R.string.SQL_next_break_movement),
					new String[] { Long.toString(reference.getInsertDateDB()) });

			if (c.moveToFirst()) {
				return createMovement(c);
			} else {
				return null;
			}
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	/**
	 * Returns the previous break moneybox movement from a date
	 * 
	 * @param reference
	 *            Date from search a break moneybox movement.
	 * @return The previous break movement from the specified date
	 */
	public static Movement getPrevBreakMoneybox(Movement reference) {
		Cursor c;
		SQLiteDatabase db = null;
		Context ctx;

		try {
			ctx = ContextManager.getContext();
			db = new MoneyboxDataHelper(ctx).getReadableDatabase();

			c = db.rawQuery(ctx.getString(R.string.SQL_prev_break_movement),
					new String[] { Long.toString(reference.getInsertDateDB()) });

			if (c.moveToFirst()) {
				return createMovement(c);
			} else {
				return null;
			}
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	private static ArrayList<Movement> createMovementList(Cursor c) {
		ArrayList<Movement> result;

		result = new ArrayList<Movement>();

		if (c != null) {
			if (c.moveToFirst()) {
				do {
					result.add(createMovement(c));
				} while (c.moveToNext());
			}
		}

		return result;
	}

	private static Movement createMovement(Cursor c) {
		Movement result;

		result = new Movement();
		result.setIdMovement(c.getInt(c.getColumnIndex("id_movement")));
		result.setBreakMoneyboxAsInt(c.getInt(c.getColumnIndex("break_moneybox")));
		result.setDescription(c.getString(c.getColumnIndex("description")));
		result.setInsertDate(new Date(
				c.getLong(c.getColumnIndex("insert_date"))));
		if (!c.isNull(c.getColumnIndex("get_date"))) {
			result.setGetDate(new Date(c.getLong(c.getColumnIndex("get_date"))));
		} else {
			result.setGetDate(null);
		}

		if (result.isBreakMoneybox()) {
			result.setAmount(MovementsManager.getBreakMoneyboxAmount(result));
		}
		else {
			result.setAmount(c.getDouble(c.getColumnIndex("amount")));
		}

		return result;
	}

	/**
	 * Add a moneybox movement to the database
	 * 
	 * @param m
	 *            Movement to be added
	 */
	public static void insertMovement(Movement m) {
		SQLiteDatabase db = null;

		try {
			db = new MoneyboxDataHelper(ContextManager.getContext())
					.getWritableDatabase();

			ContentValues values;

			values = new ContentValues();
			values.put("amount", m.getAmount());
			values.put("description", m.getDescription());
			values.put("insert_date", m.getInsertDateDB());
			values.put("get_date", m.getGetDateDB());
			values.put("break_moneybox", m.isBreakMoneyboxAsInt());

			db.insert("movements", null, values);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * Add a moneybox movement to the database
	 * 
	 * @param amount
	 *            Amount of money to add
	 */
	public static void insertMovement(double amount) {
		insertMovement(amount, null, false);
	}

	/**
	 * Add a moneybox movement to the database
	 * 
	 * @param amount
	 *            Amount of money to add
	 * @param description
	 *            Description of the movement
	 * @param isBreakMoneybox
	 *            The movemento breaks the moneybox or not
	 */
	public static void insertMovement(double amount, String description,
			boolean isBreakMoneybox) {
		Movement m;

		m = new Movement();
		m.setAmount(amount);
		m.setInsertDate(new Date());
		m.setGetDate(null);
		m.setBreakMoneybox(isBreakMoneybox);
		if (description != null) {
			m.setDescription(description);
		}

		MovementsManager.insertMovement(m);
	}

	/**
	 * Saves the values of a movement in the database
	 * 
	 * @param m
	 *            Movement to be saved
	 */
	public static void updateMovement(Movement m) {
		SQLiteDatabase db = null;

		try {
			db = new MoneyboxDataHelper(ContextManager.getContext())
					.getWritableDatabase();

			ContentValues values;

			values = new ContentValues();
			values.put("amount", m.getAmount());
			values.put("description", m.getDescription());
			values.put("insert_date", m.getInsertDateDB());
			values.put("get_date", m.getGetDateDB());
			values.put("break_moneybox", m.isBreakMoneyboxAsInt());

			db.update("movements", values, "id_movement = ?",
					new String[] { Integer.toString(m.getIdMovement()) });
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * Delete a movement from the database
	 * 
	 * @param m
	 *            Movement to be deleted
	 */
	public static void deleteMovement(Movement m) {
		SQLiteDatabase db = null;

		try {
			db = new MoneyboxDataHelper(ContextManager.getContext())
					.getWritableDatabase();

			db.delete("movements", "id_movement = ?",
					new String[] { Integer.toString(m.getIdMovement()) });
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * Gets a movement from the moneybox. Initialize the get date field with the
	 * current date and saves the movement to the database.
	 * 
	 * @param m
	 *            Movement to be modified
	 */
	public static void getMovement(Movement m) {
		m.setGetDate(new Date());
		MovementsManager.updateMovement(m);
	}

	public static double getTotalAmount() {
		SQLiteDatabase db = null;
		Cursor c;
		Context ctx;
		Movement lastBreak;

		try {
			ctx = ContextManager.getContext();
			db = new MoneyboxDataHelper(ctx).getReadableDatabase();

			lastBreak = MovementsManager.getLastBreakMoneybox();
			if (lastBreak == null) {
				c = db.rawQuery(ctx.getString(R.string.SQL_movements_sumAmount),
						null);
			} else {
				c = db.rawQuery(ctx.getString(R.string.SQL_movements_sumAmount_after),
						new String [] { Long.toString(lastBreak.getInsertDate().getTime()) });
			}

			c.moveToFirst();

			return c.getDouble(0);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	public static double getTotalAmountByDates(Date begin, Date end) {
		SQLiteDatabase db = null;
		Cursor c;
		Context ctx;

		try {
			ctx = ContextManager.getContext();
			db = new MoneyboxDataHelper(ctx).getReadableDatabase();

			c = db.rawQuery(ctx.getString(R.string.SQL_movements_sumAmount_by_dates),
					new String[] { Long.toString(begin.getTime()), 
								   Long.toString(end.getTime())});
			
			c.moveToFirst();

			return c.getDouble(0);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	public static double getTotalAmountBefore(Date reference) {
		SQLiteDatabase db = null;
		Cursor c;
		Context ctx;

		try {
			ctx = ContextManager.getContext();
			db = new MoneyboxDataHelper(ctx).getReadableDatabase();

			c = db.rawQuery(ctx.getString(R.string.SQL_movements_sumAmount_before),
					new String[] { Long.toString(reference.getTime())});
			
			c.moveToFirst();

			return c.getDouble(0);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}	
	
	/**
	 * Returns negative value of the amount between the date of the movement
	 * received and the previous break moneybox movement or the initial state
	 * of the moneybox.
	 * 
	 * @param m
	 * @return
	 */
	public static double getBreakMoneyboxAmount (Movement m) {
		Movement prev;
		
		prev = getPrevBreakMoneybox(m);
		if (prev != null) {
			return -getTotalAmountByDates (prev.getInsertDate(), m.getInsertDate());
		}
		else {  
			return -getTotalAmountBefore(m.getInsertDate());
		}
	}

	public static void breakMoneybox() {
		MovementsManager.insertMovement(-1,
				ContextManager.getContext().getString(R.string.break_moneybox),
				true);
	}

	/**
	 * Returns true if the money can be taken from the moneybox. If the insert
	 * date of the movement is after the last break movement, then the money can
	 * be taken.
	 * 
	 * @param m
	 *            Movement to test
	 * @return true if the money can be taken from the moneybox, false
	 *         otherwise.
	 */
	public static boolean canGetMovement(Movement m) {
		Movement last;

		last = MovementsManager.getLastBreakMoneybox();

		return (last == null || last.getInsertDate().before(m.getInsertDate()));
	}
}
