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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.adapter.MoneyboxMovementAdapter;
import com.cachirulop.moneybox.data.MoneyboxDataHelper;
import com.cachirulop.moneybox.entity.Movement;
import com.cachirulop.moneybox.manager.MovementsManager;

/**
 * Activity that shows a list with all the movements in the money box.
 * 
 * The items in the list can be selected to edit its properties with a simple
 * click.
 * 
 * Also can be selected with a long click to delete of the list or to get from
 * the moneybox.
 * 
 * @author dmagro
 * 
 */
public class MovementsActivity extends Activity {
	/** Constant to identify the click in the list */
	static final int EDIT_MOVEMENT_REQUEST = 0;

	/** Constant to identify the menu option to delete all the movements */
	static final int MENU_DELETE_ALL = 0;

	/** Constant to identify the menu option to import the database*/
	static final int MENU_IMPORT_DATABASE = 1;

	/** Constant to identify the menu option to export the database*/
	static final int MENU_EXPORT_DATABASE = 2;

	/**
	 * Constant to identify the context menu option to get money from the
	 * moneybox.
	 */
	static final int CONTEXT_MENU_GET = 0;

	/** Constant to identify the context menu option to delete a movement */
	static final int CONTEXT_MENU_DELETE = 1;

	/**
	 * Creates the ListView object to contains the movements information and
	 * register the context menu.
	 */
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

		registerForContextMenu(listView);

		((MainTabWidget) getParent()).setMovementsTab(this);
	}

	/**
	 * Called when returns of the edit detail window. Refresh the movement list
	 * to show the possible changes do it in the edit detail window.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case EDIT_MOVEMENT_REQUEST:
			if (resultCode == RESULT_OK) {
				refresh();
			}
			break;
		}
	}

	/**
	 * Creates an option to delete all the movements of the moneybox.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result;
		MenuItem item;

		result = super.onCreateOptionsMenu(menu);

		item = menu.add(0, MENU_DELETE_ALL, 0, R.string.menu_delete_all);
		item.setIcon(R.drawable.ic_menu_delete_all);
		
		item = menu.add(1, MENU_EXPORT_DATABASE, 1, R.string.menu_export_database);
		item.setIcon(R.drawable.ic_menu_export);
		
		item = menu.add(1, MENU_IMPORT_DATABASE, 2, R.string.menu_import_database);
		item.setIcon(R.drawable.ic_menu_import);

		return result;
	}

	/**
	 * Called when the user select the option to delete all the movements. Call
	 * to the confirmDeleteAll method to confirm and delete the movements.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DELETE_ALL:
			confirmDeleteAll();
			return true;
			
		case MENU_EXPORT_DATABASE:
			MoneyboxDataHelper.exportDB(this);
			return true;

		case MENU_IMPORT_DATABASE:
			MoneyboxDataHelper.importDB(this);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Refresh the movements list.
	 */
	@Override
	protected void onResume() {
		super.onResume();

		refresh();
	}

	/**
	 * Call to the adapter of the ListView with the movements to refresh the
	 * information.
	 */
	private void refreshMovements() {
		ListView listView;

		listView = (ListView) findViewById(R.id.lvMovements);
		((MoneyboxMovementAdapter) listView.getAdapter()).refreshMovements();
	}

	/**
	 * Called when a movement in the list is clicked.
	 * 
	 * Show a window to edit the movement (MovementDetailActivity).
	 * 
	 * @param a
	 *            Adapter of the view that launch the event.
	 * @param v
	 *            View that launch the event.
	 * @param position
	 *            Position of the item clicked
	 * @param id
	 *            Identifier of the clicked item.
	 */
	protected void onMovementClick(AdapterView<?> a, View v, int position,
			long id) {
		Intent i;
		Movement m;

		m = ((Movement) a.getAdapter().getItem(position));
		i = new Intent(this, MovementDetailActivity.class);
		i.putExtra("movement", m);

		startActivityForResult(i, EDIT_MOVEMENT_REQUEST);
	}

	/**
	 * Show a dialog to confirm that the user want to delete all the movements.
	 * 
	 * Register the method onDeleteAll to be called if the user decide to delete
	 * all the movements.
	 */
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

	/**
	 * Delete all the movements in the moneybox calling the deleteAllMovements
	 * method of the MovementsManager class.
	 */
	private void onDeleteAll() {
		MovementsManager.deleteAllMovements();
		refresh();
	}

	/**
	 * Refresh the total amount and the list of movements.
	 */
	public void refresh() {
		updateTotal();
		refreshMovements();
	}

	/**
	 * Update the total amount calling the main tab activity.
	 */
	private void updateTotal() {
		((MainTabWidget) getParent()).updateTotal();
	}

	/**
	 * Creates a context menu for the list of movements, showing an option for
	 * delete the movement and another to get the movement.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if (v.getId() == R.id.lvMovements) {
			AdapterView.AdapterContextMenuInfo info;
			ListView listView;
			Movement selected;

			listView = (ListView) findViewById(R.id.lvMovements);
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;

			selected = (Movement) ((MoneyboxMovementAdapter) listView
					.getAdapter()).getItem(info.position);

			MenuItem item;

			menu.setHeaderTitle(selected.getInsertDateFormatted());
			item = menu.add(Menu.NONE, CONTEXT_MENU_GET, 0,
					R.string.get_from_moneybox);
			if (selected.getGetDate() != null) {
				item.setEnabled(false);
			}
			item = menu.add(Menu.NONE, CONTEXT_MENU_DELETE, 1, R.string.delete);
		}
	}

	/**
	 * Handles the selected item on the context menu. Could be an option to
	 * delete the item or to get it from the moneybox.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == CONTEXT_MENU_GET
				|| item.getItemId() == CONTEXT_MENU_DELETE) {

			AdapterView.AdapterContextMenuInfo info;
			ListView listView;
			Movement selected;

			listView = (ListView) findViewById(R.id.lvMovements);
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

			selected = (Movement) ((MoneyboxMovementAdapter) listView
					.getAdapter()).getItem(info.position);

			if (item.getItemId() == CONTEXT_MENU_GET) {
				MovementsManager.getMovement(selected);
			} else if (item.getItemId() == CONTEXT_MENU_DELETE) {
				MovementsManager.deleteMovement(selected);
			}

			refresh();
		}

		return true;
	}
	
	/**
	 * Don't close the application, select the default tab.
	 */
	@Override
	public void onBackPressed() {
		((MainTabWidget) getParent()).selectMainTab();
	}


}
