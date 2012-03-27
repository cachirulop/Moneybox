package com.cachirulop.moneybox.activity;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.Movement;
import com.cachirulop.moneybox.manager.MovementsManager;

public class MovementsActivity extends Activity {
	static final int EDIT_MOVEMENT_REQUEST = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movements_tab);

		ListView listView;

		listView = (ListView) findViewById(R.id.lvMovements);
		listView.setAdapter(new MoneyboxMovementAdapter(this));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView a, View v, int position, long id) {
				onMovementClick(a, v, position, id);
			}
		});		
	}

	@Override
	protected void onResume() {
		super.onResume();

		ListView listView;

		listView = (ListView) findViewById(R.id.lvMovements);
		((MoneyboxMovementAdapter) listView.getAdapter()).refreshMovements();
	}
	
	protected void onMovementClick (AdapterView a, View v, int position, long id) {
		Intent i;
		Movement m;
		
		m = ((Movement) a.getAdapter().getItem(position)); 
		i = new Intent (this, MovementDetailActivity.class);
		i.putExtra("movement", m);

		startActivityForResult(i, EDIT_MOVEMENT_REQUEST);
/*		
		Context mContext = getApplicationContext();
		Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.movement_detail);
		dialog.setTitle("Custom Dialog");
		
		// Init the dialog fields
		TextView txtAmount;
		TextView txtDescription;
		Movement m;

		txtAmount = (TextView) dialog.findViewById(R.id.txtAmount);
		txtDescription = (TextView) dialog.findViewById(R.id.txtDescription);
		
		m = ((Movement) a.getAdapter().getItem(position)); 

		//dpDate.set
		txtAmount.setText(String.format ("%.2f", m.getAmount()));
		txtDescription.setText(m.getDescription());
		
		dialog.show ();
*/				
	}

	private static class MoneyboxMovementAdapter extends BaseAdapter {
		private MovementsActivity _parent;
		private LayoutInflater _inflater;
		private List<Movement> _lstMovements = null;

		public MoneyboxMovementAdapter(Context context) {
			_parent = (MovementsActivity) context;
			_inflater = LayoutInflater.from(context);
		}

		public int getCount() {
			if (_lstMovements == null) {
				refreshMovements();
			}

			return _lstMovements.size();
		}

		public Object getItem(int position) {
			if (_lstMovements == null) {
				refreshMovements();
			}

			return _lstMovements.get(position);
		}

		public long getItemId(int position) {
			if (_lstMovements == null) {
				refreshMovements();
			}

			return _lstMovements.get(position).getIdMovement();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = _inflater.inflate(R.layout.movement_row, null);
			}

			Movement m;
			TextView txtDate;
			TextView txtDescription;
			TextView txtAmount;

			m = _lstMovements.get(position);
			txtDate = (TextView) convertView
					.findViewById(R.id.txtRowMovementDate);
			txtDescription = (TextView) convertView
					.findViewById(R.id.txtRowMovementDescription);
			txtAmount = (TextView) convertView
					.findViewById(R.id.txtRowMovementAmount);

			txtDate.setText(formatDate(m.getInsertDate()));
			txtAmount.setText(String.format("%.2f", m.getAmount()));
			txtDescription.setText(m.getDescription());

			if (m.isBreakMoneybox()) {
				convertView.setBackgroundColor(Color.GREEN);
			}
			else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}

			return convertView;
		}

		private String formatDate(Date d) {
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
					DateFormat.SHORT).format(d);
		}

		/*
		 * static class ViewHolder { TextView text1; TextView text2; TextView
		 * text3; }
		 */
		public void refreshMovements() {
			_lstMovements = MovementsManager.getAllMovements();
		}

	}
}
