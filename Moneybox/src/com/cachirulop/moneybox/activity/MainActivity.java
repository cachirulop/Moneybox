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

package com.cachirulop.moneybox.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.adapter.MoneyboxAdapter;
import com.cachirulop.moneybox.common.ConfirmDialog;
import com.cachirulop.moneybox.common.PromptDialog;
import com.cachirulop.moneybox.data.MoneyboxDataHelper;
import com.cachirulop.moneybox.entity.Moneybox;
import com.cachirulop.moneybox.entity.Movement;
import com.cachirulop.moneybox.fragment.MoneyboxFragmentAdapter;
import com.cachirulop.moneybox.manager.ContextManager;
import com.cachirulop.moneybox.manager.CurrencyManager;
import com.cachirulop.moneybox.manager.MovementsManager;
import com.cachirulop.moneybox.manager.SoundsManager;

public class MainActivity
        extends FragmentActivity
        implements ActionBar.TabListener, IMoneyboxListener
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    MoneyboxFragmentAdapter _sectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager               _viewPager;

    /**
     * Drawer menu toggle to get the events of the menu
     */
    ActionBarDrawerToggle   _drawerToggle;

    /**
     * Layout of the drawer menu
     */
    DrawerLayout            _drawerLayout;

    /**
     * Adapter for the moneyboxes list in the drawer
     */
    MoneyboxAdapter         _drawerAdapter;

    /**
     * Creates the activity
     */
    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        ContextManager.initContext (this);

        setContentView (R.layout.activity_main);

        createActionBar ();
        createDrawer ();
        createTabs ();
    }

    /**
     * Initialize the application action bar
     */
    private void createActionBar ()
    {
        final ActionBar actionBar = getActionBar ();

        actionBar.setNavigationMode (ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled (true);
        actionBar.setHomeButtonEnabled (true);
    }

    /**
     * Create left navigation drawer with the list of created moneyboxes.
     */
    private void createDrawer ()
    {
        final ListView drawerList;

        _drawerAdapter = new MoneyboxAdapter (this);
        _drawerAdapter.setOnRowClickListener (new MoneyboxAdapter.OnRowClickListener ()
        {
            public void onClick (int position)
            {
                selectMoneybox ((Moneybox) _drawerAdapter.getItem (position));
            }
        });

        // _drawerAdapter.setCurrentMoneyboxId (Preferences.getLastMoneyboxId
        // (this));
        getActionBar ().setTitle (_drawerAdapter.getCurrentItem ().getDescription ());

        drawerList = (ListView) findViewById (R.id.lvMoneyboxes);
        drawerList.setAdapter (_drawerAdapter);

        _drawerLayout = (DrawerLayout) findViewById (R.id.drawer_layout);
        _drawerToggle = new ActionBarDrawerToggle (this,
                                                   _drawerLayout,
                                                   R.drawable.ic_drawer,
                                                   R.string.drawer_open,
                                                   R.string.drawer_close)
        {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed (View view)
            {
                setTotalVisibility (true);
                getActionBar ().setTitle (_drawerAdapter.getCurrentItem ().getDescription ());
                invalidateOptionsMenu ();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened (View drawerView)
            {
                setTotalVisibility (false);
                getActionBar ().setTitle (getString (R.string.moneybox_select_moneybox));

                updateMoneyboxesTotal ();
                drawerList.smoothScrollToPosition (_drawerAdapter.getPosition ());

                invalidateOptionsMenu ();
            }
        };

        // Set the drawer toggle as the DrawerListener
        _drawerLayout.setDrawerListener (_drawerToggle);
    }

    /**
     * Create the tabs with the sections of the application
     */
    private void createTabs ()
    {
        final ActionBar actionBar = getActionBar ();

        // Create the adapter that will return a fragment for each of the
        // primary sections of the application.
        _sectionsPagerAdapter = new MoneyboxFragmentAdapter (getSupportFragmentManager ());

        // Set up the ViewPager with the sections adapter.
        _viewPager = (ViewPager) findViewById (R.id.pager);
        _viewPager.setAdapter (_sectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        _viewPager.setOnPageChangeListener (new ViewPager.SimpleOnPageChangeListener ()
        {
            @Override
            public void onPageSelected (int position)
            {
                actionBar.setSelectedNavigationItem (position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < _sectionsPagerAdapter.getCount (); i++) {
            actionBar.addTab (actionBar.newTab ().setText (_sectionsPagerAdapter.getPageTitle (i)).setTabListener (this));
        }
    }

    /**
     * Update the total amount
     */
    public void updateTotal ()
    {
        TextView total;

        total = (TextView) findViewById (R.id.txtTotal);
        total.setText (CurrencyManager.formatAmount (MovementsManager.getTotalAmount (_drawerAdapter.getCurrentItem ())));
    }

    /**
     * Set a value to the total field
     */
    public void setTotal (double val)
    {
        TextView total;

        total = (TextView) findViewById (R.id.txtTotal);
        total.setText (CurrencyManager.formatAmount (val));
    }

    public void setTotalVisibility (boolean visible)
    {
        TextView total;

        total = (TextView) findViewById (R.id.txtTotal);
        if (visible) {
            total.setVisibility (View.VISIBLE);
        }
        else {
            total.setVisibility (View.INVISIBLE);
        }
    }

    /**
     * Load the menu from the main.xml file.
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.main,
                                    menu);

        return true;
    }

    @Override
    public void onBackPressed ()
    {
        if (_viewPager.getCurrentItem () != 0) {
            _viewPager.setCurrentItem (0);
        }
        else {
            this.finish ();
        }
    }

    /**
     * Menu option selected.
     */
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // The option selected is on the drawer menu
        if (_drawerToggle.onOptionsItemSelected (item)) {
            return true;
        }

        // Handle item selection
        switch (item.getItemId ()) {
            case R.id.action_delete_all:
                confirmDeleteAll ();
                return true;

            case R.id.action_import:
                MoneyboxDataHelper.importDB (this);
                refresh (true);
                return true;

            case R.id.action_export:
                MoneyboxDataHelper.exportDB (this);
                return true;

            case R.id.action_break_moneybox:
                onHammerClicked ();
                return true;

            case R.id.action_add_moneybox:
                onAddMoneyboxClicked ();
                return true;

            case R.id.action_del_moneybox:
                onDelMoneyboxClicked ();
                return true;

            case R.id.action_edit_moneybox:
                onEditMoneyboxClicked ();
                return true;

            default:
                return super.onOptionsItemSelected (item);
        }
    }

    /**
     * Hammer is clicked, so the moneybox should be empty.
     * 
     * Shows a confirmation message with the buttons associated to the methods
     * that break the moneybox (breakMoneybox) on cancel the dialog.
     * 
     * @param v
     *            View that launch the event.
     */
    public void onHammerClicked ()
    {
        ConfirmDialog dlg;

        dlg = new ConfirmDialog (R.string.break_moneybox,
                                 R.string.break_moneybox_confirm)
        {
            @Override
            public void onOkClicked ()
            {
                breakMoneybox ();
            }
        };

        dlg.show (getSupportFragmentManager (),
                  "breakMoneybox");
    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState)
    {
        super.onPostCreate (savedInstanceState);
        _drawerToggle.syncState ();
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig)
    {
        super.onConfigurationChanged (newConfig);
        _drawerToggle.onConfigurationChanged (newConfig);
    }

    /**
     * Empty the moneybox.
     */
    protected void breakMoneybox ()
    {
        SoundsManager.playBreakingMoneyboxSound ();
        MovementsManager.breakMoneybox (_drawerAdapter.getCurrentItem ());

        refresh (true);

        updateTotal ();
    }

    /**
     * Show a dialog to confirm that the user want to delete all the movements.
     * 
     * Register the method onDeleteAll to be called if the user decide to delete
     * all the movements.
     */
    private void confirmDeleteAll ()
    {
        ConfirmDialog dlg;

        dlg = new ConfirmDialog (R.string.delete, R.string.delete_all_confirm)
        {
            @Override
            public void onOkClicked ()
            {
                onDeleteAll ();
            }
        };

        dlg.show (getSupportFragmentManager (),
                  "breakMoneybox");
    }

    /**
     * Delete all the movements in the moneybox calling the deleteAllMovements
     * method of the MovementsManager class.
     */
    private void onDeleteAll ()
    {
        MovementsManager.deleteAllMovements (_drawerAdapter.getCurrentItem ());

        refresh (true);
    }

    /**
     * Refresh the contents of the tabs
     */
    public void refresh (boolean updateDrawer)
    {
        if (updateDrawer) {
            _drawerAdapter.refreshMoneyboxes ();
        }

        getActionBar ().setTitle (_drawerAdapter.getCurrentItem ().getDescription ());
        _sectionsPagerAdapter.getMoneyboxFragment ().refresh ();
        _sectionsPagerAdapter.getMovementsFragment ().refresh ();
    }

    // @Override
    public void onTabSelected (ActionBar.Tab tab,
                               FragmentTransaction fragmentTransaction)
    {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        _viewPager.setCurrentItem (tab.getPosition ());
    }

    // @Override
    public void onTabUnselected (ActionBar.Tab tab,
                                 FragmentTransaction fragmentTransaction)
    {}

    // @Override
    public void onTabReselected (ActionBar.Tab tab,
                                 FragmentTransaction fragmentTransaction)
    {}

    public void onUpdateTotal ()
    {
        updateTotal ();
    }

    public void onSetTotal (double value)
    {
        setTotal (value);
    }

    public void onUpdateMoneybox ()
    {
        _drawerAdapter.refreshMoneyboxes ();
        _sectionsPagerAdapter.getMoneyboxFragment ().refresh ();
    }

    public void onUpdateMovements ()
    {
        _drawerAdapter.refreshMoneyboxes ();
        _sectionsPagerAdapter.getMovementsFragment ().refresh ();
    }

    public void selectMoneybox (Moneybox m)
    {
        _drawerLayout.closeDrawer (Gravity.START);

        refresh (false);
    }

    public void onAddMoneyboxClicked ()
    {
        PromptDialog dlg;

        dlg = new PromptDialog (R.string.moneybox_new_moneybox,
                                R.string.moneybox_new_moneybox_description)
        {
            @Override
            public boolean onOkClicked (String input)
            {
                _drawerAdapter.addMoneybox (input);

                return true; // true = close dialog
            }
        };

        dlg.show (getSupportFragmentManager (),
                  "addMoneybox");
    }

    public void onDelMoneyboxClicked ()
    {
        if (_drawerAdapter.getCount () == 1) {
            Toast t;

            t = Toast.makeText (this,
                                R.string.msg_last_moneybox,
                                Toast.LENGTH_LONG);
            t.show ();
        }
        else {
            ConfirmDialog dlg;

            dlg = new ConfirmDialog (R.string.moneybox_delete,
                                     R.string.moneybox_delete_confirm)
            {
                @Override
                public void onOkClicked ()
                {
                    delMoneybox ();
                }
            };

            dlg.show (getSupportFragmentManager (),
                      "deleteMoneybox");
        }
    }

    public void delMoneybox ()
    {
        _drawerAdapter.deleteCurrentMoneybox ();
    }

    public void onEditMoneyboxClicked ()
    {
        PromptDialog dlg;

        dlg = new PromptDialog (R.string.moneybox_edit_moneybox,
                                R.string.moneybox_edit_moneybox_description)
        {
            @Override
            public boolean onOkClicked (String input)
            {
                _drawerAdapter.setMoneyboxDescription (input);

                return true;
            }
        };

        dlg.show (getSupportFragmentManager (),
                  "editMoneybox");
    }

    public Moneybox getCurrentMoneybox ()
    {
        return _drawerAdapter.getCurrentItem ();
    }

    private void updateMoneyboxesTotal ()
    {
        TextView total;

        total = (TextView) findViewById (R.id.txtTotalMoneyboxes);
        total.setText (CurrencyManager.formatAmount (_drawerAdapter.getTotalAmount ()));
    }

    public void onUpdateMoneyboxesList ()
    {
        refresh (false);
    }

    public void onGetMovement (Movement m)
    {
        _sectionsPagerAdapter.getMoneyboxFragment ().getMovement (m);
    }

    public void onDeleteMovement (Movement m)
    {
        _sectionsPagerAdapter.getMoneyboxFragment ().deleteMovement (m);
    }

    public void onDropAgainMovement (Movement m)
    {
        _sectionsPagerAdapter.getMoneyboxFragment ().dropAgainMovement (m);
    }
}
