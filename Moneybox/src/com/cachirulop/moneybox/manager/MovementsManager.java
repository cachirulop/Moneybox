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

			lastBreak = getLastBreakmoneybox();
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
	public static Movement getLastBreakmoneybox() {
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
		result.setAmount(c.getDouble(c.getColumnIndex("amount")));
		result.setDescription(c.getString(c.getColumnIndex("description")));
		result.setInsertDate(new Date(
				c.getLong(c.getColumnIndex("insert_date"))));
		if (!c.isNull(c.getColumnIndex("get_date"))) {
			result.setGetDate(new Date(c.getLong(c.getColumnIndex("get_date"))));
		} else {
			result.setGetDate(null);
		}
		result.setBreakMoneyboxAsInt(c.getInt(c
				.getColumnIndex("break_moneybox")));

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

	public static double getTotalAmount() {
		SQLiteDatabase db = null;
		Cursor c;
		Context ctx;

		try {
			ctx = ContextManager.getContext();
			db = new MoneyboxDataHelper(ctx).getReadableDatabase();

			c = db.rawQuery(ctx.getString(R.string.SQL_movements_sumAmount),
					null);
			c.moveToFirst();

			return c.getDouble(0);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public static void breakMoneybox() {
		// Negative total amount!
		MovementsManager.insertMovement(-MovementsManager.getTotalAmount(),
				ContextManager.getContext().getString(R.string.break_moneybox),
				true);
	}

}
