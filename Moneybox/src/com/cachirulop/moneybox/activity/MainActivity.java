package com.cachirulop.moneybox.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.data.MoneyboxDataHelper;
import com.cachirulop.moneybox.fragment.MoneyboxFragment;
import com.cachirulop.moneybox.fragment.MovementsFragment;
import com.cachirulop.moneybox.manager.ContextManager;
import com.cachirulop.moneybox.manager.CurrencyManager;
import com.cachirulop.moneybox.manager.MovementsManager;
import com.cachirulop.moneybox.manager.SoundsManager;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, IMoneyboxListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter _sectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager _viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ContextManager.initContext(this);
		
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		_sectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		_viewPager = (ViewPager) findViewById(R.id.pager);
		_viewPager.setAdapter(_sectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		_viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < _sectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(_sectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
	
	/**
	 * Update the total amount
	 */
	public void updateTotal() {
		TextView total;

		total = (TextView) findViewById(R.id.txtTotal);
		total.setText(CurrencyManager.formatAmount(MovementsManager
				.getTotalAmount()));
	}

	/**
	 * Set a value to the total field
	 */
	public void setTotal(double val) {
		TextView total;

		total = (TextView) findViewById(R.id.txtTotal);
		total.setText(CurrencyManager.formatAmount(val));
	}

	/**
	 * Load the menu from the main.xml file.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Menu option selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_delete_all:
			confirmDeleteAll();
			return true;

		case R.id.action_import:
			MoneyboxDataHelper.importDB(this);
			return true;

		case R.id.action_export:
			MoneyboxDataHelper.exportDB(this);
			return true;
			
		case R.id.action_break_moneybox:
			onHammerClicked();
			return true;

		default:
			return super.onOptionsItemSelected(item);
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
	public void onHammerClicked() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(R.string.break_moneybox_confirm);
		builder.setCancelable(true);

		builder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						breakMoneybox();
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
	 * Empty the moneybox.
	 */
	protected void breakMoneybox() {
		SoundsManager.playBreakingMoneyboxSound();
		MovementsManager.breakMoneybox();
		
		refresh();
		
		updateTotal();
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
	 * Refresh the contents of the tabs
	 */
	public void refresh() {
		_sectionsPagerAdapter.getMoneyboxFragment().refresh();
		_sectionsPagerAdapter.getMovementsFragment().refresh();
	}
	

	// @Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		_viewPager.setCurrentItem(tab.getPosition());
	}

	// @Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	// @Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		private final int TAB_MONEYBOX_INDEX = 0;
		private final int TAB_MOVEMENTS_INDEX = 1;
		
		private MoneyboxFragment _moneybox = null;
		private MovementsFragment _movements = null;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment result = null;
			
			switch (position) {
			case TAB_MONEYBOX_INDEX:
				result = getMoneyboxFragment();						
				break;				
				
			case TAB_MOVEMENTS_INDEX:
				result = getMovementsFragment();
				break;
			}
			
			return result;
		}
		
		public MoneyboxFragment getMoneyboxFragment() {
			if (_moneybox == null) {
				_moneybox = new MoneyboxFragment();
			}

			return _moneybox;
		}

		public MovementsFragment getMovementsFragment() {
			if (_movements == null) {
				_movements = new MovementsFragment(); 
			}
			
			return _movements;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.tab_title_moneybox);
			case 1:
				return getString(R.string.tab_title_movements);
			}
			
			return null;
		}
	}


	public void onUpdateTotal() {
		updateTotal();
	}

	public void onSetTotal(double value) {
		setTotal(value);
	}

	public void onUpdateMoneybox() {
		_sectionsPagerAdapter.getMoneyboxFragment().refresh();
	}

	public void onUpdateMovements() {
		_sectionsPagerAdapter.getMovementsFragment().refresh();
	}
	
	public void onSelectDefaultTab() {
		_viewPager.setCurrentItem(0);
	}

}
