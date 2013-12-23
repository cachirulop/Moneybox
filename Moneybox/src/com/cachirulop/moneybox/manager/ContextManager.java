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

import android.content.Context;

public class ContextManager
{
    private static Context _context = null;

    public static Context getContext ()
    {
        return _context;
    }

    public static void initContext (Context ctx)
    {
        _context = ctx;
    }

}
