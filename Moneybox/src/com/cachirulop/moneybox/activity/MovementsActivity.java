package com.cachirulop.moneybox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.adapter.MoneyboxMovementAdapter;
import com.cachirulop.moneybox.entity.Movement;
import com.cachirulop.moneybox.manager.MovementsManager;

public class MovementsActivity extends Activity {
	static final int EDIT_MOVEMENT_REQUEST = 0;
	static final int MENU_DELETE_ALL = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movements_tab);

		ListView listView;

		listView = (ListView) findViewById(R.id.lvMovements);
		listView.setAdapter(new MoneyboxMovementAdapter(this));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				onMovementClick(a, v, position, id);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case EDIT_MOVEMENT_REQUEST:
			if (resultCode == RESULT_OK) {
				refreshMovements();
			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result;
		MenuItem item;

		result = super.onCreateOptionsMenu(menu);

		item = menu.add(0, MENU_DELETE_ALL, 0, R.string.menu_delete_all);
		item.setIcon(R.drawable.ic_menu_delete_all);

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DELETE_ALL:
			confirmDeleteAll();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();

		refreshMovements();
	}

	private void refreshMovements() {
		ListView listView;

		listView = (ListView) findViewById(R.id.lvMovements);
		((MoneyboxMovementAdapter) listView.getAdapter()).refreshMovements();
	}

	protected void onMovementClick(AdapterView<?> a, View v, int position,
			long id) {
		Intent i;
		Movement m;

		m = ((Movement) a.getAdapter().getItem(position));
		i = new Intent(this, MovementDetailActivity.class);
		i.putExtra("movement", m);

		startActivityForResult(i, EDIT_MOVEMENT_REQUEST);
	}

	private void confirmDeleteAll() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(R.string.delete_all_confirm);
		builder.setCancelable(true);

		builder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						onDeleteAll();
					}
				});

		builder.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert;

		alert = builder.create();
		alert.show();
	}

	private void onDeleteAll() {
		MovementsManager.deleteAllMovements ();
		refreshMovements();
	}

}
