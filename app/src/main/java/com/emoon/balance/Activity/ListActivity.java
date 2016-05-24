package com.emoon.balance.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.emoon.balance.Adapter.ListAdapter;
import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.EarnBurn;
import com.emoon.balance.Model.Transaction;
import com.emoon.balance.R;
import com.emoon.balance.Util.Util;
import com.emoon.balance.View.ExtendedNumberPicker;
import com.zhan.library.CircularView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ListActivity extends BaseRealmActivity {

    private static final String TAG = "ListActivity";

    private String balanceType;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private SwipeMenuListView listView;
    private List<EarnBurn> itemList;
    private ListAdapter listAdapter;
    private RealmResults<EarnBurn> realmResults;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_list;
    }

/*
    private void changeTheme(){
        ContextThemeWrapper themeWrapper = new ContextThemeWrapper(this, R.style.AppThemeWithColorScheme2);
        LayoutInflater layoutInflater = LayoutInflater.from(themeWrapper);
        viewContainer.removeAllViews();
        layoutInflater.inflate(R.layout.my_layout, viewContainer, true );
    }*/

    /**
     * Create toolbar
     */
    private void createToolbar(){
        //Create the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar.setNavigationIcon(R.drawable.svg_ic_back);
        //toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        if(getSupportActionBar() != null){
            if(balanceType.equalsIgnoreCase(BalanceType.BURN.toString())){
                getSupportActionBar().setTitle("Rewards");
                toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue)));

                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                    Window window = getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    // finally change the color
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.dark_blue));
                }
            }else{
                getSupportActionBar().setTitle("Activities");
                toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));

                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                    Window window = getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    // finally change the color
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.dark_red));
                }
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void init(){
        super.init();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        listView = (SwipeMenuListView) findViewById(R.id.listView);
        itemList = new ArrayList<>();
        listAdapter = new ListAdapter(getApplicationContext(), itemList);
        listView.setAdapter(listAdapter);

        //get intent's data from caller activity
        balanceType = (getIntent().getExtras().getString(Constants.REQUEST_LIST_OTHER_TYPE));
        Log.d(TAG, "balance type :"+balanceType);

        createToolbar();
        addListeners();
        createSwipeMenu();
    }

    private void addListeners(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                intent.putExtra(Constants.REQUEST_IS_EDIT_EARNBURN, false);
                intent.putExtra(Constants.REQUEST_CREATE_EARNBURN, balanceType);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "click on item :"+itemList.get(position).getId()+", "+itemList.get(position).getName());

                /*
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                intent.putExtra(Constants.REQUEST_IS_EDIT_EARNBURN, true);
                intent.putExtra(Constants.REQUEST_EDIT_EARNBURN, itemList.get(position).getId());
                startActivity(intent);
                */

                addEarnBurnTransaction(itemList.get(position));
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();

        realmResults = myRealm.where(EarnBurn.class).equalTo("type", balanceType).findAllAsync();
        realmResults.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                Log.d("ZHAN", "There are " + realmResults.size() + " items 1");
                itemList = myRealm.copyFromRealm(realmResults);
                Log.d("ZHAN", "There are " + itemList.size() + " items 2");

                listAdapter.clear();
                listAdapter.addAll(itemList);
                //listAdapter.notifyDataSetChanged();

                realmResults.removeChangeListener(this);
            }
        });
    }

    private void createSwipeMenu(){
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem editItem = new SwipeMenuItem(getBaseContext());
                editItem.setBackground(R.color.green);
                editItem.setWidth(Util.dp2px(getBaseContext(), 90));
                editItem.setIcon(R.drawable.svg_ic_edit);
                menu.addMenuItem(editItem);
            }
        };
        //set creator
        listView.setMenuCreator(creator);

        //add listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch(index){
                    case 0 :
                        //edit
                        Toast.makeText(getBaseContext(), "edit on position"+position, Toast.LENGTH_SHORT).show();
                        editItem(position);
                        break;
                }

                //false: close the menu
                //true: dont close
                return false;
            }
        });
    }

    private void editItem(int position){
        Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
        intent.putExtra(Constants.REQUEST_IS_EDIT_EARNBURN, true);
        intent.putExtra(Constants.REQUEST_EDIT_EARNBURN, itemList.get(position).getId());
        startActivity(intent);
    }

    /**
     * Displays prompt for user to add new transaction.
     */
    private void addEarnBurnTransaction(final EarnBurn data1){
        final EarnBurn data = myRealm.where(EarnBurn.class).equalTo("id", data1.getId()).findFirst();

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        //It is ok to put null as the 2nd parameter as this custom layout is being attached to a
        //AlertDialog, where it not necessary to know what the parent is.
        View promptView = layoutInflater.inflate(R.layout.alertdialog_generic, null);

        //Title
        TextView title = (TextView) promptView.findViewById(R.id.genericTitle);
        title.setText(data.getName());

        //Circular view
        CircularView cv = (CircularView) promptView.findViewById(R.id.genericCircularView);
        cv.setIconResource(Util.getIconID(this, data.getIcon()));

        if(data.getType().equalsIgnoreCase(BalanceType.BURN.toString())){
            cv.setCircleColor(R.color.dark_blue);
            cv.setIconColor(R.color.icon_blue);
        }else{
            cv.setCircleColor(R.color.dark_red);
            cv.setIconColor(R.color.icon_red);
        }

        //Edit text
        final EditText input = (EditText) promptView.findViewById(R.id.genericEditText);
        input.setHint(data.getType());

        //NumberPicker
        final ExtendedNumberPicker unitNumberPicker = (ExtendedNumberPicker) promptView.findViewById(R.id.genericNumberPicker);

        List<String> ssList = new ArrayList<>();
        for(int i = 0; i < data.getCostList().size(); i++){
            ssList.add(data.getCostList().get(i).getUnitType());
        }

        final String[] values = ssList.toArray(new String[0]);

        unitNumberPicker.setMinValue(0);

        //If this earnBurn have been init with values
        if(values.length > 0){
            unitNumberPicker.setMaxValue(values.length - 1);
            unitNumberPicker.setDisplayedValues(values);
            unitNumberPicker.setWrapSelectorWheel(true);

            final AlertDialog ss = new AlertDialog.Builder(this)
                    .setView(promptView)
                    .setCancelable(true)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (!input.getText().toString().isEmpty()) {
                                myRealm.beginTransaction();
                                Transaction transaction = myRealm.createObject(Transaction.class);
                                transaction.setId(Util.generateUUID());
                                transaction.setDate(new Date());
                                transaction.setEarnBurn(data);
                                transaction.setUnitCost(Integer.parseInt(input.getText().toString()));
                                transaction.setCostType(values[unitNumberPicker.getValue()]);
                                myRealm.commitTransaction();

                                //go back to previous activity once user has added a transaction
                                finish();
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
                        ss.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.blue));
                        ss.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.blue));
                    }else{
                        //RED
                        ss.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
                        ss.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
                    }
                }
            });

            ss.show();
        }else{
            Intent firstTimeEarnBurn = new Intent(getBaseContext(), InfoActivity.class);
            firstTimeEarnBurn.putExtra(Constants.REQUEST_IS_EDIT_EARNBURN, true);
            firstTimeEarnBurn.putExtra(Constants.REQUEST_EDIT_EARNBURN, data.getId());
            startActivity(firstTimeEarnBurn);
        }
    }

}
