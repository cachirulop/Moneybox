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

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.listener.IMoneyboxListener;
import com.cachirulop.moneybox.manager.CurrencyManager;
import com.cachirulop.moneybox.manager.MovementsManager;
import com.cachirulop.moneybox.manager.SoundsManager;

public class MainTabWidget 
	extends TabActivity 
	implements TabHost.OnTabChangeListener {
	
	private List<IMoneyboxListener> _listeners = new ArrayList<IMoneyboxListener> ();
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    Resources res = getResources();  // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;            // Reusable TabSpec for each tab
	    Intent intent;                   // Reusable Intent for each tab
	    
	    getTabHost().setOnTabChangedListener(this);

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    // Initialize a TabSpec for each tab and add it to the TabHost

	    // Moneybox tab
	    intent = new Intent(this, MoneyboxActivity.class);
	    spec = tabHost.newTabSpec("moneybox");
	    spec.setIndicator(res.getString(R.string.tab_title_moneybox), 
	    				  res.getDrawable(R.drawable.ic_tab_moneybox));
	    spec.setContent(intent);
	    tabHost.addTab(spec);
    	    
	    // Movements tab
	    intent = new Intent(this, MovementsActivity.class);
	    spec = tabHost.newTabSpec("movements");
	    spec.setIndicator(res.getString(R.string.tab_title_movements),
	                      res.getDrawable(R.drawable.ic_tab_movements));
	    spec.setContent(intent);
	    tabHost.addTab(spec);
	    
	    // Select the default tab
	    tabHost.setCurrentTab(0);
	    
	    updateTotal ();
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
	
	public void addListener (IMoneyboxListener listener) {
		_listeners.add(listener);
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
	public void onHammerClicked(View v) {
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
/*
		RelativeLayout layout;

		layout = (RelativeLayout) findViewById(R.id.moneyDropLayout);
		layout.removeAllViews();
*/
		for (IMoneyboxListener l : _listeners) {
			l.refresh();
		}

		updateTotal();
	}

	public void onTabChanged(String tabId) {
		if ("moneybox".equals(tabId)) {
			for (IMoneyboxListener l : _listeners) {
				l.refresh();
			}
		}
	}
}
