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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.adapter.CurrencySpinnerAdapter;
import com.cachirulop.moneybox.entity.CurrencyValueDef;
import com.cachirulop.moneybox.entity.Movement;
import com.cachirulop.moneybox.manager.MovementsManager;

public class MovementDetailActivity extends Activity {

	// Constants to identify the dialogs
	////////////////////////////////////////////////////////
	
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
	 * Creates the activity.
	 * Load the data of the spinner with the available money and load the data
	 * of the movement in the controls. Also initialize the status of the buttons
	 * depending on the type of the movement.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movement_detail);

		loadSpinner();
		initData();
	}

	/**
	 * Launched by android when creates a new dialog.
	 * Can create this dialogs:
	 * 		- Edit the insert date field
	 * 		- Edit the insert time field
	 * 		- Edit the get date field
	 * 		- Edit the get time field
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
		pos = ((CurrencySpinnerAdapter) amount.getAdapter()).getItemPositionByAmount(_movement.getAmount());
		amount.setSelection(pos, true);
		
		txt = (TextView) findViewById(R.id.txtDescription);
		txt.setText(_movement.getDescription());
	}

	public void onChangeDateClick(View v) {
		showDialog(INSERT_DATE_DIALOG_ID);
	}

	public void onChangeTimeClick(View v) {
		showDialog(INSERT_TIME_DIALOG_ID);
	}

	public void onChangeGetDateClick(View v) {
		showDialog(GET_DATE_DIALOG_ID);
	}

	public void onChangeGetTimeClick(View v) {
		showDialog(GET_TIME_DIALOG_ID);
	}

	public void onInsertDateSet(DatePicker view, int year, int month, int day) {
		Calendar cal;

		cal = Calendar.getInstance();

		cal.setTime(_movement.getInsertDate());
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);

		_movement.setInsertDate(cal.getTime());

		updateInsertDate();
	}

	public void onInsertTimeSet(TimePicker view, int hour, int minute) {
		Calendar cal;

		cal = Calendar.getInstance();

		cal.setTime(_movement.getInsertDate());
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);

		_movement.setInsertDate(cal.getTime());

		updateInsertTime();
	}
	
	public void onGetDateSet(DatePicker view, int year, int month, int day) {
		Calendar cal;

		cal = Calendar.getInstance();

		cal.setTime(_movement.getInsertDate());
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);

		_movement.setGetDate(cal.getTime());

		updateGetDate();
	}

	public void onGetTimeSet(TimePicker view, int hour, int minute) {
		Calendar cal;

		cal = Calendar.getInstance();

		cal.setTime(_movement.getInsertDate());
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);

		_movement.setGetDate(cal.getTime());

		updateGetTime();
	}

	private void updateDateAndTime() {
		updateInsertDate();
		updateInsertTime();
		updateGetDate();
		updateGetTime();
	}
	
	private void updateGetDate() {
		TextView txt;

		txt = (TextView) findViewById(R.id.txtGetDate);
		if (_movement.getGetDate() != null) {
			txt.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(
					_movement.getGetDate()));
		}
		else {
			txt.setText("");
		}
	}
	
	private void updateGetTime() {
		TextView txt;
		
		txt = (TextView) findViewById(R.id.txtGetTime);
		if (_movement.getGetDate() != null) {
			txt.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(
					_movement.getGetDate()));
		}
		else {
			txt.setText("");
		}
	}
	
	private void updateInsertDate () {
		TextView txt;

		txt = (TextView) findViewById(R.id.txtDate);
		txt.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(
				_movement.getInsertDate()));
	}
	
	private void updateInsertTime () {
		TextView txt;
		
		txt = (TextView) findViewById(R.id.txtTime);
		txt.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(
				_movement.getInsertDate()));
	}
	

	// Button events
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

	public void onCancelClick(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}

	public void onGetClick(View v) {
		_movement.setGetDate(new Date());
		MovementsManager.updateMovement(_movement);

		setResult(RESULT_OK);
		finish();
	}

	public void onDeleteClick(View v) {
		MovementsManager.deleteMovement(_movement);

		setResult(RESULT_OK);
		finish();
	}

}
