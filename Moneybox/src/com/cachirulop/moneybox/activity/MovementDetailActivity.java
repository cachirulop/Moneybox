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

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
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
public class MovementDetailActivity extends Activity {

	// Constants to identify the dialogs
	// //////////////////////////////////////////////////////

	/** Dialog to set the insert date */
	static final int INSERT_DATE_DIALOG_ID = 0;

	/** Dialog to set the insert time */
	static final int INSERT_TIME_DIALOG_ID = 1;

	/** Dialog to set the get date */
	static final int GET_DATE_DIALOG_ID = 2;

	/** Dialog to set the get time */
	static final int GET_TIME_DIALOG_ID = 3;

	/** Movement loaded in the window */
	private Movement _movement;

	/**
	 * Creates the activity. Load the data of the spinner with the available
	 * money and load the data of the movement in the controls. Also initialize
	 * the status of the buttons depending on the type of the movement.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movement_detail);

		loadSpinner();
		initData();
		initButtons();
	}

	/**
	 * Launched by android when creates a new dialog. Can create this dialogs: -
	 * Edit the insert date field - Edit the insert time field - Edit the get
	 * date field - Edit the get time field
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar cal;
		DatePickerDialog.OnDateSetListener ld;
		TimePickerDialog.OnTimeSetListener lt;

		cal = Calendar.getInstance();

		switch (id) {
		case INSERT_DATE_DIALOG_ID:
			ld = new DatePickerDialog.OnDateSetListener() {
				public void onDateSet(DatePicker view, int year, int month,
						int day) {
					onInsertDateSet(view, year, month, day);
				}
			};

			cal.setTime(_movement.getInsertDate());

			return new DatePickerDialog(this, ld, cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

		case INSERT_TIME_DIALOG_ID:
			lt = new TimePickerDialog.OnTimeSetListener() {
				public void onTimeSet(TimePicker view, int hour, int minute) {
					onInsertTimeSet(view, hour, minute);
				}
			};

			cal.setTime(_movement.getInsertDate());

			return new TimePickerDialog(this, lt,
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
					true);
		case GET_DATE_DIALOG_ID:
			ld = new DatePickerDialog.OnDateSetListener() {
				public void onDateSet(DatePicker view, int year, int month,
						int day) {
					onGetDateSet(view, year, month, day);
				}
			};

			cal.setTime(_movement.getGetDate());

			return new DatePickerDialog(this, ld, cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

		case GET_TIME_DIALOG_ID:
			lt = new TimePickerDialog.OnTimeSetListener() {
				public void onTimeSet(TimePicker view, int hour, int minute) {
					onGetTimeSet(view, hour, minute);
				}
			};

			cal.setTime(_movement.getGetDate());

			return new TimePickerDialog(this, lt,
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
					true);
		}

		return null;
	}

	/**
	 * Load the spinner with the available money coins and bills.
	 */
	private void loadSpinner() {
		Spinner spn;

		spn = (Spinner) findViewById(R.id.sAmount);
		spn.setAdapter(new CurrencySpinnerAdapter(this));
	}

	/**
	 * Load the movements data to the window fields.
	 */
	private void initData() {
		TextView txt;
		Spinner amount;
		int pos;

		_movement = (Movement) getIntent().getExtras().getSerializable(
				"movement");

		updateDateAndTime();

		amount = (Spinner) findViewById(R.id.sAmount);
		pos = ((CurrencySpinnerAdapter) amount.getAdapter())
				.getItemPositionByAmount(_movement.getAmount());
		amount.setSelection(pos, true);

		txt = (TextView) findViewById(R.id.txtDescription);
		txt.setText(_movement.getDescription());
	}

	/**
	 * Initialize the state of the buttons depending on the type of the
	 * movement.
	 * 
	 * If the movement is normal without get date, the fields with the get date
	 * (date and time) should be invisibles. If the insert date of the movement
	 * is after the last break moneybox then the delete and get button should be
	 * disabled. If the movement is a break moneybox movement, then the get date
	 * fields and the amount field should be invisibles, and the get button
	 * should be disabled.
	 */
	private void initButtons() {
		if (!_movement.isBreakMoneybox()) {
			if (_movement.getGetDate() == null) {
				setVisibleGetDate(false);
			} else {
				setEnableGetButton(false);
			}

			if (!MovementsManager.canGetMovement(_movement)) {
				setEnableGetButton(false);
				setEnableDeleteButton(false);
			}

		} else {
			setEnableGetButton(false);
			setVisibleGetDate(false);
			setVisibleAmount(false);
		}
	}

