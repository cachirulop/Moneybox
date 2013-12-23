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

package com.cachirulop.moneybox.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.CurrencyValueDef;
import com.cachirulop.moneybox.manager.CurrencyManager;

/**
 * Adapter to show money info in a spinner.
 * 
 * The adapter uses the spinner_money_list layout created like an android
 * resource.
 * 
 * It shows a text with the value of the money and a image with its
 * representation.
 * 
 * @author dmagro
 * 
 */
public class CurrencySpinnerAdapter
        extends BaseAdapter
        implements SpinnerAdapter
{
    /** List of currencies showed in the spinner */
    private final List<CurrencyValueDef> _content;

    /** Activity that shows the spinner */
    private final Activity               _activity;

    /**
     * Constructor that receives the parent activity.
     * 
     * Load the list of currencies with the method
     * {@link com.cachirulop.moneybox.manager.CurrencyManager#getCurrencyDefList()
     * CurrencyManager.getCurrencyDefList}.
     * 
     * @param activity
     *            Activity that shows the spinner.
     */
    public CurrencySpinnerAdapter (Activity activity)
    {
        super ();

        this._content = CurrencyManager.getCurrencyDefList ();
        this._activity = activity;
    }

    /**
     * Returns the number of items in the spinner.
     */
    public int getCount ()
    {
        return _content.size ();
    }

    /**
     * Returns the item in the specified position.
     */
    public CurrencyValueDef getItem (int position)
    {
        return _content.get (position);
    }

    /**
     * Returns the position of a item with the specified amount.
     * 
     * @param amount
     *            Amount of the item searched.
     * @return The position in the list of the item
     */
    public int getItemPositionByAmount (double amount)
    {
        int i;

        i = 0;
        for (CurrencyValueDef c : _content) {
            if (c.getAmount () == amount) {
                return i;
            }

            i++;
        }

        return -1;
    }

    /**
     * Returns the identifier of the item of the specified position. The value
     * returned is the position of the iten, so the items of the adapter hasn't
     * identifier.
     */
    public long getItemId (int position)
    {
        return position;
    }

    /**
     * Returns the view to be drawn inside the adapter.
     */
    public View getView (int position,
                         View convertView,
                         ViewGroup parent)
    {
        LayoutInflater inflater;
        View spinnerEntry;
        TextView amount;
        ImageView currencyImage;
        CurrencyValueDef currentEntry;

        inflater = _activity.getLayoutInflater ();
        spinnerEntry = inflater.inflate (R.layout.spinner_money_list,
                                         null);

        amount = (TextView) spinnerEntry.findViewById (R.id.txtCurrencyAmount);
        currencyImage = (ImageView) spinnerEntry.findViewById (R.id.ivCurrency);

        currentEntry = _content.get (position);

        amount.setText (CurrencyManager.formatAmount (currentEntry.getAmount ()));
        currencyImage.setImageDrawable (currentEntry.getDrawable ());

        return spinnerEntry;
    }
}
