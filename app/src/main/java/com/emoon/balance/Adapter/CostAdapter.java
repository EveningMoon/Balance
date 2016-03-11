package com.emoon.balance.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.emoon.balance.Model.Cost;
import com.emoon.balance.R;

import java.util.List;

/**
 * Created by zhanyap on 2016-03-07.
 */
public class CostAdapter extends ArrayAdapter<Cost>{

    private Activity activity;
    private List<Cost> costList;

    static class ViewHolder {
        public TextView points, quantity, measure;
    }

    public CostAdapter(Activity activity, List<Cost> costList){
        super(activity, R.layout.item_cost, costList);
        this.activity = activity;
        this.costList = costList;
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
            convertView = inflater.inflate(R.layout.item_cost, parent, false);

            viewHolder.points = (TextView) convertView.findViewById(R.id.pointsTextView);
            viewHolder.quantity = (TextView) convertView.findViewById(R.id.valueTextView);
            viewHolder.measure = (TextView) convertView.findViewById(R.id.measureTextView);

            // The tag can be any Object, this just happens to be the ViewHolder
            convertView.setTag(viewHolder);
        }else {
            // Get the ViewHolder back to get fast access to the Views
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // getting cost data for the row
        Cost cost = costList.get(position);

        viewHolder.points.setText(cost.getPointsEarnPer()+"");
        viewHolder.quantity.setText(cost.getUnitCost() + "");
        viewHolder.measure.setText(cost.getUnitType());

        return convertView;
    }
}
