package com.cachirulop.moneybox.activity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.Movement;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MovementDetailActivity extends Activity implements
		DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;

	private Movement _movement;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movement_detail);

		initData();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar cal;

		cal = Calendar.getInstance();
		cal.setTime(_movement.getInsertDate());

		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, this, cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, this,
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
					true);
		}

		return null;
	}

	private void initData() {
		TextView txt;

		_movement = (Movement) getIntent().getExtras().getSerializable(
				"movement");

		updateDateAndTime();

		txt = (TextView) findViewById(R.id.txtAmount);
		txt.setText(String.format("%.2f", _movement.getAmount()));

		txt = (TextView) findViewById(R.id.txtDescription);
		txt.setText(_movement.getDescription());
	}

	public void onChangeDateClick(View v) {
		showDialog(DATE_DIALOG_ID);
	}

	public void onChangeTimeClick(View v) {
		showDialog(TIME_DIALOG_ID);
	}

	// DatePickerDialog.OnDateSetListener
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar cal;

		cal = Calendar.getInstance();

		cal.setTime(_movement.getInsertDate());
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);

		_movement.setInsertDate(cal.getTime());

		updateDateAndTime();
	}

	// TimePickerDialog.OnTimeSetListener
	public void onTimeSet(TimePicker view, int hour, int minute) {
		Calendar cal;

		cal = Calendar.getInstance();

		cal.setTime(_movement.getInsertDate());
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);

		_movement.setInsertDate(cal.getTime());

		updateDateAndTime();
	}

	private void updateDateAndTime() {
		TextView txt;

		txt = (TextView) findViewById(R.id.txtDate);
		txt.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(
				_movement.getInsertDate()));

		txt = (TextView) findViewById(R.id.txtTime);
		txt.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(
				_movement.getInsertDate()));
	}
}
