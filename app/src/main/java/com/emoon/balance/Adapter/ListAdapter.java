package com.emoon.balance.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.EarnBurn;
import com.emoon.balance.R;
import com.emoon.balance.Util.Util;
import com.zhan.library.CircularView;

import java.util.List;


/**
 * Created by zhanyap on 15-08-19.
 */
public class ListAdapter extends ArrayAdapter<EarnBurn> {

    private Context context;
    private List<EarnBurn> earnBurnList;

    static class ViewHolder {
        public TextView name;
        public CircularView circularView;
    }

    public ListAdapter(Context context, List<EarnBurn> earnBurnList){
        super(context, R.layout.item_listview_earn_burn, earnBurnList);
        this.context = context;
        this.earnBurnList = earnBurnList;
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
            convertView = inflater.inflate(R.layout.item_listview_earn_burn, parent, false);

            viewHolder.name = (TextView) convertView.findViewById(R.id.earnBurnName);
            viewHolder.circularView = (CircularView) convertView.findViewById(R.id.genericCircularView);

            // The tag can be any Object, this just happens to be the ViewHolder
            convertView.setTag(viewHolder);
        }else {
            // Get the ViewHolder back to get fast access to the Views
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // getting EarnBurn data for the row
        EarnBurn earnBurn = earnBurnList.get(position);

        viewHolder.name.setText(earnBurn.getName());

        if(earnBurn.getType().equalsIgnoreCase(BalanceType.BURN.toString())){
            viewHolder.circularView.setIconColor(R.color.icon_blue);
            viewHolder.circularView.setCircleColor(R.color.dark_blue);
        }else{
            viewHolder.circularView.setIconColor(R.color.icon_red);
            viewHolder.circularView.setCircleColor(R.color.dark_red);
        }

        viewHolder.circularView.setIconResource(Util.getIconID(context, earnBurn.getIcon()));

        return convertView;
    }

}