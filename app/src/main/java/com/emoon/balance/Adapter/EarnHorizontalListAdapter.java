package com.emoon.balance.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emoon.balance.Model.Earn;
import com.emoon.balance.R;
import com.zhan.library.CircularView;

import java.util.List;

/**
 * Created by zhanyap on 2016-02-20.
 */
public class EarnHorizontalListAdapter extends RecyclerView.Adapter<EarnHorizontalListAdapter.ViewHolder> {

    private Activity activity;
    private List<Earn> list;
    private EarnInterfaceListener mListener;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView name;
        public CircularView circularView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView){
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.baseName);
            circularView = (CircularView) itemView.findViewById(R.id.baseIcon);
        }
    }

    public EarnHorizontalListAdapter(Activity activity, List<Earn> list) {
        this.activity = activity;
        this.list = list;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public EarnHorizontalListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.item_base, parent, false);

        view.setBackground(ContextCompat.getDrawable(this.activity, R.drawable.red_button));

        // Return a new holder instance
        final ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(viewHolder.getLayoutPosition());
            }
        });

        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(EarnHorizontalListAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Earn item = list.get(position);

        // Set item views based on the data model
        viewHolder.name.setText(item.getName());
        viewHolder.circularView.setCircleColor(R.color.dark_red);
        viewHolder.circularView.setIconResource(R.drawable.ic_person);
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void setOnItemClickListener(EarnInterfaceListener listener){
        mListener = listener;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Interfaces
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public interface EarnInterfaceListener{
        void onItemClick(int position);
    }
}
