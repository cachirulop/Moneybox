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

package com.cachirulop.moneybox.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.activity.IMoneyboxListener;
import com.cachirulop.moneybox.common.Preferences;
import com.cachirulop.moneybox.entity.Moneybox;
import com.cachirulop.moneybox.manager.ContextManager;
import com.cachirulop.moneybox.manager.CurrencyManager;
import com.cachirulop.moneybox.manager.MoneyboxesManager;

/**
 * Class to show a moneybox information in the drawer menu.
 * 
 * It shows the description, the creation date of the moneybox and a radio
 * button to select the moneybox.
 * 
 * @author david
 * 
 */
public class MoneyboxAdapter
        extends BaseAdapter
        implements SpinnerAdapter
{
    /** Inflater to load the xml with the definition of the view */
    private LayoutInflater     _inflater;

    /** List of moneyboxes to be showed */
    private List<Moneybox>     _lstMoneyboxes    = null;

    /** Index of the selected row */
    private int                _currentPosition  = -1;

    /** Radio button object of the selected row */
    private RadioButton        _currentButton    = null;

    /** Listener for the row clicked event */
    private OnRowClickListener _rowClickListener = null;

    private IMoneyboxListener  _moneyboxListener;

    /**
     * Constructor that receives the context (parent) of the adapter. Creates
     * the inflater to load the xml with the definition of the view.
     * 
     * @param context
     *            Parent of the adapter.
     */
    public MoneyboxAdapter (Context context)
    {
        _inflater = LayoutInflater.from (context);
        _moneyboxListener = (IMoneyboxListener) context;

        readMoneyboxes ();
        setCurrentMoneyboxId (Preferences.getLastMoneyboxId (context));
    }

    /**
     * Returns the number of items in the list of moneyboxes.
     */
    public int getCount ()
    {
        if (_lstMoneyboxes == null) {
            refreshMoneyboxes ();
        }

        return _lstMoneyboxes.size ();
    }

    /**
     * Returns the item of the specified position.
     */
    public Moneybox getItem (int position)
    {
        if (_lstMoneyboxes == null) {
            refreshMoneyboxes ();
        }

        return _lstMoneyboxes.get (position);
    }

    public List<Moneybox> getItems ()
    {
        return _lstMoneyboxes;
    }

    public int getPosition ()
    {
        return _currentPosition;
    }

    /**
     * Returns the identifier of the item in the specified position. The
     * identifier is the field IdMoneybox of the moneybox object.
     */
    public long getItemId (int position)
    {
        if (_lstMoneyboxes == null) {
            refreshMoneyboxes ();
        }

        return _lstMoneyboxes.get (position).getIdMoneybox ();
    }

    public Moneybox getCurrentItem ()
    {
        return getItem (_currentPosition);
    }

    public void setCurrentMoneyboxId (long id)
    {
        if (_lstMoneyboxes == null) {
            refreshMoneyboxes ();
        }

        int i;
        boolean find;

        find = false;
        i = 0;
        while (i < _lstMoneyboxes.size () && !find) {
            Moneybox m;

            m = _lstMoneyboxes.get (i);
            if (m.getIdMoneybox () == id) {
                _currentPosition = i;

                find = true;
            }

            i++;
        }

        if (!find) {
            _currentPosition = 0;
        }

        updatePreferences ();
    }

    public void setCurrentMoneybox (Moneybox selected)
    {
        setCurrentMoneyboxId (selected.getIdMoneybox ());
    }

    public void addMoneybox (String description)
    {
        Moneybox m;

        m = MoneyboxesManager.insertMoneybox (description);

        readMoneyboxes ();
        setCurrentMoneybox (m);
        Preferences.setLastMoneyboxId (ContextManager.getContext (),
                                       m.getIdMoneybox ());

        _moneyboxListener.onUpdateMoneyboxesList ();
        notifyDataSetChanged ();
    }

    public void setMoneyboxDescription (String description)
    {
        Moneybox m;

        m = getCurrentItem ();
        m.setDescription (description);

        MoneyboxesManager.updateMoneybox (m);

        readMoneyboxes ();
        setCurrentMoneybox (m);

        _moneyboxListener.onUpdateMoneyboxesList ();
        notifyDataSetChanged ();
    }

    public double getTotalAmount ()
    {
        double result;

        result = 0.0;
        for (Moneybox m : _lstMoneyboxes) {
            result += MoneyboxesManager.getMoneyboxTotal (m);
        }

        return result;
    }

    public void deleteCurrentMoneybox ()
        throws ArrayIndexOutOfBoundsException
    {
        if (_lstMoneyboxes.size () == 1) {
            throw new ArrayIndexOutOfBoundsException (ContextManager.getContext ().getString (R.string.msg_last_moneybox));
        }

        MoneyboxesManager.deleteMoneybox (getCurrentItem ());

        readMoneyboxes ();
        setCurrentMoneybox (_lstMoneyboxes.get (0));

        _moneyboxListener.onUpdateMoneyboxesList ();
        notifyDataSetChanged ();
    }

    /**
     * Update the list of moneyboxes reading from the database with the method
     * {@link MoneyboxesManager#getAllMoneyboxes}
     */
    public void refreshMoneyboxes ()
    {
        readMoneyboxes ();
        setCurrentMoneybox (getCurrentItem ());

        notifyDataSetChanged ();
    }

    private void readMoneyboxes ()
    {
        _lstMoneyboxes = MoneyboxesManager.getAllMoneyboxes ();
    }

    private void updatePreferences ()
    {
        Preferences.setLastMoneyboxId (ContextManager.getContext (),
                                       getCurrentItem ().getIdMoneybox ());
    }

    /**
     * Returns the view to be showed in a row of the list.
     * 
     * The view is created from the layout moneybox_row in the folder res of the
     * project.
     * 
     */
    public View getView (final int position,
                         View convertView,
                         ViewGroup parent)
    {
        View view;
        ViewHolder holder;

        view = convertView;
        if (view == null) {
            view = _inflater.inflate (R.layout.moneybox_row,
                                      null);

            holder = new ViewHolder ();
            holder.btn = (RadioButton) view.findViewById (R.id.rbMoneyboxRowMoneybox);
            holder.txt = (TextView) view.findViewById (R.id.txtMoneyboxTotal);

            view.setTag (holder);
        }
        else {
            holder = (ViewHolder) view.getTag ();
        }

        holder.btn.setOnClickListener (new OnClickListener ()
        {
            public void onClick (View v)
            {
                if (position != _currentPosition && _currentButton != null) {
                    _currentButton.setChecked (false);
                }

                _currentPosition = position;
                updatePreferences ();

                _currentButton = (RadioButton) v;

                if (_rowClickListener != null) {
                    _rowClickListener.onClick (position);
                }
            }
        });

        if (_currentPosition != position) {
            holder.btn.setChecked (false);
        }
        else {
            holder.btn.setChecked (true);
            if (holder.btn != _currentButton) {
                _currentButton = holder.btn;
            }
        }

        Moneybox m;

        m = _lstMoneyboxes.get (position);

        holder.btn.setText (m.getDescription ());
        holder.btn.setTag (m);
        holder.txt.setText (CurrencyManager.formatAmount (MoneyboxesManager.getMoneyboxTotal (m)));

        return view;
    }

    public void setOnRowClickListener (OnRowClickListener listener)
    {
        _rowClickListener = listener;
    }

    public abstract static interface OnRowClickListener
    {
        public abstract void onClick (int position);
    }

    /**
     * Class to cache a row in the view
     * 
     * @author david
     */
    private class ViewHolder
    {
        RadioButton btn;
        TextView    txt;
    }
}
