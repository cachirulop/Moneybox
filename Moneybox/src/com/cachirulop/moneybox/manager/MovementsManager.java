package com.cachirulop.moneybox.manager;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.activity.MoneyboxActivity;
import com.cachirulop.moneybox.data.MoneyboxDataHelper;
import com.cachirulop.moneybox.entity.Movement;

public class MovementsManager {

	public static ArrayList<Movement> getMovements()
	{
		ArrayList<Movement> result;
		Cursor c;
		SQLiteDatabase db;
		
		db = new MoneyboxDataHelper(MoneyboxActivity.getContext()).getReadableDatabase();
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
	
	public static void addMovement (Movement m) {
		SQLiteDatabase db;
		
		db = new MoneyboxDataHelper(MoneyboxActivity.getContext()).getWritableDatabase();
		
		ContentValues values;
		
		values = new ContentValues();
		values.put("amount", m.getAmount());
		values.put("description", m.getDescription());
		values.put("insert_date", m.getInsertDateDB());
		
		db.insert("movements", null, values);
	}
	
    /**
     * Add a moneybox movement to the database
     * @param amount Amount of money to add
     */
    public static void addMovement (double amount) {
    	addMovement(amount, null);
    }
    
    public static void addMovement (double amount, String description) {
        Movement m;
        
        m = new Movement();
        m.setAmount(amount);
        m.setInsertDate(new Date());
        if (description != null) {
        	m.setDescription(description);
        }

        MovementsManager.addMovement(m);
    }
	
	public static double getTotalAmount () {
		SQLiteDatabase db;
		Cursor c;
		Context ctx;
		
		ctx = MoneyboxActivity.getContext();
		
		db = new MoneyboxDataHelper(ctx).getReadableDatabase();
		
		c = db.rawQuery(ctx.getString(R.string.moneyboxDatabase_SQL_movements_sumAmount), null);
		c.moveToFirst();
		
		return c.getDouble(0);
	}
	
	public static void breakMoneybox () {
		// Negative total amount!
		MovementsManager.addMovement(-MovementsManager.getTotalAmount(), 
				MoneyboxActivity.getContext().getString(R.string.break_moneybox));
	}
	
}
