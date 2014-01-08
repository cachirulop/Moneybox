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

package com.cachirulop.moneybox.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.manager.ContextManager;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages.
 */
public class MoneyboxFragmentAdapter
        extends FragmentPagerAdapter
{

    private final int         TAB_MONEYBOX_INDEX  = 0;
    private final int         TAB_MOVEMENTS_INDEX = 1;

    private MoneyboxFragment  _moneybox           = null;
    private MovementsFragment _movements          = null;

    public MoneyboxFragmentAdapter (FragmentManager fm)
    {
        super (fm);
    }

    @Override
    public Fragment getItem (int position)
    {
        Fragment result = null;

        switch (position) {
            case TAB_MONEYBOX_INDEX:
                result = getMoneyboxFragment ();
                break;

            case TAB_MOVEMENTS_INDEX:
                result = getMovementsFragment ();
                break;
        }

        return result;
    }

    public MoneyboxFragment getMoneyboxFragment ()
    {
        if (_moneybox == null) {
            _moneybox = new MoneyboxFragment ();
        }

        return _moneybox;
    }

    public MovementsFragment getMovementsFragment ()
    {
        if (_movements == null) {
            _movements = new MovementsFragment ();
        }

        return _movements;
    }

    @Override
    public int getCount ()
    {
        return 2;
    }

    @Override
    public CharSequence getPageTitle (int position)
    {
        switch (position) {
            case 0:
                return ContextManager.getContext ().getString (R.string.tab_title_moneybox);
            case 1:
                return ContextManager.getContext ().getString (R.string.tab_title_movements);
        }

        return null;
    }
}
