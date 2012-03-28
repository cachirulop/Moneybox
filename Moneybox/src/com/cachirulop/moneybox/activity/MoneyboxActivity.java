package com.cachirulop.moneybox.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.CurrencyValueDef;
import com.cachirulop.moneybox.entity.Movement;
import com.cachirulop.moneybox.manager.CurrencyManager;
import com.cachirulop.moneybox.manager.MovementsManager;
import com.cachirulop.moneybox.manager.SoundsManager;

public class MoneyboxActivity extends Activity {

	private static Context _context;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moneybox_tab);
		
		_context = this;

		initActivity();
	}
	
	private void initActivity ()
	{
		String currencyName;

		currencyName = getResources().getString(R.string.currency_name);
		
		CurrencyManager.initCurrencyDefList(currencyName);
		addButtons();
}

	protected void onMoneyClicked(View v) {
		CurrencyValueDef value;

		value = (CurrencyValueDef) v.getTag();
		if (value != null) {
			MovementsManager.insertMovement(value.getAmount());
			throwMoney(v, value);

			updateTotal();
		}
	}

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

	protected void breakMoneybox() {
		SoundsManager.playBreakingMoneyboxSound();
		MovementsManager.breakMoneybox();

		RelativeLayout layout;

		layout = (RelativeLayout) findViewById(R.id.moneyDropLayout);
		layout.removeAllViews();

		updateTotal();
	}

	/**
	 * Move an image of the money like it was dropping inside the money box
	 * 
	 * @param src
	 *            Image of the money to drop
	 */
	private void throwMoney(View src, CurrencyValueDef c) {
		HorizontalScrollView scroll;

		scroll = (HorizontalScrollView) findViewById(R.id.scrollButtonsView);

		throwMoney (src.getLeft() - scroll.getScrollX(), 
					// scroll.getWidth() - src.getRight(),
					src.getRight(), 
					c);
	}

	protected void throwMoney(int leftMargin, int width, CurrencyValueDef c) {
		ImageView money;
		AnimationSet moneyDrop;
		RelativeLayout layout;
		RelativeLayout.LayoutParams lpParams;
		Rect r;

		money = new ImageView(this);
		r = c.getDrawable().getBounds();

		money.setImageDrawable(c.getDrawable());

		layout = (RelativeLayout) findViewById(R.id.moneyDropLayout);

		lpParams = new RelativeLayout.LayoutParams(r.width(), r.height());
		lpParams.leftMargin = leftMargin;
		lpParams.rightMargin = layout.getWidth() - (leftMargin + width);

		layout.addView(money, lpParams);

		if (c.getType() == CurrencyValueDef.MoneyType.COIN) {
			moneyDrop = (AnimationSet) AnimationUtils.loadAnimation(this,
					R.anim.coin_drop);
		} else {
			moneyDrop = (AnimationSet) AnimationUtils.loadAnimation(this,
					R.anim.bill_drop);
		}

		money.setVisibility(View.VISIBLE);

		SoundsManager.playCoinsSound();

		money.startAnimation(moneyDrop);
		
		layout.invalidate();
	}

	/**
	 * Update all the totals
	 */
	private void updateTotal() {
		TextView total;
		
		total = (TextView) findViewById(R.id.txtTotal);
		total.setText(CurrencyManager.formatAmount(MovementsManager.getTotalAmount()));
	}
	
	/**
	 * Set a value to the total field
	 */
	protected void setTotal(double val) {
		TextView total;

		total = (TextView) findViewById(R.id.txtTotal);
		total.setText(CurrencyManager.formatAmount(val));
	}

	/**
	 * Add the currency buttons dynamically from the money_defs.xml file
	 */
	private void addButtons() {
		ArrayList<CurrencyValueDef> currencies;
		LinearLayout buttons;

		buttons = (LinearLayout) findViewById(R.id.moneyButtonsLayout);

		currencies = CurrencyManager.getCurrencyDefList();

		View.OnClickListener listener;

		listener = new View.OnClickListener() {
			public void onClick(View v) {
				onMoneyClicked(v);
			}
		};

		for (CurrencyValueDef c : currencies) {
			ImageView v;

			v = new ImageView(this);
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
	public static Context getContext() {
		return _context;
	}

	/**
	 * Fill the moneybox with all the movements dropping coins randomly
	 */
	public void fillMoneybox() {
		RelativeLayout layout;
		int maxWidth;
		ArrayList<Movement> lstMoney;
		Random rnd;
		double total;
		int i;

		layout = (RelativeLayout) findViewById(R.id.moneyDropLayout);
		maxWidth = layout.getWidth();
		if (maxWidth == 0) {
			// The layout is not initialized
			return;
		}
		
		total = 0.0;
		i = 0;
		
		rnd = new Random();
		lstMoney = MovementsManager.getActiveMovements();
		for (Movement m : lstMoney) {
			Rect r;
			CurrencyValueDef curr;
			int left;

			curr = CurrencyManager.getCurrencyDef(m.getAmount());
			r = curr.getDrawable().getBounds();

			left = rnd.nextInt(maxWidth - r.width());
			
			total += curr.getAmount();

			Timer t = new Timer ();
			ThrowMoneyTimerTask task;
			
			task = new ThrowMoneyTimerTask(this, curr, left, r.width(), total);
			t.schedule(task, 200 * i);
			
			i++;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
/*
		View v;
		final ViewTreeObserver vto;
		
		v = findViewById(R.id.moneyDropLayout);
		vto = v.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener () {
			public void onGlobalLayout() {
				fillMoneybox();
				
				 vto.removeGlobalOnLayoutListener(this);
			}
		});
*/
	}
}


final class ThrowMoneyTimerTask extends TimerTask  implements Runnable {
	MoneyboxActivity _parent;
	CurrencyValueDef _currency;
	int _left;
	int _width;
	double _total;
	public ThrowMoneyTimerTask (MoneyboxActivity parent, 
			CurrencyValueDef currency, 
			int left, 
			int width,
			double total) {
		_parent = parent;
		_currency = currency;
		_left = left;
		_width = width;
		_total = total;
	}
	
	public void run () {	
		Log.i("moneybox", "Running timer task");
		_parent.runOnUiThread(new Runnable () {
			public void run() {
				
				_parent.throwMoney(_left, _width, _currency);
				_parent.setTotal (_total);

				Log.i("moneybox", "Running timer task in UiThread");
			}
		});
	}
}





