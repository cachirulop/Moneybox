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
import com.cachirulop.moneybox.entity.Moneybox;
import com.cachirulop.moneybox.entity.Movement;

/**
 * Manages the movements logic.
 * 
 * Access to the database to the movements table to obtain and save the values
 * of the movements.
 * 
 * @author david
 * 
 */
public class MovementsManager
{

    /**
     * Returns all the movements of the specified moneybox.
     * 
     * @param m
     *            Moneybox to obtain all the movements
     * @return List of objects of the Movement class
     */
    public static ArrayList<Movement> getAllMovements (Moneybox m)
    {
        Cursor c = null;
        SQLiteDatabase db = null;

        try {
            db = new MoneyboxDataHelper (ContextManager.getContext ()).getReadableDatabase ();

            c = db.query ("movements",
                          null,
                          "id_moneybox = ?",
                          new String[] { Long.toString (m.getIdMoneybox ()) },
                          null,
                          null,
                          "insert_date ASC");

            return createMovementList (c);
        }
        finally {
            if (c != null) {
                c.close ();
            }

            if (db != null) {
                db.close ();
            }
        }
    }

    /**
     * Delete all the movements of the specified moneybox.
     * 
     * @param m
     *            Moneybox where the movements will be deleted
     */
    public static void deleteAllMovements (Moneybox m)
    {
        SQLiteDatabase db = null;

        try {
            db = new MoneyboxDataHelper (ContextManager.getContext ()).getWritableDatabase ();

            db.delete ("movements",
                       "id_moneybox = ?",
                       new String[] { Long.toString (m.getIdMoneybox ()) });
        }
        finally {
            if (db != null) {
                db.close ();
            }
        }
    }

    /**
     * Returns the active movements of the specified moneybox.
     * 
     * An active movement is a movement that doesn't be got from the last break
     * of the moneybox or from the begining if there isn't any break.
     * 
     * @param m
     *            Moneybox to obtain the movements
     * @return A list of Movement objects
     */
    public static ArrayList<Movement> getActiveMovements (Moneybox m)
    {
        Movement lastBreak;
        Cursor c = null;
        Context ctx;
        SQLiteDatabase db = null;

        try {
            ctx = ContextManager.getContext ();
            db = new MoneyboxDataHelper (ctx).getReadableDatabase ();

            lastBreak = getLastBreakMoneybox (m);
            if (lastBreak != null) {
                c = db.rawQuery (ctx.getString (R.string.SQL_active_movements_by_date),
                                 new String[] { Long.toString (lastBreak.getInsertDateDB ()),
                                         Long.toString (m.getIdMoneybox ()) });

            }
            else {
                c = db.rawQuery (ctx.getString (R.string.SQL_active_movements),
                                 new String[] { Long.toString (m.getIdMoneybox ()) });
            }

            return createMovementList (c);
        }
        finally {
            if (c != null) {
                c.close ();
            }

            if (db != null) {
                db.close ();
            }
        }
    }

