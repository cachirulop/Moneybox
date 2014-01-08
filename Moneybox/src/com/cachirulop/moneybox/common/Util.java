/*******************************************************************************
 *   Copyright (c) 2012 David Magro Martin.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/gpl.html
 *   
 * Contributors:
 *       David Magro Martin
 *******************************************************************************/

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