	/**
	 * Change the state (enabled/disabled) of the Get button.
	 * 
	 * @param enabled
	 *            Tells if the button should be enabled (true) or disabled
	 *            (false)
	 */
	private void setEnableGetButton(boolean enabled) {
		Button btn;

		btn = (Button) findViewById(R.id.btnGetFromMoneybox);
		btn.setEnabled(enabled);
	}

	/**
	 * Change the state (enabled/disabled) of the Delete button.
	 * 
	 * @param enabled
	 *            Tells if the button should be enabled (true) or disabled
	 *            (false)
	 */
	private void setEnableDeleteButton(boolean enabled) {
		Button btn;

		btn = (Button) findViewById(R.id.btnDelete);
		btn.setEnabled(enabled);
	}

	/**
	 * Change the visibility of the get date fields (date and time), including
	 * the title and separator.
	 * 
	 * @param visible
	 *            Tells if the fields should be visible (true) or not (false)
	 */
	private void setVisibleGetDate(boolean visible) {
		TextView txt;
		Button btn;
		View v;
		int visibility;
		RelativeLayout.LayoutParams lpParams;

		if (visible) {
			visibility = View.VISIBLE;
		} else {
			visibility = View.GONE;
		}

		txt = (TextView) findViewById(R.id.txtGetDateTitle);
		txt.setVisibility(visibility);

		v = findViewById(R.id.vGetDateTitleLine);
		v.setVisibility(visibility);

		txt = (TextView) findViewById(R.id.txtGetDateDesc);
		txt.setVisibility(visibility);

		txt = (TextView) findViewById(R.id.txtGetDate);
		txt.setVisibility(visibility);

		btn = (Button) findViewById(R.id.btnChangeGetDate);
		btn.setVisibility(visibility);

		txt = (TextView) findViewById(R.id.txtGetTimeDesc);
		txt.setVisibility(visibility);

		txt = (TextView) findViewById(R.id.txtGetTime);
		txt.setVisibility(visibility);

		btn = (Button) findViewById(R.id.btnChangeGetTime);
		btn.setVisibility(visibility);

		// Change the layout_below property of the end line
		// to display correctly
		v = findViewById(R.id.vGetDateTitleEndLine);
		lpParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
		lpParams.addRule(RelativeLayout.BELOW, R.id.btnChangeTime);
		v.setLayoutParams(lpParams);
	}

	/**
	 * Change the visibility of the amount fields, including the title.
	 * 
	 * @param visible
	 *            Tells if the fields should be visible (true) or not (false)
	 */
	private void setVisibleAmount(boolean visible) {
		TextView txt;
		Spinner spn;
		int visibility;

		if (visible) {
			visibility = View.VISIBLE;
		} else {
			visibility = View.GONE;
		}

		txt = (TextView) findViewById(R.id.AmountDesc);
		txt.setVisibility(visibility);

		spn = (Spinner) findViewById(R.id.sAmount);
		spn.setVisibility(visibility);
	}

	/**
	 * Handler for the change insert date button
	 * 
	 * @param v
	 *            view that launch the event
	 */
	public void onChangeInsertDateClick(View v) {
		showDialog(INSERT_DATE_DIALOG_ID);
	}

	/**
	 * Handler for the change insert time button
	 * 
	 * @param v
	 *            view that launch the event
	 */
	public void onChangeInsertTimeClick(View v) {
		showDialog(INSERT_TIME_DIALOG_ID);
	}

	/**
	 * Handler for the change get date button
	 * 
	 * @param v
	 *            view that launch the event
	 */
	public void onChangeGetDateClick(View v) {
		showDialog(GET_DATE_DIALOG_ID);
	}