    /**
     * Return the last time when the moneybox was broken or null if the moneybox
     * is never broken.
     * 
     * @param m
     *            Moneybox to obtain the movement
     * @return The last movement when the moneybox was broken.
     */
    public static Movement getLastBreakMoneybox (Moneybox m)
    {
        Cursor c = null;
        SQLiteDatabase db = null;
        Context ctx;

        try {
            ctx = ContextManager.getContext ();
            db = new MoneyboxDataHelper (ctx).getReadableDatabase ();

            c = db.rawQuery (ctx.getString (R.string.SQL_last_break_movement),
                             new String[] { Long.toString (m.getIdMoneybox ()) });

            if (c.moveToFirst ()) {
                return createMovement (c);
            }
            else {
                return null;
            }
        }
        finally {
            if (c != null) {
                c.close ();
            }

            if (db != null) {
                db.close ();
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
    public static Movement getNextBreakMoneybox (Movement reference)
    {
        Cursor c = null;
        SQLiteDatabase db = null;
        Context ctx;

        try {
            ctx = ContextManager.getContext ();
            db = new MoneyboxDataHelper (ctx).getReadableDatabase ();

            c = db.rawQuery (ctx.getString (R.string.SQL_next_break_movement),
                             new String[] { Long.toString (reference.getInsertDateDB ()),
                                     Long.toString (reference.getIdMoneybox ()) });

            if (c.moveToFirst ()) {
                return createMovement (c);
            }
            else {
                return null;
            }
        }
        finally {
            if (c != null) {
                c.close ();
            }

            if (db != null) {
                db.close ();
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
    public static Movement getPrevBreakMoneybox (Movement reference)
    {
        Cursor c = null;
        SQLiteDatabase db = null;
        Context ctx;

        try {
            ctx = ContextManager.getContext ();
            db = new MoneyboxDataHelper (ctx).getReadableDatabase ();

            c = db.rawQuery (ctx.getString (R.string.SQL_prev_break_movement),
                             new String[] { Long.toString (reference.getInsertDateDB ()),
                                     Long.toString (reference.getIdMoneybox ()) });

            if (c.moveToFirst ()) {
                return createMovement (c);
            }
            else {
                return null;
            }
        }
        finally {
            if (c != null) {
                c.close ();
            }

            if (db != null) {
                db.close ();
            }
        }
    }

    /**
     * Read the data from the database (Cursor) and creates a list of movements
     * 
     * @param c
     *            Cursor with the database data
     * @return New list of object of Movement class.
     */
    private static ArrayList<Movement> createMovementList (Cursor c)
    {
        ArrayList<Movement> result;

        result = new ArrayList<Movement> ();

        if (c != null) {
            if (c.moveToFirst ()) {
                do {
                    result.add (createMovement (c));
                }
                while (c.moveToNext ());
            }
        }

        return result;
    }

    /**
     * Creates a new Movement object from the database data (Cursor)
     * 
     * @param c
     *            Cursor with the database data of the Movement
     * @return New Movement object with the data of the Cursor.
     */
    private static Movement createMovement (Cursor c)
    {
        Movement result;

        result = new Movement ();
        result.setIdMovement (c.getLong (c.getColumnIndex ("id_movement")));
        result.setIdMoneybox (c.getLong (c.getColumnIndex ("id_moneybox")));
        result.setBreakMoneyboxAsInt (c.getInt (c.getColumnIndex ("break_moneybox")));
        result.setDescription (c.getString (c.getColumnIndex ("description")));
        result.setInsertDate (new Date (c.getLong (c.getColumnIndex ("insert_date"))));
        if (!c.isNull (c.getColumnIndex ("get_date"))) {
            result.setGetDate (new Date (c.getLong (c.getColumnIndex ("get_date"))));
        }
        else {
            result.setGetDate (null);
        }

        if (result.isBreakMoneybox ()) {
            result.setAmount (MovementsManager.getBreakMoneyboxAmount (result));
        }
        else {
            result.setAmount (c.getDouble (c.getColumnIndex ("amount")));
        }

        return result;
    }

    /**
     * Add a moneybox movement to the database
     * 
     * @param m
     *            Movement to be added
     * @return The new movement inserted
     */
    public static Movement insertMovement (Movement m)
    {
        SQLiteDatabase db = null;

        try {
            db = new MoneyboxDataHelper (ContextManager.getContext ()).getWritableDatabase ();

            ContentValues values;

            values = new ContentValues ();
            values.put ("id_moneybox",
                        m.getIdMoneybox ());
            values.put ("amount",
                        m.getAmount ());
            values.put ("description",
                        m.getDescription ());
            values.put ("insert_date",
                        m.getInsertDateDB ());
            values.put ("get_date",
                        m.getGetDateDB ());
            values.put ("break_moneybox",
                        m.isBreakMoneyboxAsInt ());

            db.insert ("movements",
                       null,
                       values);
            
            m.setIdMovement (getLastIdMovement());
            
            return m;
        }
        finally {
            if (db != null) {
                db.close ();
            }
        }
    }

    /**
     * Add a moneybox movement to the database
     * 
     * @param m
     *            Moneybox in which the movement will be inserted
     * @param amount
     *            Amount of money to add
     * 
     * @return The new movement inserted
     */
    public static Movement insertMovement (Moneybox m,
                                           double amount)
    {
        return insertMovement (m,
                               amount,
                               null,
                               false);
    }

    /**
     * Add a moneybox movement to the database
     * 
     * @param n
     *            Moneybox in which the movement will be inserted
     * @param amount
     *            Amount of money to add
     * @param description
     *            Description of the movement
     * @param isBreakMoneybox
     *            The movement breaks the moneybox or not
     * @return The new movement inserted
     */
    public static Movement insertMovement (Moneybox m,
                                           double amount,
                                           String description,
                                           boolean isBreakMoneybox)
    {
        Movement mov;

        mov = new Movement ();
        mov.setMoneybox (m);
        mov.setAmount (amount);
        mov.setInsertDate (new Date ());
        mov.setGetDate (null);
        mov.setBreakMoneybox (isBreakMoneybox);
        if (description != null) {
            mov.setDescription (description);
        }

        return MovementsManager.insertMovement (mov);
    }
    
    /**
     * Gets the maximum identifier of the movements table
     * 
     * @return
     */
    private static long getLastIdMovement ()
    {
        return new MoneyboxDataHelper (ContextManager.getContext ()).getLastId ("movements");
    }
    

    /**
     * Saves the values of a movement in the database
     * 
     * @param m
     *            Movement to be saved
     */
    public static void updateMovement (Movement m)
    {
        SQLiteDatabase db = null;

        try {
            db = new MoneyboxDataHelper (ContextManager.getContext ()).getWritableDatabase ();

            ContentValues values;

            values = new ContentValues ();
            values.put ("id_moneybox",
                        m.getIdMoneybox ());
            values.put ("amount",
                        m.getAmount ());
            values.put ("description",
                        m.getDescription ());
            values.put ("insert_date",
                        m.getInsertDateDB ());
            values.put ("get_date",
                        m.getGetDateDB ());
            values.put ("break_moneybox",
                        m.isBreakMoneyboxAsInt ());

            db.update ("movements",
                       values,
                       "id_movement = ?",
                       new String[] { Long.toString (m.getIdMovement ()) });
        }
        finally {
            if (db != null) {
                db.close ();
            }
        }
    }

    /**
     * Delete a movement from the database
     * 
     * @param m
     *            Movement to be deleted
     */
    public static void deleteMovement (Movement m)
    {
        SQLiteDatabase db = null;

        try {
            db = new MoneyboxDataHelper (ContextManager.getContext ()).getWritableDatabase ();

            db.delete ("movements",
                       "id_movement = ?",
                       new String[] { Long.toString (m.getIdMovement ()) });
        }
        finally {
            if (db != null) {
                db.close ();
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
    public static void getMovement (Movement m)
    {
        m.setGetDate (new Date ());
        MovementsManager.updateMovement (m);
    }

    /**
     * Remove the get date of a movement to redrop in the moneybox.
     * 
     * @param m
     *            Movement to be modified
     */
    public static void dropMovement (Movement m)
    {
        m.setGetDate (null);
        MovementsManager.updateMovement (m);
    }

    /**
     * Returns the total amount in the specified moneybox
     * 
     * @param m
     *            Moneybox to get the amount
     * @return The sum of the active movements alfter the last moneybox break
     */
    public static double getTotalAmount (Moneybox m)
    {
        SQLiteDatabase db = null;
        Cursor c = null;
        Context ctx;
        Movement lastBreak;

        try {
            ctx = ContextManager.getContext ();
            db = new MoneyboxDataHelper (ctx).getReadableDatabase ();

            lastBreak = MovementsManager.getLastBreakMoneybox (m);
            if (lastBreak == null) {
                c = db.rawQuery (ctx.getString (R.string.SQL_sum_amount),
                                 new String[] { Long.toString (m.getIdMoneybox ()) });
            }
            else {
                c = db.rawQuery (ctx.getString (R.string.SQL_sum_amount_after),
                                 new String[] { Long.toString (lastBreak.getInsertDate ().getTime ()),
                                         Long.toString (m.getIdMoneybox ()) });
            }

            c.moveToFirst ();

            return c.getDouble (0);
        }
        finally {
            if (c != null) {
                c.close ();
            }

            if (db != null) {
                db.close ();
            }
        }
    }

    /**
     * Returns the total amount of a moneybox between two dates
     * 
     * @param m
     *            Moneybox to get the amount
     * @param begin
     *            Begin date of the query
     * @param end
     *            End date of the query
     * @return The amount of the active movements of the moneybox
     */
    public static double getTotalAmountByDates (Moneybox m,
                                                Date begin,
                                                Date end)
    {
        SQLiteDatabase db = null;
        Cursor c = null;
        Context ctx;

        try {
            ctx = ContextManager.getContext ();
            db = new MoneyboxDataHelper (ctx).getReadableDatabase ();

            c = db.rawQuery (ctx.getString (R.string.SQL_sum_amount_by_dates),
                             new String[] { Long.toString (begin.getTime ()),
                                     Long.toString (end.getTime ()),
                                     Long.toString (m.getIdMoneybox ()) });

            c.moveToFirst ();

            return c.getDouble (0);
        }
        finally {
            if (c != null) {
                c.close ();
            }

            if (db != null) {
                db.close ();
            }
        }
    }

    /**
     * Returns the total amount of a moneybox before a specified date
     * 
     * @param m
     *            Moneybox to obtain the data
     * @param reference
     *            Date to obtain the query
     * @return The amount of the active movements before the specified date of
     *         the selected moneybox
     */
    public static double getTotalAmountBefore (Moneybox m,
                                               Date reference)
    {
        SQLiteDatabase db = null;
        Cursor c = null;
        Context ctx;

        try {
            ctx = ContextManager.getContext ();
            db = new MoneyboxDataHelper (ctx).getReadableDatabase ();

            c = db.rawQuery (ctx.getString (R.string.SQL_sum_amount_before),
                             new String[] { Long.toString (reference.getTime ()),
                                     Long.toString (m.getIdMoneybox ()) });

            c.moveToFirst ();

            return c.getDouble (0);
        }
        finally {
            if (c != null) {
                c.close ();
            }

            if (db != null) {
                db.close ();
            }
        }
    }

    /**
     * Returns negative value of the amount between the date of the movement
     * received and the previous break moneybox movement or the initial state of
     * the moneybox.
     * 
     * @param m
     *            Movement with the date to filter the result date
     * @return Amount until the specified movement
     */
    public static double getBreakMoneyboxAmount (Movement m)
    {
        Movement prev;

        prev = getPrevBreakMoneybox (m);
        if (prev != null) {
            return -getTotalAmountByDates (m.getMoneybox (),
                                           prev.getInsertDate (),
                                           m.getInsertDate ());
        }
        else {
            return -getTotalAmountBefore (m.getMoneybox (),
                                          m.getInsertDate ());
        }
    }

    /**
     * Breaks the specified moneybox putting its amount to 0
     * 
     * @param m
     *            Moneybox to be broken
     */
    public static void breakMoneybox (Moneybox m)
    {
        MovementsManager.insertMovement (m,
                                         -1,
                                         ContextManager.getContext ().getString (R.string.break_moneybox),
                                         true);
    }

    /**
     * Returns true if the money can be taken from the moneybox. Conditions: -
     * If the movement is a break moneybox movement then it can't be got - If
     * the movement is already get movement then return false - If the insert
     * date of the movement is after the last break movement, then the money can
     * be taken.
     * 
     * @param m
     *            Movement to test
     * @return true if the money can be taken from the moneybox, false
     *         otherwise.
     */
    public static boolean canGetMovement (Movement m)
    {
        if (m.isBreakMoneybox ()) {
            return false;
        }

        if (m.getGetDate () != null) {
            return false;
        }

        return isAfterBreak (m);
    }

    /**
     * Return true if the movement can be deleted.
     * 
     * A movement can be deleted if there isn't a break moneybox after that or
     * if the movement is of the break moneybox type.
     * 
     * @param m
     *            Movement to check
     * @return True if the movement can be deleted from the database
     */
    public static boolean canDeleteMovement (Movement m)
    {
        if (m.isBreakMoneybox ()) {
            return true;
        }
        return isAfterBreak (m);
    }

    /**
     * Return true if the movement can be dropped again in the moneybox.
     * 
     * A movement can be dropped again to the moneybox if there isn't any break
     * moneybox after its insert date, and if it has a valid get date.
     * 
     * @param m
     *            Movement to check if can be dropped
     * @return True if the movement can be dropped again in the moneybox.
     */
    public static boolean canDropMovement (Movement m)
    {
        if (m.getGetDate () == null) {
            return false;
        }

        return isAfterBreak (m);
    }

    /**
     * Check if a movement date if after a break moneybox or not to know if it
     * is active and can be got, dropped or deleted.
     * 
     * @param m
     * @return
     */
    private static boolean isAfterBreak (Movement m)
    {
        Movement last;

        last = MovementsManager.getLastBreakMoneybox (m.getMoneybox ());

        return (last == null || last.getInsertDate ().before (m.getInsertDate ()));
    }
}
