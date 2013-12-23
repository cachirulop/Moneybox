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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.cachirulop.moneybox.R;

/**
 * Provides the methods to access to the database.
 * 
 * @author dmagro
 * 
 */
public class MoneyboxDataHelper
        extends SQLiteOpenHelper
{
    /** Name of the database */
    private static final String DATABASE_NAME    = "moneybox";

    /** Current version */
    private static final int    DATABASE_VERSION = 8;

    /** Context where the object is created */
    private final Context       _ctx;

    /**
     * Constructor that receives the context.
     * 
     * @param ctx
     *            Context where the database object is created. Is used to get
     *            the references to the resources.
     */
    public MoneyboxDataHelper (Context ctx)
    {
        super (ctx,
               DATABASE_NAME,
               null,
               DATABASE_VERSION);

        _ctx = ctx;
    }

    /**
     * Creates the tables of the database, executing the SQL_on_create sentence
     * defined in the application resources.
     */
    @Override
    public void onCreate (SQLiteDatabase db)
    {
        String[] sql = _ctx.getString (R.string.SQL_on_create).split (";");

        db.beginTransaction ();
        try {
            // Create tables
            execMultipleSQL (db,
                             sql);

            db.setTransactionSuccessful ();
        }
        catch (SQLException e) {
            Log.e ("Error creating tables",
                   e.toString ());
            throw e;
        }
        finally {
            db.endTransaction ();
        }
    }

    /**
     * Drop the tables and recreate it calling {@link onCreate} method. To drop
     * the tables uses the SQL_on_upgrade sentences defined in the application
     * resources.
     */
    @Override
    public void onUpgrade (SQLiteDatabase db,
                           int oldVersion,
                           int newVersion)
    {
        String[] sql = _ctx.getString (R.string.SQL_on_upgrade).split (";");

        db.beginTransaction ();
        try {
            execMultipleSQL (db,
                             sql);
            db.setTransactionSuccessful ();
        }
        catch (SQLException e) {
            Log.e ("Error upgrading tables",
                   e.toString ());
            throw e;
        }
        finally {
            db.endTransaction ();
        }

        /* onCreate(db); */
    }

    /**
     * Execute all of the SQL statements in the String[] array
     * 
     * @param db
     *            The database on which to execute the statements
     * @param sql
     *            An array of SQL statements to execute
     */
    private void execMultipleSQL (SQLiteDatabase db,
                                  String[] sql)
    {
        for (String s : sql) {
            if (s.trim ().length () > 0) {
                db.execSQL (s);
            }
        }
    }

    /**
     * Copy the database file in the sd card
     */
    public static void exportDB (Context ctx)
    {
        try {
            String backup;

            backup = getBackupPath (ctx);

            Toast.makeText (ctx,
                            String.format (ctx.getString (R.string.msg_database_exporting),
                                           backup),
                            Toast.LENGTH_LONG).show ();

            copyFile (ctx,
                      ctx.getDatabasePath (DATABASE_NAME).getAbsolutePath (),
                      backup,
                      false);

            Toast.makeText (ctx,
                            ctx.getString (R.string.msg_database_exported),
                            Toast.LENGTH_LONG).show ();
        }
        catch (Exception e) {
            Toast.makeText (ctx,
                            e.getMessage (),
                            Toast.LENGTH_LONG).show ();
        }
    }

    /**
     * Import the database file located in the sd card
     */
    public static void importDB (Context ctx)
    {
        try {
            String backup;

            backup = getBackupPath (ctx);

            Toast.makeText (ctx,
                            String.format (ctx.getString (R.string.msg_database_importing),
                                           backup),
                            Toast.LENGTH_LONG).show ();

            copyFile (ctx,
                      backup,
                      ctx.getDatabasePath (DATABASE_NAME).getAbsolutePath (),
                      false);

            Toast.makeText (ctx,
                            ctx.getString (R.string.msg_database_imported),
                            Toast.LENGTH_LONG).show ();
        }
        catch (Exception e) {
            Toast.makeText (ctx,
                            e.getMessage (),
                            Toast.LENGTH_LONG).show ();
        }
    }

    /**
     * Copy source file to destination file
     * 
     * @param src
     *            Source file path
     * @param dst
     *            Destination file path
     * @param deleteSrc
     *            If true delete the source file when done
     * @throws FileNotFoundException
     *             If the source file is not found
     * @throws IOException
     *             If there is some error writing/reading file
     * @throws Exception
     *             If the source file can't be read or the destination file
     *             can't be write
     */
    private static void copyFile (Context ctx,
                                  String src,
                                  String dst,
                                  boolean deleteSrc)
        throws FileNotFoundException,
            IOException,
            Exception
    {
        FileChannel srcChannel = null;
        FileChannel dstChannel = null;
        FileInputStream srcStream = null;
        FileOutputStream dstStream = null;

        try {
            File dstFile;
            File dstParentFile;

            dstFile = new File (dst);
            dstParentFile = dstFile.getParentFile ();
            if (!dstParentFile.exists ()) {
                dstParentFile.mkdirs ();
            }

            if (dstParentFile.canWrite ()) {
                File srcFile;

                srcFile = new File (src);
                if (srcFile.exists ()) {
                    srcStream = new FileInputStream (srcFile);
                    dstStream = new FileOutputStream (dstFile);

                    srcChannel = srcStream.getChannel ();
                    dstChannel = dstStream.getChannel ();

                    dstChannel.transferFrom (srcChannel,
                                             0,
                                             srcChannel.size ());

                    if (deleteSrc) {
                        srcFile.delete ();
                    }
                }
                else {
                    throw new Exception (ctx.getString (R.string.error_cant_read_file));
                }
            }
            else {
                throw new Exception (ctx.getString (R.string.error_cant_write_file));
            }
        }
        finally {
            try {
                if (srcStream != null) {
                    srcStream.close ();
                }

                if (dstStream != null) {
                    dstStream.close ();
                }

                if (srcChannel != null) {
                    srcChannel.close ();
                }

                if (dstChannel != null) {
                    dstChannel.close ();
                }
            }
            catch (Exception e2) {
            }
        }
    }

    /**
     * Constructs the file path for the database backup
     * 
     * @return The absolute path to the database backup file
     */
    private static String getBackupPath (Context ctx)
    {
        StringBuffer backupPath;

        backupPath = new StringBuffer ();
        backupPath.append (Environment.getExternalStorageDirectory ());
        backupPath.append (File.separator);
        backupPath.append (ctx.getString (R.string.app_name));
        backupPath.append (File.separator);
        backupPath.append (DATABASE_NAME);

        return backupPath.toString ();
    }

    /**
     * Returns the last identifier value of an specified table
     * 
     * @return The last identifier of the table in the sqlite_sequence table
     */
    public long getLastId (String table)
    {
        long index = 0;
        SQLiteDatabase sdb = null;
        Cursor cursor = null;

        try {
            sdb = getReadableDatabase ();
            cursor = sdb.query ("sqlite_sequence",
                                new String[] { "seq" },
                                "name = ?",
                                new String[] { table },
                                null,
                                null,
                                null,
                                null);

            if (cursor.moveToFirst ()) {
                index = cursor.getLong (cursor.getColumnIndex ("seq"));
            }

            return index;
        }
        finally {
            if (cursor != null) {
                cursor.close ();
            }

            if (sdb != null) {
                sdb.close ();
            }
        }
    }

}
