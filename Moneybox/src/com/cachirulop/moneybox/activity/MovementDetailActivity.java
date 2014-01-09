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

package com.cachirulop.moneybox.activity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.adapter.CurrencySpinnerAdapter;
import com.cachirulop.moneybox.entity.CurrencyValueDef;
import com.cachirulop.moneybox.entity.Movement;
import com.cachirulop.moneybox.manager.MovementsManager;

/**
 * Activity to edit a moneybox movement.
 * 
 * In this window the user can change the fields of a movement and can get money
 * from the moneybox. Also
 * 
 * @author david
 * 
 */
public class MovementDetailActivity
        extends Activity
{

    /** Movement loaded in the window */
    private Movement        _movement;

    /** Result value when the user push the get button */
    public static final int RESULT_GET_MOVEMENT    = Activity.RESULT_FIRST_USER;

    /** Result value when the user push the delete button */
    public static final int RESULT_DELETE_MOVEMENT = Activity.RESULT_FIRST_USER + 1;

    /** Result value when the user push the drop again button */
    public static final int RESULT_DROP_MOVEMENT   = Activity.RESULT_FIRST_USER + 2;

    /**
     * Creates the activity. Load the data of the spinner with the available
     * money and load the data of the movement in the controls. Also initialize
     * the status of the buttons depending on the type of the movement.
     */
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.movement_detail);

        loadSpinner ();
        initData ();
        createActionBar ();
        initForm ();
    }

    /**
     * Initialize the activity action bar
     */
    private void createActionBar ()
    {
        final ActionBar actionBar = getActionBar ();

        actionBar.setNavigationMode (ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayHomeAsUpEnabled (true);
        actionBar.setHomeButtonEnabled (true);
    }

    /**
     * Initialize the status of the fields in the form.
     */
    private void initForm ()
    {
        setVisibleGetDate (!(_movement.isBreakMoneybox () || _movement.getGetDate () == null));
        setVisibleAmount (!_movement.isBreakMoneybox ());
    }

    /**
     * Load the menu from the movement_detail.xml file.
     * 
     * Activates and deactivates the put and drop menu buttons depending of the
     * movement.
     * 
     * @param menu
     *            Menu to be inflated with the menu inflater.
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.movement_detail,
                                    menu);

        MenuItem item;

        item = menu.findItem (R.id.action_get_from_moneybox);
        item.setVisible (MovementsManager.canGetMovement (_movement));
        item.setEnabled (item.isVisible ());

        item = menu.findItem (R.id.action_drop_to_moneybox);
        item.setVisible (MovementsManager.canDropMovement (_movement));
        item.setEnabled (item.isVisible ());

        item = menu.findItem (R.id.action_delete_movement);
        item.setVisible (MovementsManager.canDeleteMovement (_movement));
        item.setEnabled (item.isVisible ());

        return true;
    }

    /**
     * Load the spinner with the available money coins and bills.
     */
    private void loadSpinner ()
    {
        Spinner spn;

        spn = (Spinner) findViewById (R.id.sAmount);
        spn.setAdapter (new CurrencySpinnerAdapter (this));
    }

    /**
     * Load the movements data to the window fields.
     */
    private void initData ()
    {
        TextView txt;
        Spinner amount;
        int pos;

        _movement = (Movement) getIntent ().getExtras ().getSerializable ("movement");

        updateDateAndTime ();

        amount = (Spinner) findViewById (R.id.sAmount);
        pos = ((CurrencySpinnerAdapter) amount.getAdapter ()).getItemPositionByAmount (_movement.getAmount ());
        amount.setSelection (pos,
                             true);

        txt = (TextView) findViewById (R.id.txtDescription);
        txt.setText (_movement.getDescription ());
    }

    /**
     * Change the visibility of the get date fields (date and time), including
     * the title and separator.
     * 
     * @param visible
     *            Tells if the fields should be visible (true) or not (false)
     */
    private void setVisibleGetDate (boolean visible)
    {
        LinearLayout detailLayout;
        int visibility;

        if (visible) {
            visibility = View.VISIBLE;
        }
        else {
            visibility = View.GONE;
        }

        detailLayout = (LinearLayout) findViewById (R.id.llGetDate);
        detailLayout.setVisibility (visibility);
    }

    /**
     * Change the visibility of the amount fields, including the title.
     * 
     * @param visible
     *            Tells if the fields should be visible (true) or not (false)
     */
    private void setVisibleAmount (boolean visible)
    {
        TextView txt;
        Spinner spn;
        int visibility;

        if (visible) {
            visibility = View.VISIBLE;
        }
        else {
            visibility = View.GONE;
        }

        txt = (TextView) findViewById (R.id.AmountDesc);
        txt.setVisibility (visibility);

        spn = (Spinner) findViewById (R.id.sAmount);
        spn.setVisibility (visibility);
    }

    /**
     * Handler for the change insert date button
     * 
     * @param v
     *            view that launch the event
     */
    public void onChangeInsertDateClick (View v)
    {
        Calendar cal;
        DatePickerDialog dlg;
        DatePickerDialog.OnDateSetListener ld;

        cal = Calendar.getInstance ();

        ld = new DatePickerDialog.OnDateSetListener ()
        {
            public void onDateSet (DatePicker view,
                                   int year,
                                   int month,
                                   int day)
            {
                onInsertDateSet (view,
                                 year,
                                 month,
                                 day);
            }
        };

        cal.setTime (_movement.getInsertDate ());

        dlg = new DatePickerDialog (this,
                                    ld,
                                    cal.get (Calendar.YEAR),
                                    cal.get (Calendar.MONTH),
                                    cal.get (Calendar.DAY_OF_MONTH));
        dlg.show ();
    }

    /**
     * Handler for the change insert time button
     * 
     * @param v
     *            view that launch the event
     */
    public void onChangeInsertTimeClick (View v)
    {
        Calendar cal;
        TimePickerDialog dlg;
        TimePickerDialog.OnTimeSetListener lt;

        cal = Calendar.getInstance ();

        lt = new TimePickerDialog.OnTimeSetListener ()
        {
            public void onTimeSet (TimePicker view,
                                   int hour,
                                   int minute)
            {
                onInsertTimeSet (view,
                                 hour,
                                 minute);
            }
        };

        cal.setTime (_movement.getInsertDate ());

        dlg = new TimePickerDialog (this,
                                    lt,
                                    cal.get (Calendar.HOUR_OF_DAY),
                                    cal.get (Calendar.MINUTE),
                                    true);

        dlg.show ();
    }

    /**
     * Handler for the change get date button
     * 
     * @param v
     *            view that launch the event
     */
    public void onChangeGetDateClick (View v)
    {
        Calendar cal;
        DatePickerDialog dlg;
        DatePickerDialog.OnDateSetListener ld;

        cal = Calendar.getInstance ();

        ld = new DatePickerDialog.OnDateSetListener ()
        {
            public void onDateSet (DatePicker view,
                                   int year,
                                   int month,
                                   int day)
            {
                onGetDateSet (view,
                              year,
                              month,
                              day);
            }
        };

        cal.setTime (_movement.getGetDate ());

        dlg = new DatePickerDialog (this,
                                    ld,
                                    cal.get (Calendar.YEAR),
                                    cal.get (Calendar.MONTH),
                                    cal.get (Calendar.DAY_OF_MONTH));
        dlg.show ();
    }

    /**
     * Handler for the change get date button
     * 
     * @param v
     *            view that launch the event
     */
    public void onChangeGetTimeClick (View v)
    {
        Calendar cal;
        TimePickerDialog dlg;
        TimePickerDialog.OnTimeSetListener lt;

        cal = Calendar.getInstance ();

        lt = new TimePickerDialog.OnTimeSetListener ()
        {
            public void onTimeSet (TimePicker view,
                                   int hour,
                                   int minute)
            {
                onGetTimeSet (view,
                              hour,
                              minute);
            }
        };

        cal.setTime (_movement.getGetDate ());

        dlg = new TimePickerDialog (this,
                                    lt,
                                    cal.get (Calendar.HOUR_OF_DAY),
                                    cal.get (Calendar.MINUTE),
                                    true);

        dlg.show ();
    }

    /**
     * Handles the onDateSet event of the date dialog box for change the insert
     * date.
     * 
     * @param view
     *            View that launch the event
     * @param year
     *            Year selected in the dialog
     * @param month
     *            Month selected in the dialog
     * @param day
     *            Day selected in the dialog
     */
    public void onInsertDateSet (DatePicker view,
                                 int year,
                                 int month,
                                 int day)
    {
        Calendar cal;

        cal = Calendar.getInstance ();

        cal.setTime (_movement.getInsertDate ());
        cal.set (Calendar.YEAR,
                 year);
        cal.set (Calendar.MONTH,
                 month);
        cal.set (Calendar.DAY_OF_MONTH,
                 day);

        if (validateInsertDate (cal.getTime ())) {
            _movement.setInsertDate (cal.getTime ());

            updateInsertDate ();
        }
    }

    /**
     * Handles the onDateSet event of the date dialog box for change the insert
     * time.
     * 
     * @param view
     *            View that launch the event
     * @param hour
     *            Hour selected in the dialog
     * @param minute
     *            Minute selected in the dialog
     */
    public void onInsertTimeSet (TimePicker view,
                                 int hour,
                                 int minute)
    {
        Calendar cal;

        cal = Calendar.getInstance ();

        cal.setTime (_movement.getInsertDate ());
        cal.set (Calendar.HOUR_OF_DAY,
                 hour);
        cal.set (Calendar.MINUTE,
                 minute);

        if (validateInsertDate (cal.getTime ())) {
            _movement.setInsertDate (cal.getTime ());

            updateInsertTime ();
        }
    }

    /**
     * Handles the onDateSet event of the date dialog box for change the get
     * date.
     * 
     * @param view
     *            View that launch the event
     * @param year
     *            Year selected in the dialog
     * @param month
     *            Month selected in the dialog
     * @param day
     *            Day selected in the dialog
     */
    public void onGetDateSet (DatePicker view,
                              int year,
                              int month,
                              int day)
    {
        Calendar cal;

        cal = Calendar.getInstance ();

        cal.setTime (_movement.getGetDate ());
        cal.set (Calendar.YEAR,
                 year);
        cal.set (Calendar.MONTH,
                 month);
        cal.set (Calendar.DAY_OF_MONTH,
                 day);

        if (validateGetDate (cal.getTime ())) {
            _movement.setGetDate (cal.getTime ());

            updateGetDate ();
        }
    }

    /**
     * Handles the onDateSet event of the date dialog box for change the get
     * time.
     * 
     * @param view
     *            View that launch the event
     * @param hour
     *            Hour selected in the dialog
     * @param minute
     *            Minute selected in the dialog
     */
    public void onGetTimeSet (TimePicker view,
                              int hour,
                              int minute)
    {
        Calendar cal;

        cal = Calendar.getInstance ();

        cal.setTime (_movement.getGetDate ());
        cal.set (Calendar.HOUR_OF_DAY,
                 hour);
        cal.set (Calendar.MINUTE,
                 minute);

        if (validateGetDate (cal.getTime ())) {
            _movement.setGetDate (cal.getTime ());

            updateGetTime ();
        }
    }

    /**
     * Validate the get date and get time fields. The date and time can't be in
     * the future an the get date should be after the insert date.
     * 
     * @param newDate
     *            Date selected in the dialog
     * @return True if the date is valid, false otherwise.
     */
    public boolean validateGetDate (Date newDate)
    {
        if (newDate.after (new Date ())) {
            Toast.makeText (this,
                            R.string.error_date_incorrect_future,
                            Toast.LENGTH_LONG).show ();

            return false;
        }
        else if (newDate.before (_movement.getInsertDate ())) {
            Toast.makeText (this,
                            R.string.error_date_incorrect_before_insert,
                            Toast.LENGTH_LONG).show ();

            return false;
        }

        return true;
    }

    /**
     * Validate the insert date and insert time fields. The date and time can't
     * be in the future an the get date should be after the insert date.
     * 
     * @param newDate
     *            Date selected in the dialog
     * @return True if the date is valid, false otherwise.
     */
    public boolean validateInsertDate (Date newDate)
    {
        if (newDate.after (new Date ())) {
            Toast.makeText (this,
                            R.string.error_date_incorrect_future,
                            Toast.LENGTH_LONG).show ();

            return false;
        }
        else if (newDate.after (_movement.getInsertDate ())) {
            Toast.makeText (this,
                            R.string.error_date_incorrect_after_get,
                            Toast.LENGTH_LONG).show ();

            return false;
        }

        return true;
    }

    /**
     * Update the fields with the insert and the get time with the values of the
     * movement object.
     */
    private void updateDateAndTime ()
    {
        updateInsertDate ();
        updateInsertTime ();
        updateGetDate ();
        updateGetTime ();
    }

    /**
     * Update the get date field of the window with the value of the movement
     * object.
     */
    private void updateGetDate ()
    {
        TextView txt;

        txt = (TextView) findViewById (R.id.txtGetDate);
        if (_movement.getGetDate () != null) {
            txt.setText (DateFormat.getDateInstance (DateFormat.MEDIUM).format (_movement.getGetDate ()));
        }
        else {
            txt.setText ("");
        }
    }

    /**
     * Update the get time field of the window with the value of the movement
     * object.
     */
    private void updateGetTime ()
    {
        TextView txt;

        txt = (TextView) findViewById (R.id.txtGetTime);
        if (_movement.getGetDate () != null) {
            txt.setText (DateFormat.getTimeInstance (DateFormat.SHORT).format (_movement.getGetDate ()));
        }
        else {
            txt.setText ("");
        }
    }

    /**
     * Update the insert date field of the window with the value of the movement
     * object.
     */
    private void updateInsertDate ()
    {
        TextView txt;

        txt = (TextView) findViewById (R.id.txtDate);
        txt.setText (DateFormat.getDateInstance (DateFormat.MEDIUM).format (_movement.getInsertDate ()));
    }

    /**
     * Update the insert time field of the window with the value of the movement
     * object.
     */
    private void updateInsertTime ()
    {
        TextView txt;

        txt = (TextView) findViewById (R.id.txtTime);
        txt.setText (DateFormat.getTimeInstance (DateFormat.SHORT).format (_movement.getInsertDate ()));
    }

    /**
     * Handles the click of the save button. Copy the values of the window in
     * the movement object and save in the database using the MovementManager
     * class.
     */
    public void onSaveClick ()
    {
        TextView txt;
        Spinner amount;
        CurrencyValueDef c;

        txt = (TextView) findViewById (R.id.txtDescription);
        amount = (Spinner) findViewById (R.id.sAmount);

        c = (CurrencyValueDef) amount.getSelectedItem ();

        _movement.setAmount (c.getAmount ());
        _movement.setDescription (txt.getText ().toString ());

        MovementsManager.updateMovement (_movement);

        exitActivity (RESULT_OK);
    }

    /**
     * Handles the click of the cancel button. Only close the window.
     */
    public void onCancelClick ()
    {
        exitActivity (RESULT_CANCELED);
    }

    /**
     * Handles the click of the get button. Set the get date of the movement and
     * save it in the database using the MovementsManager class.
     */
    public void onGetClick ()
    {
        MovementsManager.getMovement (_movement);

        exitActivity (RESULT_GET_MOVEMENT);
    }

    /**
     * Handles the click of the delete button. Delete the current movement of
     * the database using the MovementsManager class.
     */
    public void onDeleteClick ()
    {
        MovementsManager.deleteMovement (_movement);

        exitActivity (RESULT_DELETE_MOVEMENT);
    }

    /**
     * Handles the click of the drop to moneybox button to reinsert a movement
     * into the moneybox. To do this it removes the get date of the movement.
     */
    public void onDropToMoneyboxClick ()
    {
        MovementsManager.dropMovement (_movement);

        exitActivity (RESULT_DROP_MOVEMENT);
    }

    /**
     * Finish the activity passing the parameter called "movement" in an extra
     * Intent object.
     * 
     * @param resultCode
     *            Result code to pass to the called activity
     */
    private void exitActivity (int resultCode)
    {
        Intent data;

        data = new Intent ();
        data.putExtra ("movement",
                       _movement);

        setResult (resultCode,
                   data);
        finish ();
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // Handle presses on the action bar items
        switch (item.getItemId ()) {
            case R.id.action_get_from_moneybox:
                onGetClick ();
                return true;

            case R.id.action_drop_to_moneybox:
                onDropToMoneyboxClick ();
                return true;

            case R.id.action_delete_movement:
                onDeleteClick ();
                return true;

            case R.id.action_save_movement:
                onSaveClick ();
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask (this);
                return true;

            default:
                return super.onOptionsItemSelected (item);
        }
    }

}
