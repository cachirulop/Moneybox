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
import android.os.Vibrator;

import com.cachirulop.moneybox.entity.CurrencyValueDef;

public class VibratorManager
{
    private static Vibrator _vibrator;

    static {
        _vibrator = (Vibrator) ContextManager.getContext ().getSystemService (Context.VIBRATOR_SERVICE);
    }

    public static void vibrateMoneyDrop (CurrencyValueDef.MoneyType type)
    {
        if (type == CurrencyValueDef.MoneyType.COIN) {
            vibrateCoinDrop ();
        }
        else {
            vibrateBillDrop ();
        }
    }

    public static void vibrateCoinDrop ()
    {
        long[] pattern = { 800,
                200,
                300,
                100,
                200,
                50 };

        _vibrator.vibrate (pattern,
                           -1);
    }

    public static void vibrateBillDrop ()
    {
        long[] pattern = { 1700,
                200 };

        _vibrator.vibrate (pattern,
                           -1);
    }

    public static void initVibrator ()
    {
        // Do nothing, the initialization is done in the static constructor.
    }
}