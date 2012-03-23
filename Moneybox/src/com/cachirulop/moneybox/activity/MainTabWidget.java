package com.cachirulop.moneybox.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.cachirulop.moneybox.R;

public class MainTabWidget 
	extends TabActivity {
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    Resources res = getResources();  // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;            // Reusable TabSpec for each tab
	    Intent intent;                   // Reusable Intent for each tab

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
	}
}
