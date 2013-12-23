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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.Moneybox;
import com.cachirulop.moneybox.entity.Movement;
import com.cachirulop.moneybox.manager.CurrencyManager;
import com.cachirulop.moneybox.manager.MovementsManager;

/**
 * Adapter to show an item inside the movements list.
 * 
 * The view of the adapter is created from the movement_row resource.
 * 
 * It shows the insert date, the get date, the amount and the description of the
 * movement.
 * 
 * @author dmagro
 * 
 */
public class MoneyboxMovementAdapter
        extends BaseAdapter
{
    /** Inflater to load the xml with the definition of the view */
    private LayoutInflater _inflater;

    /** List of movements to be showed */
    private List<Movement> _lstMovements = null;

    /** Moneybox to get the movements */
    Moneybox               _moneybox;

    /**
     * Constructor that receives the context (parent) of the adapter. Creates
     * the inflater to load the xml with the definition of the view.
     * 
     * @param context
     *            Parent of the adapter. Should be a {@link MovementsActivity
     *            MovementsActivity}.
     */
    public MoneyboxMovementAdapter (Context context,
                                    Moneybox moneybox)
    {
        _inflater = LayoutInflater.from (context);
        _moneybox = moneybox;
    }

    /**
     * Returns the moneybox of the movements to be displayed
     * 
     * @return Moneybox object of the movements
     */
    public Moneybox getMoneybox ()
    {
        return _moneybox;
    }

    /**
     * Change the moneybox to be displayed
     * 
     * @param m
     *            New moneybox
     */
    public void setMoneybox (Moneybox m)
    {
        _moneybox = m;
        refreshMovements ();
    }

    /**
     * Returns the number of items in the list of movements.
     */
    public int getCount ()
    {
        if (_lstMovements == null) {
            refreshMovements ();
        }

        return _lstMovements.size ();
    }

    /**
     * Returns the item of the specified position.
     */
    public Object getItem (int position)
    {
        if (_lstMovements == null) {
            refreshMovements ();
        }

        return _lstMovements.get (position);
    }

    /**
     * Returns the identifier of the item in the specified position. The
     * identifier is the field IdMovement of the movement.
     */
    public long getItemId (int position)
    {
        if (_lstMovements == null) {
            refreshMovements ();
        }

        return _lstMovements.get (position).getIdMovement ();
    }

    /**
     * Returns the view to be showed in a row of the list.
     * 
     * The view is created from the layout movement_row in the folder res of the
     * project.
     * 
     * It shows a text with the insert date, another one in yellow color with
     * the get date, another with the amount and other one with the description
     * of the movement.
     * 
     * If the movement is of the break moneybox type then the texts are
     * displayed in red color.
     */
    public View getView (int position,
                         View convertView,
                         ViewGroup parent)
    {
        if (convertView == null) {
            convertView = _inflater.inflate (R.layout.movement_row,
                                             null);
        }

        Movement m;
        TextView txtDate;
        TextView txtGetDate;
        TextView txtDescription;
        TextView txtAmount;

        m = _lstMovements.get (position);
        txtDate = (TextView) convertView.findViewById (R.id.txtRowMovementDate);
        txtGetDate = (TextView) convertView.findViewById (R.id.txtRowMovementGetDate);
        txtDescription = (TextView) convertView.findViewById (R.id.txtRowMovementDescription);
        txtAmount = (TextView) convertView.findViewById (R.id.txtRowMovementAmount);

        txtDate.setText (m.getInsertDateFormatted ());
        if (m.getGetDate () != null) {
            txtAmount.setPaintFlags (txtAmount.getPaintFlags () |
                                     Paint.STRIKE_THRU_TEXT_FLAG);
            txtGetDate.setVisibility (View.VISIBLE);
            txtGetDate.setText (m.getGetDateFormatted ());
        }
        else {
            txtAmount.setPaintFlags (txtAmount.getPaintFlags () &
                                     (~Paint.STRIKE_THRU_TEXT_FLAG));
            txtGetDate.setVisibility (View.GONE);
            txtGetDate.setText ("");
        }

        txtAmount.setText (CurrencyManager.formatAmount (m.getAmount ()));

        if (m.getDescription () != null &&
            !m.getDescription ().trim ().equals ("")) {
            txtDescription.setVisibility (View.VISIBLE);
            txtDescription.setText (m.getDescription ());
        }
        else {
            txtDescription.setVisibility (View.GONE);
        }

        if (m.isBreakMoneybox ()) {
            txtDate.setTextColor (Color.RED);
            txtAmount.setTextColor (Color.RED);
            txtDescription.setTextColor (Color.RED);
        }
        else {
            int blue;

            blue = Color.rgb (5,
                              143,
                              255);

            txtDate.setTextColor (blue);
            txtAmount.setTextColor (blue);
            txtDescription.setTextColor (blue);

            if (m.getGetDate () != null) {
                txtGetDate.setTextColor (Color.YELLOW);
            }
        }

        return convertView;
    }

    /**
     * Update the list of movement reading from the database with the method
     * {@link MovementsManager#getAllMovements}
     */
    public void refreshMovements ()
    {
        _lstMovements = MovementsManager.getAllMovements (_moneybox);
        notifyDataSetChanged ();
    }

}
