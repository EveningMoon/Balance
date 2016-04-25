package com.emoon.balance.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.Transaction;
import com.emoon.balance.R;
import com.emoon.balance.Util.Util;

import java.util.List;

/**
 * Created by zhanyap on 2016-03-12.
 */
public class TransactionAdapter extends ArrayAdapter<Transaction>{
    private Context context;
    private List<Transaction> transactionList;

    static class ViewHolder {
        public ImageView icon;
        public TextView name, points, unitType;
    }

    public TransactionAdapter(Context context, List<Transaction> transactionList){
        super(context, R.layout.item_transaction, transactionList);
        this.context = context;
        this.transactionList = transactionList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Avoid un-necessary calls to findViewById() on each row, which is expensive!
        ViewHolder viewHolder;

        /*
         * If convertView is not null, we can reuse it directly, no inflation required!
         * We only inflate a new View when the convertView is null.
         */
        if (convertView == null) {

            // Create a ViewHolder and store references to the two children views
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_transaction, parent, false);

            viewHolder.icon = (ImageView) convertView.findViewById(R.id.transactionIcon);
            viewHolder.points = (TextView) convertView.findViewById(R.id.transactionPointsPerUnit);
            viewHolder.unitType = (TextView) convertView.findViewById(R.id.transactionUnitType);
            viewHolder.name = (TextView) convertView.findViewById(R.id.earnBurnName);

            // The tag can be any Object, this just happens to be the ViewHolder
            convertView.setTag(viewHolder);
        }else {
            // Get the ViewHolder back to get fast access to the Views
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // getting Transaction data for the row
        Transaction transaction = transactionList.get(position);

        if(transaction.getEarnBurn().getType().equalsIgnoreCase(BalanceType.BURN.toString())) {
            viewHolder.icon.setColorFilter(Color.parseColor(context.getResources().getString(0 + R.color.icon_blue)));
        }else {
            viewHolder.icon.setColorFilter(Color.parseColor(context.getResources().getString(0 + R.color.icon_red)));
        }
        viewHolder.icon.setImageResource(Util.getIconID(this.context, transaction.getEarnBurn().getIcon()));

        viewHolder.name.setText(transaction.getEarnBurn().getName());
        viewHolder.points.setText(String.valueOf(transaction.getUnitCost()));
        viewHolder.unitType.setText(transaction.getCostType());

        return convertView;
    }
}