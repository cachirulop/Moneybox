package com.cachirulop.moneybox.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.Moneybox;
import com.cachirulop.moneybox.manager.MoneyboxesManager;

/**
 * Class to show a moneybox information in the drawer menu.
 * 
 * It shows the description, the creation date of the moneybox and a 
 * radio button to select the moneybox.
 * 
 * @author david
 *
 */
public class MoneyboxAdapter extends BaseAdapter {
    /** Inflater to load the xml with the definition of the view */
    private LayoutInflater _inflater;
    
    /** List of moneyboxes to be showed */
    private List<Moneybox> _lstMoneyboxes = null;

    /**
     * Constructor that receives the context (parent) of the adapter.
     * Creates the inflater to load the xml with the definition of the view.
     * 
     * @param context Parent of the adapter.
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
     * Returns the identifier of the item in the specified position.
     * The identifier is the field IdMoneybox of the moneybox object.
     */
    public long getItemId(int position) {
        if (_lstMoneyboxes == null) {
            refreshMoneyboxes();
        }

        return _lstMoneyboxes.get(position).getIdMoneybox();
    }

    /**
     * Returns the view to be showed in a row of the list.
     * 
     * The view is created from the layout moneybox_row in the folder res of
     * the project.
     * 
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = _inflater.inflate(R.layout.moneybox_row, null);
        }

        Moneybox m;
        TextView txtDate;
        TextView txtGetDate;
        TextView txtDescription;
        TextView txtAmount;

        m = _lstMoneyboxes.get(position);
/*        
        txtDate = (TextView) convertView
                .findViewById(R.id.txtRowMovementDate);
        txtGetDate = (TextView) convertView
                .findViewById(R.id.txtRowMovementGetDate);
        txtDescription = (TextView) convertView
                .findViewById(R.id.txtRowMovementDescription);
        txtAmount = (TextView) convertView
                .findViewById(R.id.txtRowMovementAmount);

        txtDate.setText(m.getInsertDateFormatted());
        if (m.getGetDate() != null) {
            txtAmount.setPaintFlags(txtAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtGetDate.setVisibility(View.VISIBLE);
            txtGetDate.setText(m.getGetDateFormatted());
        }
        else {
            txtAmount.setPaintFlags(txtAmount.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            txtGetDate.setVisibility(View.GONE);
            txtGetDate.setText("");
        }
        
        txtAmount.setText(CurrencyManager.formatAmount(m.getAmount()));
        
        if (m.getDescription() != null && !m.getDescription().trim().equals ("")) {
            txtDescription.setVisibility(View.VISIBLE);
            txtDescription.setText(m.getDescription());
        }
        else {
            txtDescription.setVisibility(View.GONE);
        }

        if (m.isBreakMoneybox()) {
            txtDate.setTextColor(Color.RED);
            txtAmount.setTextColor(Color.RED);
            txtDescription.setTextColor(Color.RED);
        }
        else {
            int blue;
            
            blue = Color.rgb(5, 143, 255);
            
            txtDate.setTextColor(blue);
            txtAmount.setTextColor(blue);
            txtDescription.setTextColor(blue);

            if (m.getGetDate() != null) {
                txtGetDate.setTextColor(Color.YELLOW);
            }           
        }
*/
        return convertView;
    }

    /**
     * Update the list of moneyboxes reading from the database with the 
     * method {@link MoneyboxesManager#getAllMoneyboxes}
     */
    public void refreshMoneyboxes() {
        _lstMoneyboxes = MoneyboxesManager.getAllMoneyboxes();
        
        notifyDataSetChanged();
    }
}
