package com.cachirulop.moneybox.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.Moneybox;
import com.cachirulop.moneybox.manager.MoneyboxesManager;

/**
 * Class to show a moneybox information in the drawer menu.
 * 
 * It shows the description, the creation date of the moneybox and a radio
 * button to select the moneybox.
 * 
 * @author david
 * 
 */
public class MoneyboxAdapter extends BaseAdapter {
	/** Inflater to load the xml with the definition of the view */
	private LayoutInflater _inflater;

	/** List of moneyboxes to be showed */
	private List<Moneybox> _lstMoneyboxes = null;
	
	private int _currentPosition = -1;
	private RadioButton _currentButton = null;
	

	/**
	 * Constructor that receives the context (parent) of the adapter. Creates
	 * the inflater to load the xml with the definition of the view.
	 * 
	 * @param context
	 *            Parent of the adapter.
	 */
	public MoneyboxAdapter(Context context) {
		_inflater = LayoutInflater.from(context);
	}

	/**
	 * Returns the number of items in the list of moneyboxes.
	 */
	public int getCount() {
		if (_lstMoneyboxes == null) {
			refreshMoneyboxes();
		}

		return _lstMoneyboxes.size();
	}

	/**
	 * Returns the item of the specified position.
	 */
	public Object getItem(int position) {
		if (_lstMoneyboxes == null) {
			refreshMoneyboxes();
		}

		return _lstMoneyboxes.get(position);
	}

	/**
	 * Returns the identifier of the item in the specified position. The
	 * identifier is the field IdMoneybox of the moneybox object.
	 */
	public long getItemId(int position) {
		if (_lstMoneyboxes == null) {
			refreshMoneyboxes();
		}

		return _lstMoneyboxes.get(position).getIdMoneybox();
	}

	/**
	 * Update the list of moneyboxes reading from the database with the method
	 * {@link MoneyboxesManager#getAllMoneyboxes}
	 */
	public void refreshMoneyboxes() {
		_lstMoneyboxes = MoneyboxesManager.getAllMoneyboxes();

		notifyDataSetChanged();
	}

	/**
	 * Returns the view to be showed in a row of the list.
	 * 
	 * The view is created from the layout moneybox_row in the folder res of the
	 * project.
	 * 
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;

		view = convertView;

		if (view == null) {
			view = _inflater.inflate(R.layout.moneybox_row, null);
			
			holder = new ViewHolder();
			holder.btn = (RadioButton) view.findViewById(R.id.rbMoneyboxRowMoneybox);
			holder.txt = (TextView) view.findViewById(R.id.txtMoneyboxRowCreationDate);
			
			view.setTag(holder);
		}
		else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (position != _currentPosition && _currentButton != null) {
					_currentButton.setChecked(false);
				}
				
				_currentPosition = position;
				_currentButton = (RadioButton) v;
			}
		});
		
		if (_currentPosition != position) {
			holder.btn.setChecked(false);
		}
		else {
			holder.btn.setChecked(true);
			if (_currentButton != null && holder.btn != _currentButton) {
				_currentButton = holder.btn;
			}
		}
		
		Moneybox m; 
		 
		m = _lstMoneyboxes.get(position);
		
		holder.btn.setText(m.getDescription()); 
		holder.btn.setTag(m);
		holder.txt.setText(m.getCreationDateFormatted());
		
		return view;
	}

	/*
	 * public View getView(int position, View convertView, ViewGroup parent) {
	 * if (convertView == null) { convertView =
	 * _inflater.inflate(R.layout.moneybox_row, null); }
	 * 
	 * Moneybox m; RadioButton rbMoneybox; TextView txtCreationDate;
	 * 
	 * m = _lstMoneyboxes.get(position);
	 * 
	 * rbMoneybox = (RadioButton) convertView
	 * .findViewById(R.id.rbMoneyboxRowMoneybox); txtCreationDate = (TextView)
	 * convertView .findViewById(R.id.txtMoneyboxRowCreationDate);
	 * 
	 * rbMoneybox.setText(m.getDescription()); rbMoneybox.setTag(m);
	 * txtCreationDate.setText(m.getCreationDateFormatted());
	 * 
	 * return convertView; }
	 */

	private class ViewHolder {
		RadioButton btn;
		TextView txt;
	}
}
