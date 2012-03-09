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

	public static ArrayList<Movement> getMovements(Context ctx)
	{
		ArrayList<Movement> result;
		Cursor c;
		SQLiteDatabase db;
		
		db = new MoneyboxDataHelper(ctx).getReadableDatabase();
		result = new ArrayList<Movement>();
		
		c = db.query("movements", null, null, null, null, null, "date(insert_date)");
		if (c!= null) {
			c.moveToFirst();
			do {
				Movement current;
				
				current = new Movement ();
				current.setIdMovement(c.getInt(c.getColumnIndex("id_movement")));
				current.setAmount(c.getDouble(c.getColumnIndex("amount")));
				current.setDescription(c.getString(c.getColumnIndex("description")));
				current.setInsertDate(new Date(c.getString(c.getColumnIndex("insert_date"))));
			}
			while (c.moveToNext());
		}
		
		return result;
	}
	
	public static void addMovement (Context ctx, Movement m) {
		SQLiteDatabase db;
		
		db = new MoneyboxDataHelper(ctx).getWritableDatabase();
		
		ContentValues values;
		
		values = new ContentValues();
		values.put("amount", m.getAmount());
		values.put("description", m.getDescription());
		values.put("insert_date", m.getInsertDateDB());
		
		db.insert("movements", null, values);
	}
	
	public static double getTotalAmount (Context ctx) {
		SQLiteDatabase db;
		Cursor c;
		
		db = new MoneyboxDataHelper(ctx).getReadableDatabase();
		
		c = db.rawQuery(ctx.getString(R.string.moneyboxDatabase_SQL_movements_sumAmount), null);
		c.moveToFirst();
		
		return c.getDouble(0);
	}
}
