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

package com.cachirulop.moneybox.entity;

import android.graphics.drawable.Drawable;

public class CurrencyValueDef
{
    private Drawable  _drawable;
    private double    _amount;
    private MoneyType _type;

    public enum MoneyType
    {
        COIN,
        BILL
    };

    public Drawable getDrawable ()
    {
        return _drawable;
    }

    public void setDrawable (Drawable _drawable)
    {
        this._drawable = _drawable;
    }

    public double getAmount ()
    {
        return _amount;
    }

    public void setAmount (double value)
    {
        this._amount = value;
    }

    public MoneyType getType ()
    {
        return _type;
    }

    public void setType (MoneyType type)
    {
        this._type = type;
    }
}
