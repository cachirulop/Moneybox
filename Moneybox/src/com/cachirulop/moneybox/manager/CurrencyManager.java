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

import android.content.res.Resources;
import android.content.res.TypedArray;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.CurrencyValueDef;
import com.cachirulop.moneybox.entity.CurrencyValueDef.MoneyType;

public class CurrencyManager
{
    private static ArrayList<CurrencyValueDef> _currencyDefList = null;
    private static String                      _currencySign;

    public static ArrayList<CurrencyValueDef> getCurrencyDefList ()
    {
        return _currencyDefList;
    }

    public static void initCurrencyDefList (String name)
    {
        TypedArray icons;
        TypedArray values;
        TypedArray types;
        Resources res;
        String packageName;

        _currencyDefList = new ArrayList<CurrencyValueDef> ();

        res = ContextManager.getContext ().getResources ();
        packageName = ContextManager.getContext ().getPackageName ();

        icons = res.obtainTypedArray (res.getIdentifier (name + "_money_icons",
                                                         "array",
                                                         packageName));
        values = res.obtainTypedArray (res.getIdentifier (name +
                                                                  "_money_values",
                                                          "array",
                                                          packageName));
        types = res.obtainTypedArray (res.getIdentifier (name + "_money_types",
                                                         "array",
                                                         packageName));

        _currencySign = res.getString (res.getIdentifier (name + "_sign",
                                                          "string",
                                                          packageName));

        for (int i = 0; i < icons.length (); i++) {
            CurrencyValueDef c;

            c = new CurrencyValueDef ();
            c.setDrawable (icons.getDrawable (i));
            // c.getDrawable().setBounds(new Rect (0, 0, 90, 90));
            c.setAmount (values.getFloat (i,
                                          0));
            c.setType (getType (types.getString (i),
                                res));

            _currencyDefList.add (c);
        }
    }

    /**
     * Returns the currency definition of the specified amount
     * 
     * @param amount
     *            Value to obtain the currency definition
     * @return The currency definition of the specified amount
     */
    public static CurrencyValueDef getCurrencyDef (double amount)
    {
        for (CurrencyValueDef c : _currencyDefList) {
            if (c.getAmount () == amount) {
                return c;
            }
        }

        return null;
    }

    private static CurrencyValueDef.MoneyType getType (String type,
                                                       Resources res)
    {
        if (res.getString (R.string.money_coin).equals (type)) {
            return MoneyType.COIN;
        }
        else if (res.getString (R.string.money_bill).equals (type)) {
            return MoneyType.BILL;
        }
        else {
            return null;
        }
    }

    public static String formatAmount (double value)
    {
        return String.format ("%.2f%s",
                              value,
                              _currencySign);
    }
}
