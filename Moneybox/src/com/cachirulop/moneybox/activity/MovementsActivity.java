package com.cachirulop.moneybox.activity;

import com.cachirulop.moneybox.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MovementsActivity 	extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		TextView textview = new TextView(this);
        textview.setText("Movements activity tab");
        setContentView(textview);
    }
}
