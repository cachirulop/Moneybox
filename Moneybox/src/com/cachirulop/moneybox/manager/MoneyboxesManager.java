package com.cachirulop.moneybox.manager;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cachirulop.moneybox.data.MoneyboxDataHelper;
import com.cachirulop.moneybox.entity.Moneybox;

public class MoneyboxesManager {
    /**
     * Create a list with all the moneyboxes of the database
     * 
     * @return New ArrayList of Moneybox objects
     */
    public static ArrayList<Moneybox> getAllMoneyboxes() {
        Cursor c;
        SQLiteDatabase db = null;

        try {
            db = new MoneyboxDataHelper(ContextManager.getContext())
                    .getReadableDatabase();

            c = db.query("moneyboxes", null, null, null, null, null,
                    "description ASC");

            return createMoneyboxesList(c);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Create a list of moneyboxes from a database cursor
     * 
     * @param c
     *            Cursor with the database data
     * @return New ArrayList with the moneyboxes of the cursor
     */
    private static ArrayList<Moneybox> createMoneyboxesList(Cursor c) {
        ArrayList<Moneybox> result;

        result = new ArrayList<Moneybox>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    result.add(createMoneybox(c));
                } while (c.moveToNext());
            }
        }

        return result;
    }

    /**
     * Create new moneybox object from a database cursor
     * 
     * @param c
     *            Cursor with the data of the moneybox
     * @return New object of {@link com.cachirulop.moneybox.entity.Moneybox}
     *         class with the data of the cursor
     */
    private static Moneybox createMoneybox(Cursor c) {
        Moneybox result;

        result = new Moneybox();
        result.setIdMoneybox(c.getInt(c.getColumnIndex("id_moneybox")));
        result.setDescription(c.getString(c.getColumnIndex("description")));
        result.setCreationDate(new Date(c.getLong(c.getColumnIndex("creation_date"))));

        return result;
    }

    /**
     * Add a moneybox to the database
     * 
     * @param m
     *            Moneybox to be added
     */
    public static void insertMoneybox(Moneybox m) {
        SQLiteDatabase db = null;

        try {
            db = new MoneyboxDataHelper(ContextManager.getContext())
                    .getWritableDatabase();

            ContentValues values;

            values = new ContentValues();
            values.put("description", m.getDescription());
            values.put("creation_date", m.getCreationDateDB());

            db.insert("moneyboxes", null, values);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Add a moneybox with specified description to the database. The creation
     * date is the current date.
     * 
     * @param description
     *            Description of the moneybox
     */
    public static void insertMovement(String description) {
        Moneybox m;

        m = new Moneybox();
        m.setDescription(description);
        m.setCreationDate(new Date());

        insertMoneybox(m);
    }

    /**
     * Saves the values of a moneybox in the database
     * 
     * @param m
     *            Moneybox to be saved
     */
    public static void updateMoneybox(Moneybox m) {
        SQLiteDatabase db = null;

        try {
            db = new MoneyboxDataHelper(ContextManager.getContext())
                    .getWritableDatabase();

            ContentValues values;

            values = new ContentValues();
            values.put("description", m.getDescription());
            values.put("creation_date", m.getCreationDateDB());

            db.update("moneyboxes", values, "id_moneybox = ?",
                    new String[] { Integer.toString(m.getIdMoneybox()) });
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Delete a moneybox from the database
     * 
     * @param m
     *            Moneybox to be deleted
     */
    public static void deleteMoneybox(Moneybox m) {
        SQLiteDatabase db = null;

        try {
            db = new MoneyboxDataHelper(ContextManager.getContext())
                    .getWritableDatabase();

            db.delete("moneyboxes", "id_moneybox = ?",
                    new String[] { Integer.toString(m.getIdMoneybox()) });
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
