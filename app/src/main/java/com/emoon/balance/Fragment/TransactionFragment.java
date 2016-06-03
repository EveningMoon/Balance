package com.emoon.balance.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.emoon.balance.Activity.InfoActivity;
import com.emoon.balance.Adapter.TransactionAdapter;
import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.EarnBurn;
import com.emoon.balance.Model.IconType;
import com.emoon.balance.Model.Transaction;
import com.emoon.balance.R;
import com.emoon.balance.Util.Util;
import com.emoon.balance.View.ExtendedNumberPicker;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zhan.library.CircularView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by zhanyap on 2016-03-12.
 */
public class TransactionFragment extends BaseRealmFragment{

    private static final String TAG = "TransactionFragment";

    private TransactionAdapter transactionAdapter;
    private SwipeMenuListView transactionListView;
    private List<Transaction> transactionList;
    private RealmResults<Transaction> transactionRealmResults;

    private TextView emptyTextView;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_transaction;
    }

    @Override
    protected void init() {
        super.init();

        emptyTextView = (TextView)view.findViewById(R.id.emptyTransactionTextView);

        transactionListView = (SwipeMenuListView)view.findViewById(R.id.transactionListView);
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(getContext(), transactionList);
        transactionListView.setAdapter(transactionAdapter);

        createSwipeMenu();

        getAllTransactions();
    }

    private void getAllTransactions(){
        transactionRealmResults = myRealm.where(Transaction.class).findAllAsync();
        transactionRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Transaction>>() {
            @Override
            public void onChange(RealmResults<Transaction> element) {
                transactionList = myRealm.copyFromRealm(element);

                transactionAdapter.clear();
                transactionAdapter.addAll(transactionList);

                updateTransactionStatus();

                element.removeChangeListener(this);
            }
        });
    }

    private void updateTransactionStatus(){
        if(transactionList.size() > 0){
            emptyTextView.setVisibility(View.GONE);
            transactionListView.setVisibility(View.VISIBLE);
        }else{
            emptyTextView.setVisibility(View.VISIBLE);
            transactionListView.setVisibility(View.GONE);
        }
    }

    /**
     * Add swipe capability on list view to delete that item.
     * From 3rd party library.
     */
    private void createSwipeMenu(){
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem editItem = new SwipeMenuItem(getContext());
                editItem.setBackground(R.color.green);
                editItem.setWidth(Util.dp2px(getContext(), 90));
                editItem.setIcon(R.drawable.svg_ic_edit);
                menu.addMenuItem(editItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
                deleteItem.setBackground(R.color.red);// set item background
                deleteItem.setWidth(Util.dp2px(getContext(), 90));// set item width
                deleteItem.setIcon(R.drawable.svg_ic_delete);// set a icon
                menu.addMenuItem(deleteItem);// add to menu
            }
        };
        //set creator
        transactionListView.setMenuCreator(creator);

        // step 2. listener item click event
        transactionListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        editTransaction(transactionList.get(position).getEarnBurn().getId(), transactionList.get(position), position);
                        break;
                    case 1:
                        // delete
                        Toast.makeText(getContext(), "DELETING @ " + position, Toast.LENGTH_SHORT).show();
                        transactionList.remove(position);

                        transactionAdapter.clear();
                        transactionAdapter.addAll(transactionList);

                        myRealm.beginTransaction();
                        transactionRealmResults.get(position).deleteFromRealm();
                        myRealm.commitTransaction();

                        updateTransactionStatus();

                        break;
                }
                //False: Close the menu
                //True: Did not close the menu
                return false;
            }
        });

        // set SwipeListener
        transactionListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });
    }


    MaterialEditText input;

    /**
     * Displays prompt for user to edit transaction.
     */
    private void editTransaction(String dataId, final Transaction tt, final int pos){
        final EarnBurn data = myRealm.where(EarnBurn.class).equalTo("id", dataId).findFirst();

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

        //It is ok to put null as the 2nd parameter as this custom layout is being attached to a
        //AlertDialog, where it not necessary to know what the parent is.
        View promptView = layoutInflater.inflate(R.layout.alertdialog_generic, null);

        //Title
        TextView title = (TextView) promptView.findViewById(R.id.genericTitle);
        title.setText(data.getName());

        //Circular view
        CircularView cv = (CircularView) promptView.findViewById(R.id.genericCircularView);
        if(data.getIconType().equalsIgnoreCase(IconType.ICON.toString())){
            cv.setIconResource(Util.getIconID(getContext(), data.getIcon()));
            cv.setIconColor(R.color.white);
        }else if(data.getIconType().equalsIgnoreCase(IconType.NUMBER.toString())){
            cv.setText(""+Util.getFirstCharacterFromString(data.getName().toUpperCase()));
            cv.setTextColor(R.color.white);
        }

        //Edit text
        input = (MaterialEditText) promptView.findViewById(R.id.genericEditText);
        input.setHint(data.getType());
        input.setFloatingLabelText(data.getType());
        input.setText(""+tt.getUnitCost());

        if(data.getType().equalsIgnoreCase(BalanceType.BURN.toString())){
            cv.setCircleColor(R.color.blue);
            input.setBaseColor(ContextCompat.getColor(getContext(), R.color.blue));
            input.setPrimaryColor(ContextCompat.getColor(getContext(), R.color.blue));
            input.setMetHintTextColor(ContextCompat.getColor(getContext(), R.color.blue));
        }else{
            cv.setCircleColor(R.color.red);
            input.setBaseColor(ContextCompat.getColor(getContext(), R.color.red));
            input.setPrimaryColor(ContextCompat.getColor(getContext(), R.color.red));
            input.setMetHintTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }

        //NumberPicker
        final ExtendedNumberPicker unitNumberPicker = (ExtendedNumberPicker) promptView.findViewById(R.id.genericNumberPicker);

        List<String> ssList = new ArrayList<>();
        for(int i = 0; i < data.getCostList().size(); i++){
            ssList.add(data.getCostList().get(i).getUnitType());
        }

        final String[] values = ssList.toArray(new String[0]);

        unitNumberPicker.setMinValue(0);

        //find current transactions unit type and set it to that
        int index = 0;
        for(int a = 0;a < ssList.size(); a++){
            if(tt.getCostType().equalsIgnoreCase(ssList.get(a))){
                index = a;
                break;
            }
        }


        //If this earnBurn have been init with values
        if(values.length > 0){
            unitNumberPicker.setMaxValue(values.length - 1);
            unitNumberPicker.setDisplayedValues(values);
            unitNumberPicker.setWrapSelectorWheel(true);
            unitNumberPicker.setValue(index);


            final AlertDialog ss = new AlertDialog.Builder(getActivity())
                    .setView(promptView)
                    .setCancelable(true)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (!input.getText().toString().isEmpty()) {
                                myRealm.beginTransaction();
                                Transaction transaction = myRealm.where(Transaction.class).equalTo("id", tt.getId()).findFirst();
                                transaction.setUnitCost(Integer.parseInt(input.getText().toString()));
                                transaction.setCostType(values[unitNumberPicker.getValue()]);
                                myRealm.copyToRealmOrUpdate(transaction);
                                myRealm.commitTransaction();

                                getAllTransactions();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create();

            ss.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    if(data.getType().equalsIgnoreCase(BalanceType.BURN.toString())){
                        //BLUE
                        ss.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        ss.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    }else{
                        //RED
                        ss.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                        ss.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    }
                }
            });

            ss.show();
        }else{
            Intent firstTimeEarnBurn = new Intent(getContext(), InfoActivity.class);
            firstTimeEarnBurn.putExtra(Constants.REQUEST_IS_EDIT_EARNBURN, true);
            firstTimeEarnBurn.putExtra(Constants.REQUEST_EDIT_EARNBURN, data.getId());
            startActivity(firstTimeEarnBurn);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Lifecycle
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
        resumeRealm();
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
        resumeRealm();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
        closeRealm();
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop");
        closeRealm();
    }

    public void resumeRealm(){
        //if(myRealm == null || myRealm.isClosed()){
            myRealm = Realm.getDefaultInstance();
            Log.d(TAG, "resumeRealm");
        //}
    }

    public void closeRealm(){
        //if(myRealm != null && !myRealm.isClosed()){
            myRealm.close();
            Log.d(TAG, "closeRealm");
        //}
    }
}
