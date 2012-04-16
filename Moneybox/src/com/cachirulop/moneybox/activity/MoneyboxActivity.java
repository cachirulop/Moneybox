package com.cachirulop.moneybox.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.CurrencyValueDef;
import com.cachirulop.moneybox.entity.Movement;
import com.cachirulop.moneybox.listener.IMoneyboxListener;
import com.cachirulop.moneybox.listener.TranslateAnimationListener;
import com.cachirulop.moneybox.manager.ContextManager;
import com.cachirulop.moneybox.manager.CurrencyManager;
import com.cachirulop.moneybox.manager.MovementsManager;
import com.cachirulop.moneybox.manager.SoundsManager;
import com.cachirulop.moneybox.manager.VibratorManager;

public class MoneyboxActivity extends Activity implements IMoneyboxListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moneybox_tab);

		ContextManager.initContext(this);

		initActivity();
		updateTotal();
		
		((MainTabWidget) getParent()).addListener(this);

		registerLayoutListener();
	}

	/**
	 * Register the event OnGlobalLayoutListener to fill the moneybox when
	 * the layout is created.
	 */
	private void registerLayoutListener() {
		final View v;
		final ViewTreeObserver vto;

		v = findViewById(R.id.moneyDropLayout);
		vto = v.getViewTreeObserver();
		if (vto.isAlive()) {
			vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					initMoneybox();

					v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			});
		}
	}

	private void initActivity() {
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
			dropMoney(v, value);

			updateTotal();
		}
	}

	/**
	 * Move an image of the money like it was dropping inside the money box
	 * 
	 * @param src
	 *            Image of the money to drop
	 */
	private void dropMoney(View src, CurrencyValueDef c) {
		HorizontalScrollView scroll;

		scroll = (HorizontalScrollView) findViewById(R.id.scrollButtonsView);

		dropMoney(src.getLeft() - scroll.getScrollX(), src.getRight(), c);
	}

	/**
	 * Drop money from the top of the layout to the bottom simulating that a
	 * coin or bill is inserted in the moneybox.
	 * 
	 * @param leftMargin
	 *            Left side of the coin/bill
	 * @param width
	 *            Width of the image to slide down
	 * @param c
	 *            Value of the money to drop
	 */
	protected void dropMoney(int leftMargin, int width, CurrencyValueDef c) {
		ImageView money;
		AnimationSet moneyDrop;
		RelativeLayout layout;
		RelativeLayout.LayoutParams lpParams;
		Rect r;

		money = new ImageView(this);
		money.setVisibility(View.INVISIBLE);
		r = c.getDrawable().getBounds();

		money.setImageDrawable(c.getDrawable().getConstantState().newDrawable());
		money.setTag(c);

		layout = (RelativeLayout) findViewById(R.id.moneyDropLayout);

		lpParams = new RelativeLayout.LayoutParams(r.width(), r.height());
		lpParams.leftMargin = leftMargin;
		lpParams.rightMargin = layout.getWidth() - (leftMargin + width);
		lpParams.topMargin = 0;
		lpParams.bottomMargin = r.height();

		layout.addView(money, lpParams);
		/*
		 * if (c.getType() == CurrencyValueDef.MoneyType.COIN) { moneyDrop =
		 * (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.coin_drop);
		 * } else { moneyDrop = (AnimationSet)
		 * AnimationUtils.loadAnimation(this, R.anim.bill_drop); }
		 */
		moneyDrop = createDropAnimation(money, layout, c);
		money.setVisibility(View.VISIBLE);

		SoundsManager.playMoneySound(c.getType());
		VibratorManager.vibrateMoneyDrop(c.getType());

		moneyDrop.setAnimationListener(new TranslateAnimationListener(money,
				(View) layout));
		money.startAnimation(moneyDrop);
	}

	private AnimationSet createDropAnimation(ImageView img, View layout,
			CurrencyValueDef curr) {
		AnimationSet result;

		result = new AnimationSet(false);
		result.setFillAfter(false);

		// Fade in
		AlphaAnimation fadeIn;

		fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(300);
		result.addAnimation(fadeIn);

		// drop
		TranslateAnimation drop;
		int bottom;

		bottom = Math.abs(layout.getHeight() - img.getLayoutParams().height);
		drop = new TranslateAnimation(1.0f, 1.0f, 1.0f, bottom);
		drop.setStartOffset(300);
		drop.setDuration(1500);

		if (curr.getType() == CurrencyValueDef.MoneyType.COIN) {
			drop.setInterpolator(new BounceInterpolator());
		} else {
			drop.setInterpolator(new DecelerateInterpolator(0.7f));
		}

		result.addAnimation(drop);

		return result;
	}

	/**
	 * Take money of the moneybox removing the image from the view.
	 * 
	 * @param c
	 *            Value to be removed.
	 */
	protected void takeMoney(CurrencyValueDef c) {
		// RelativeLayout layout;
		Animation takeMoney;
		ImageView money;

		money = findMoneyImage(c);
		if (money != null) {
			// layout = (RelativeLayout) findViewById(R.id.moneyDropLayout);
			takeMoney = AnimationUtils.loadAnimation(this, R.anim.money_take);

			money.startAnimation(takeMoney);
			// layout.invalidate();
		}
	}

	private ImageView findMoneyImage(CurrencyValueDef c) {
		RelativeLayout layout;

		layout = (RelativeLayout) findViewById(R.id.moneyDropLayout);
		for (int i = 0; i < layout.getChildCount(); i++) {
			View money;
			CurrencyValueDef current;

			money = layout.getChildAt(i);
			if (money.getClass() == ImageView.class && money.getTag() != null) {
				current = (CurrencyValueDef) money.getTag();

				if (current.getAmount() == c.getAmount()) {
					return (ImageView) money;
				}
			}
		}

		return null;
	}

	/**
	 * Update the total amount
	 */
	private void updateTotal() {
		((MainTabWidget) getParent()).updateTotal();
	}

	/**
	 * Set a value to the total field
	 */
	protected void setTotal(double val) {
		((MainTabWidget) getParent()).setTotal(val);
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

			curr = CurrencyManager.getCurrencyDef(Math.abs(m.getAmount()));
			if (curr != null) {
				r = curr.getDrawable().getBounds();

				left = rnd.nextInt(maxWidth - r.width());

				total += m.getAmount();

				MoneyTimerTask task;

				task = new MoneyTimerTask(this, curr, left, r.width(),
						m.getAmount(), total);

				layout.postDelayed(task, 400 * i);
			}

			i++;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void initMoneybox() {
		fillMoneybox();

		HorizontalScrollView scroll;
		int offsetX;
		List<CurrencyValueDef> currList;
		int elemWidth;

		currList = CurrencyManager.getCurrencyDefList();
		scroll = (HorizontalScrollView) findViewById(R.id.scrollButtonsView);

		elemWidth = currList.get(0).getDrawable().getBounds().right;
		offsetX = ((currList.size() * elemWidth) / 2) - (elemWidth / 2);
		scroll.scrollTo(offsetX, 0);
	}

	public void refresh() {
		RelativeLayout layout;

		layout = (RelativeLayout) findViewById(R.id.moneyDropLayout);
		layout.removeAllViews();
		
		fillMoneybox();
	}
}

final class MoneyTimerTask implements Runnable {
	MoneyboxActivity _parent;
	CurrencyValueDef _currency;
	int _left;
	int _width;
	double _total;
	double _amount;

	public MoneyTimerTask(MoneyboxActivity parent, CurrencyValueDef currency,
			int left, int width, double amount, double total) {
		_parent = parent;
		_currency = currency;
		_left = left;
		_width = width;
		_amount = amount;
		_total = total;
	}

	public void run() {
		// Log.i("moneybox", "Running timer task");
		_parent.runOnUiThread(new Runnable() {
			public void run() {

				if (_amount > 0) {
					_parent.dropMoney(_left, _width, _currency);
				} else {
					_parent.takeMoney(_currency);
				}
				_parent.setTotal(_total);

				// Log.i("moneybox", "Running timer task in UiThread");
			}
		});
	}
}
