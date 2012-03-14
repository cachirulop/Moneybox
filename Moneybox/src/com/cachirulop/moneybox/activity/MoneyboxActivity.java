package com.cachirulop.moneybox.activity;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.CurrencyValueDef;
import com.cachirulop.moneybox.entity.Movement;
import com.cachirulop.moneybox.manager.CurrencyManager;
import com.cachirulop.moneybox.manager.MovementsManager;

public class MoneyboxActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // TODO: Add dynamically the currency image buttons
        ArrayList<CurrencyValueDef> currencies;
        String currencyName;
        
        currencyName = getResources().getString(R.string.currency_name);
        
        currencies = CurrencyManager.getCurrencyDef(currencyName, this);
        for (CurrencyValueDef c : currencies) {
        	ImageView v;
        	
        	v = new ImageView (this);
        	v.setImageDrawable(c.getDrawable());
        	v.setTag(c);
        }
    }
    
    public void onMoneyClicked(View v) {
        double amount = 0;
        String name = "";
        
        switch (v.getId()) {
	        case R.id.btn1cent:
	    		amount = 0.01;
	    		name = "1 cent.";
	    		break;
        	
	        case R.id.btn2cent:
	    		amount = 0.02;
	    		name = "2 cent.";
	    		break;
	        case R.id.btn5cent:
	    		amount = 0.05;
	    		name = "5 cent.";
	    		break;
	        case R.id.btn10cent:
	    		amount = 0.10;
	    		name = "10 cent.";
	    		break;
	        case R.id.btn5euro:
	    		amount = 5;
	    		name = "5 euros";
	    		break;
        }
        
        Movement m;
        
        m = new Movement();
        m.setAmount(amount);
        m.setInsertDate(new Date());
        
        MovementsManager.addMovement(this, m);
        
        Toast.makeText(this, name + " pa la hucha! Total: " + MovementsManager.getTotalAmount(this), Toast.LENGTH_SHORT).show();

        ImageView spaceshipImage = (ImageView) findViewById(R.id.img1cent);

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.money_fall);
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
    }
}