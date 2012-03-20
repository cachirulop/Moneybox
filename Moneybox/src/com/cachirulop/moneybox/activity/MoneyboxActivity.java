package com.cachirulop.moneybox.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.CurrencyValueDef;
import com.cachirulop.moneybox.manager.CurrencyManager;
import com.cachirulop.moneybox.manager.MovementsManager;
import com.cachirulop.moneybox.manager.SoundsManager;

public class MoneyboxActivity 
	extends Activity {
	
	private static Context _context;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moneybox_tab);
        
        _context = this;
        
        addButtons ();
    }
    
    protected void onMoneyClicked(View v) {
        CurrencyValueDef value;
        
        value = (CurrencyValueDef) v.getTag();
        if (value != null) {
        	MovementsManager.addMovement(value.getAmount());
            throwMoney (v, value);
            updateTotals ();
        }
    }
    
    public void onHammerClicked(View v) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setMessage(R.string.break_moneybox_confirm);
    	builder.setCancelable(true);
    	
    	builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                breakMoneybox();
    	           }
    	       });
    	
    	builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	
    	AlertDialog alert;
    	
    	alert = builder.create();
    	alert.show();
    }
    
    protected void breakMoneybox ()
    {
    	RelativeLayout layout;
    	
        SoundsManager.playBreakingMoneyboxSound();
    	MovementsManager.breakMoneybox();

    	layout = (RelativeLayout) findViewById(R.id.moneyBoxLayout);
        layout.clearDisappearingChildren();

        updateTotals ();
    }
    
    /**
     * Move an image of the money like it was droping inside 
     * the money box
     * @param src Image of the money to drop
     */
    private void throwMoney (View src, CurrencyValueDef c) {
        ImageView money;
        AnimationSet moneyDrop;
        RelativeLayout layout;
        RelativeLayout.LayoutParams lpParams;
        HorizontalScrollView scroll;
        Rect r;

        scroll = (HorizontalScrollView) findViewById(R.id.scrollButtonsView);
        money = new ImageView (this);
        r = c.getDrawable().getBounds();
        
        money.setImageDrawable(c.getDrawable());
        money.setMinimumWidth(r.width());
        money.setMinimumHeight(r.height());

        layout = (RelativeLayout) findViewById(R.id.moneyBoxLayout);
        
        lpParams = new RelativeLayout.LayoutParams(r.width(), r.height());
        lpParams.leftMargin = src.getLeft() - scroll.getScrollX();
        lpParams.rightMargin = scroll.getWidth() - src.getRight();
        
        layout.addView(money, lpParams);

        if (c.getType() == CurrencyValueDef.MoneyType.COIN) {
        	moneyDrop = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.coin_drop);
        }
        else {
        	moneyDrop = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.bill_drop);
        }
        
        money.setVisibility(View.VISIBLE);
        
        SoundsManager.playCoinsSound();
        money.startAnimation(moneyDrop);
    }

   
    /**
     * Update all the totals
     */
    private void updateTotals () {
    	// Global 
    	TextView total;
    	
    	total = (TextView) findViewById(R.id.txtTotal);
    	total.setText(String.format("%.2f", MovementsManager.getTotalAmount()));
    }

    /**
     * Add the currency buttons dynamically from the money_defs.xml file
     */
    private void addButtons ()
    {
        ArrayList<CurrencyValueDef> currencies;
        String currencyName;
        LinearLayout buttons;
        
        buttons = (LinearLayout) findViewById(R.id.moneyButtonsLayout);
        
        currencyName = getResources().getString(R.string.currency_name);
        currencies = CurrencyManager.getCurrencyDef(currencyName);
        
        View.OnClickListener listener;
        
        listener = new View.OnClickListener() {
			public void onClick(View v) {
				onMoneyClicked(v);
			}
		};
		
        for (CurrencyValueDef c : currencies) {
        	ImageView v;
        	
        	v = new ImageView (this);
        	v.setOnClickListener(listener);
        	v.setImageDrawable(c.getDrawable());
        	v.setLongClickable(true);
        	v.setTag(c);
        	
        	buttons.addView(v);
        }
    }
    
    /**
     * Global context to access from other classes
     * 
     * @return A reference to the default Activity
     */
    public static Context getContext () 
    {
    	return _context;
    }

}


