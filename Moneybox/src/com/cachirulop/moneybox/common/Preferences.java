
package com.cachirulop.moneybox.common;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences
{

    private static final String PREFERENCES_FILE_NAME = "moneybox";

    private static final String LAST_MONEYBOX_ID      = "LAST_MONEYBOX_ID";

    /**
     * Gets the value of the last moneybox selected, saved in the shared
     * preferences
     * 
     * @param c
     *            Context to read the preferences file
     * @return The value of the LAST_MONEYBOX_ID in the shared preferences file
     */
    public static long getLastMoneyboxId (Context c)
    {
        return c.getSharedPreferences (PREFERENCES_FILE_NAME,
                                       Context.MODE_PRIVATE).getLong (LAST_MONEYBOX_ID,
                                                                      1);
    }

    /**
     * Sets the value of the last moneybox selected in the shared preferences
     * file.
     * 
     * @param c
     *            Context to write the preferences file
     * @param value
     *            New value of the LAST_MONEYBOX_ID key in the preferences file
     */
    public static void setLastMoneyboxId (Context c,
                                          long value)
    {
        SharedPreferences.Editor editor;

        editor = c.getSharedPreferences (PREFERENCES_FILE_NAME,
                                         Context.MODE_PRIVATE).edit ();
        editor.putLong (LAST_MONEYBOX_ID,
                        value);
        editor.commit ();
    }
}
