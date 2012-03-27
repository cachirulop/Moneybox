package com.cachirulop.moneybox.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.adapter.MoneyboxMovementAdapter;
import com.cachirulop.moneybox.entity.Movement;

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
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				onMovementClick(a, v, position, id);
			}
		});		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case EDIT_MOVEMENT_REQUEST:
			if (resultCode == RESULT_OK) {
				refreshMovements();
			}
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
			
		refreshMovements();
	}
	
	private void refreshMovements ()
	{
		ListView listView;

		listView = (ListView) findViewById(R.id.lvMovements);
		((MoneyboxMovementAdapter) listView.getAdapter()).refreshMovements();
	}
	
	protected void onMovementClick (AdapterView<?> a, View v, int position, long id) {
		Intent i;
		Movement m;
		
		m = ((Movement) a.getAdapter().getItem(position)); 
		i = new Intent (this, MovementDetailActivity.class);
		i.putExtra("movement", m);

		startActivityForResult(i, EDIT_MOVEMENT_REQUEST);
	}
}