	/**
	 * Handler for the change get date button
	 * 
	 * @param v
	 *            view that launch the event
	 */
	public void onChangeGetTimeClick(View v) {
		showDialog(GET_TIME_DIALOG_ID);
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
	public void onInsertDateSet(DatePicker view, int year, int month, int day) {
		Calendar cal;

		cal = Calendar.getInstance();

		cal.setTime(_movement.getInsertDate());
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);

		if (validateInsertDate(cal.getTime())) {
			_movement.setInsertDate(cal.getTime());

			updateInsertDate();
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
	public void onInsertTimeSet(TimePicker view, int hour, int minute) {
		Calendar cal;

		cal = Calendar.getInstance();

		cal.setTime(_movement.getInsertDate());
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);

		if (validateInsertDate(cal.getTime())) {
			_movement.setInsertDate(cal.getTime());

			updateInsertTime();
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
	public void onGetDateSet(DatePicker view, int year, int month, int day) {
		Calendar cal;

		cal = Calendar.getInstance();

		cal.setTime(_movement.getGetDate());
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);

		if (validateGetDate(cal.getTime())) {
			_movement.setGetDate(cal.getTime());

			updateGetDate();
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
	public void onGetTimeSet(TimePicker view, int hour, int minute) {
		Calendar cal;

		cal = Calendar.getInstance();

		cal.setTime(_movement.getGetDate());
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);

		if (validateGetDate(cal.getTime())) {
			_movement.setGetDate(cal.getTime());

			updateGetTime();
		}
	}
	
	public boolean validateGetDate (Date newDate) {
		if (newDate.after(new Date())) {
			Toast.makeText(this, R.string.error_date_incorrect_future,
					Toast.LENGTH_LONG).show();

			return false;
		} else if (newDate.before(_movement.getInsertDate())) {
			Toast.makeText(this, R.string.error_date_incorrect_before_insert,
					Toast.LENGTH_LONG).show();

			return false;
		}
		
		return true;
	}

	public boolean validateInsertDate (Date newDate) {
		if (newDate.after(new Date())) {
			Toast.makeText(this, R.string.error_date_incorrect_future,
					Toast.LENGTH_LONG).show();

			return false;
		} else if (newDate.after(_movement.getInsertDate())) {
			Toast.makeText(this, R.string.error_date_incorrect_after_get,
					Toast.LENGTH_LONG).show();

			return false;
		}
		
		return true;
	}


	/**
	 * Update the fields with the insert and the get time with the values of the
	 * movement object.
	 */
	private void updateDateAndTime() {
		updateInsertDate();
		updateInsertTime();
		updateGetDate();
		updateGetTime();
	}

	/**
	 * Update the get date field of the window with the value of the movement
	 * object.
	 */
	private void updateGetDate() {
		TextView txt;

		txt = (TextView) findViewById(R.id.txtGetDate);
		if (_movement.getGetDate() != null) {
			txt.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(
					_movement.getGetDate()));
		} else {
			txt.setText("");
		}
	}

	/**
	 * Update the get time field of the window with the value of the movement
	 * object.
	 */
	private void updateGetTime() {
		TextView txt;

		txt = (TextView) findViewById(R.id.txtGetTime);
		if (_movement.getGetDate() != null) {
			txt.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(
					_movement.getGetDate()));
		} else {
			txt.setText("");
		}
	}

	/**
	 * Update the insert date field of the window with the value of the movement
	 * object.
	 */
	private void updateInsertDate() {
		TextView txt;

		txt = (TextView) findViewById(R.id.txtDate);
		txt.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(
				_movement.getInsertDate()));
	}

	/**
	 * Update the insert time field of the window with the value of the movement
	 * object.
	 */
	private void updateInsertTime() {
		TextView txt;

		txt = (TextView) findViewById(R.id.txtTime);
		txt.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(
				_movement.getInsertDate()));
	}

	/**
	 * Handles the click of the save button. Copy the values of the window in
	 * the movement object and save in the database using the MovementManager
	 * class.
	 * 
	 * @param v
	 *            View that launch the event
	 */
	public void onSaveClick(View v) {
		TextView txt;
		Spinner amount;
		CurrencyValueDef c;

		txt = (TextView) findViewById(R.id.txtDescription);
		amount = (Spinner) findViewById(R.id.sAmount);

		c = (CurrencyValueDef) amount.getSelectedItem();

		_movement.setAmount(c.getAmount());
		_movement.setDescription(txt.getText().toString());

		MovementsManager.updateMovement(_movement);

		setResult(RESULT_OK);
		finish();
	}

	/**
	 * Handles the click of the cancel button. Only close the window.
	 * 
	 * @param v
	 *            View that launch the event
	 */
	public void onCancelClick(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}

	/**
	 * Handles the click of the get button. Set the get date of the movement and
	 * save it in the database using the MovementsManager class.
	 * 
	 * @param v
	 *            View that launch the event.
	 */
	public void onGetClick(View v) {
		MovementsManager.getMovement(_movement);

		setResult(RESULT_OK);
		finish();
	}

	/**
	 * Handles the click of the delete button. Delete the current movement of
	 * the database using the MovementsManager class.
	 * 
	 * @param v
	 *            View that launch the event.
	 */
	public void onDeleteClick(View v) {
		MovementsManager.deleteMovement(_movement);

		setResult(RESULT_OK);
		finish();
	}

}
