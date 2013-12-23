
package com.cachirulop.moneybox.common;

import java.text.DateFormat;
import java.util.Date;

public class Util
{

    /**
     * Format the received date to string human readable value
     * 
     * @param value
     *            Date to be formatted
     * @return String with the formatted date
     */
    public static String formatDate (Date value)
    {
        return DateFormat.getDateTimeInstance (DateFormat.MEDIUM,
                                               DateFormat.SHORT).format (value);
    }
}
