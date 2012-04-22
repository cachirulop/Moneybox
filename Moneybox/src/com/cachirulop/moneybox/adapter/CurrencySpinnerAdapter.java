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
package com.cachirulop.moneybox.adapter;

import java.util.List;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.CurrencyValueDef;
import com.cachirulop.moneybox.manager.CurrencyManager;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class CurrencySpinnerAdapter extends BaseAdapter implements
		SpinnerAdapter {
	private final List<CurrencyValueDef> _content;
	private final Activity _activity;

	public CurrencySpinnerAdapter(Activity activity) {
		super();
		
		this._content = CurrencyManager.getCurrencyDefList();
		this._activity = activity;
	}

	public int getCount() {
		return _content.size();
	}

	public CurrencyValueDef getItem(int position) {
		return _content.get(position);
	}
	
	public int getItemPositionByAmount (double amount) {
		int i;
		 
		i = 0;
		for (CurrencyValueDef c : _content) {
			if (c.getAmount() == amount) {
				return i;
			}
			
			i++;
		}
		
		return -1;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater;		
		View spinnerEntry; 
		TextView amount;
		ImageView currencyImage;
		CurrencyValueDef currentEntry;

		inflater = _activity.getLayoutInflater();
		spinnerEntry = inflater.inflate(R.layout.spinner_money_list, null);
		
		amount = (TextView) spinnerEntry.findViewById(R.id.txtCurrencyAmount);
		currencyImage = (ImageView) spinnerEntry.findViewById(R.id.ivCurrency);

		currentEntry = _content.get(position);
		
		amount.setText(CurrencyManager.formatAmount(currentEntry.getAmount()));
		currencyImage.setImageDrawable(currentEntry.getDrawable());
		
		return spinnerEntry;
	}
}
