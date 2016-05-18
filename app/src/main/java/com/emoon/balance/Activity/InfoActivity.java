package com.emoon.balance.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.emoon.balance.Adapter.CostAdapter;
import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.Cost;
import com.emoon.balance.Model.EarnBurn;
import com.emoon.balance.Model.UnitType;
import com.emoon.balance.R;
import com.emoon.balance.Util.Util;
import com.emoon.balance.View.ExtendedNumberPicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.RealmList;
import io.realm.RealmResults;

public class InfoActivity extends BaseRealmActivity {

    private static final String TAG = "InfoActivity";

    private Toolbar toolbar;

    private FloatingActionButton fab;

    private boolean isEditMode;

    private String balanceType;
    private CostAdapter costAdapter;
    private SwipeMenuListView costListView;
    private List<Cost> costList;
    private EditText nameEditText;

    private EarnBurn editEarnBurn;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_info;
    }

    @Override
    protected void init(){
        super.init();

        costListView = (SwipeMenuListView) findViewById(R.id.costListView);
        costList = new ArrayList<>();
        costAdapter = new CostAdapter(this, costList);
        costListView.setAdapter(costAdapter);

        nameEditText = (EditText)findViewById(R.id.nameEditText);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        //Get intent's data of whether the user is editing or creating new earnBurn
        isEditMode = (getIntent().getExtras().getBoolean(Constants.REQUEST_IS_EDIT_EARNBURN));

        if(isEditMode) {
            String id = (getIntent().getExtras().getString(Constants.REQUEST_EDIT_EARNBURN));
            editEarnBurn = myRealm.where(EarnBurn.class).equalTo("id", id).findFirst();
            balanceType = editEarnBurn.getType();

            //Set name
            nameEditText.setText(editEarnBurn.getName());

            //Set list of cost attached to this earnBurn
            costList = deepCopyCost(editEarnBurn.getCostList());
            costAdapter.clear();
            costAdapter.addAll(costList);

            Log.d(TAG, "in edit mode : "+editEarnBurn.getType()+", there are orig cost size : "+costList.size());
        }else{
            //Get intent's data of which type of earnBurn to show (Activity or Reward)
            balanceType = (getIntent().getExtras().getString(Constants.REQUEST_CREATE_EARNBURN));

            Log.d(TAG, "in add new mode : "+balanceType);
        }

        unitsThisEarnBurnHave = new ArrayList<>();

        if(isEditMode){
            String id = (getIntent().getExtras().getString(Constants.REQUEST_EDIT_EARNBURN));
            EarnBurn eb = myRealm.where(EarnBurn.class).equalTo("id", id).findFirst();
            List<Cost> ccList = eb.getCostList();

            //convert cost into string
            for(int i = 0; i < ccList.size(); i++){
                unitsThisEarnBurnHave.add(ccList.get(i).getUnitType());
                Log.d(TAG, editEarnBurn.getName()+" has unit type "+ccList.get(i).getUnitType());
            }
            Log.d(TAG ,"orig unit size :"+unitsThisEarnBurnHave.size());
        }

        createToolbar();
        addListeners();
        createSwipeMenu();
    }

    /**
     * Create toolbar
     */
    private void createToolbar(){
        //Create the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.svg_ic_close);

        if(getSupportActionBar() != null){
            if(balanceType.equalsIgnoreCase(BalanceType.BURN.toString())){
                if(isEditMode){
                    getSupportActionBar().setTitle("Edit Reward");
                }else{
                    getSupportActionBar().setTitle("Add Reward");
                }

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
                if(isEditMode){
                    getSupportActionBar().setTitle("Edit Activity");
                }else{
                    getSupportActionBar().setTitle("Add Activity");
                }

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
        }
    }

    private void addListeners(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        costListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showToast("click on "+position);
                editCostDialog(position);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(costList.size() < 5){
                    createNewCostDialog();
                }
            }
        });
    }

    private void showToast(String value){
        Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
    }

    private EditText pointsEditText, valueEditText;
    private ExtendedNumberPicker measureNumberPicker;
    private Cost editCost;
    private void editCostDialog(final int position){
        // get cost.xml view
        LayoutInflater layoutInflater = getLayoutInflater();

        //It is ok to put null as the 2nd parameter as this custom layout is being attached to a
        //AlertDialog, where it not necessary to know what the parent is.
        View promptView = layoutInflater.inflate(R.layout.alertdialog_cost, null);

        editCost = costList.get(position);

        pointsEditText = (EditText) promptView.findViewById(R.id.pointsEditText);
        valueEditText = (EditText) promptView.findViewById(R.id.valueEditText);
        measureNumberPicker = (ExtendedNumberPicker) promptView.findViewById(R.id.measureNumberPicker);

        pointsEditText.setHint("Points earned per");

        pointsEditText.setText(String.valueOf(editCost.getPointsEarnPer()));
        valueEditText.setText(String.valueOf(editCost.getUnitCost()));

        final String[] values = Util.getListOfUnits();

        measureNumberPicker.setMinValue(0);
        measureNumberPicker.setMaxValue(values.length - 1);
        measureNumberPicker.setDisplayedValues(values);
        measureNumberPicker.setWrapSelectorWheel(false);

        for(int i = 0; i < values.length; i++){
            if(values[i].equalsIgnoreCase(editCost.getUnitType())){
                measureNumberPicker.setValue(i);
                break;
            }
        }

        measureNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected value from picker
                Log.d("WHEEL", "Selected value : " + values[newVal]);
                selectedNumberPickerIndex = newVal;
            }
        });
        Log.d("ZHAP", "dialog ");

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(promptView)
                .setTitle("Add new cost")
                .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("ZHAP", "click done on dialog "+position);


                        if (Util.isNotNullNotEmptyNotWhiteSpaceOnlyByJava(pointsEditText.getText().toString()) &&
                                Util.isNotNullNotEmptyNotWhiteSpaceOnlyByJava(valueEditText.getText().toString())) {

                            editCost.setPointsEarnPer(Integer.parseInt(pointsEditText.getText().toString()));
                            editCost.setUnitCost(Integer.parseInt(valueEditText.getText().toString()));
                            editCost.setUnitType(values[selectedNumberPickerIndex]);
                            costList.set(position, editCost);
                            costAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please input a value", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog noteDialog = builder.create();
        noteDialog.show();
    }

    int selectedNumberPickerIndex;


    private List<String> unitsThisEarnBurnHave;

    private void createNewCostDialog(){
        // get cost.xml view
        LayoutInflater layoutInflater = getLayoutInflater();

        //It is ok to put null as the 2nd parameter as this custom layout is being attached to a
        //AlertDialog, where it not necessary to know what the parent is.
        View promptView = layoutInflater.inflate(R.layout.alertdialog_cost, null);

        final EditText points = (EditText) promptView.findViewById(R.id.pointsEditText);
        final EditText value = (EditText) promptView.findViewById(R.id.valueEditText);
        final ExtendedNumberPicker measureNumberPicker = (ExtendedNumberPicker) promptView.findViewById(R.id.measureNumberPicker);

        //Gets all list of units
        final List<String> values1 = Util.getListOfUnits1();

        //Display only the difference (to avoid duplicated of units)
        Set<String> ad1 = new HashSet<>(values1); //contains all
        Set<String> ad2 = new HashSet<>(unitsThisEarnBurnHave); //contains subset
        ad1.removeAll(ad2);//find differences

        //Convert set back into array
        final String[] valueDiff = ad1.toArray(new String[ad1.size()]);

        measureNumberPicker.setMinValue(0);
        measureNumberPicker.setMaxValue(valueDiff.length - 1);
        measureNumberPicker.setDisplayedValues(valueDiff);
        measureNumberPicker.setWrapSelectorWheel(false);

        //measureNumberPicker.setValue(3);
        selectedNumberPickerIndex = 0;

        measureNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected value from picker
                Log.d("WHEEL", "Selected value : " + valueDiff[newVal]);
                selectedNumberPickerIndex = newVal;
            }
        });

        points.setHint("Points earned per");

        final Cost cost = new Cost();
        cost.setId(Util.generateUUID());

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(promptView)
                .setTitle("Add new cost")
                .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Util.isNotNullNotEmptyNotWhiteSpaceOnlyByJava(points.getText().toString()) &&
                                Util.isNotNullNotEmptyNotWhiteSpaceOnlyByJava(value.getText().toString())) {

                            cost.setPointsEarnPer(Integer.parseInt(points.getText().toString()));
                            cost.setUnitCost(Integer.parseInt(value.getText().toString()));
                            cost.setUnitType(valueDiff[selectedNumberPickerIndex]);

                            costList.add(cost);

                            Log.d(TAG, "after adding, new cost size : "+costList.size());

                            costAdapter.clear();
                            costAdapter.addAll(costList);

                            //remove unit from list for future available values to add.
                            //Do this by adding to list "unitsThisEarnBurnHave"
                            unitsThisEarnBurnHave.add(valueDiff[selectedNumberPickerIndex]);
                            Log.d(TAG, "after adding, new unit size : "+unitsThisEarnBurnHave.size());


                        } else {
                            Toast.makeText(getApplicationContext(), "Please input a value", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog noteDialog = builder.create();
        noteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        noteDialog.show();
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
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(R.color.red);// set item background
                deleteItem.setWidth(Util.dp2px(getApplicationContext(), 90));// set item width
                deleteItem.setIcon(R.drawable.svg_ic_delete);// set a icon
                menu.addMenuItem(deleteItem);// add to menu
            }
        };
        //set creator
        costListView.setMenuCreator(creator);

        // step 2. listener item click event
        costListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // delete
                        //Toast.makeText(getApplicationContext(), "DELETING @ "+position+" "+costList.remove(position).getUnitType(), Toast.LENGTH_SHORT).show();

                        //remove from list "unitsThisEarnBurnHave"
                        //unitsThisEarnBurnHave.remove(costList.get(position).getUnitType());

                        Log.d(TAG, "removing at "+position);

                        Log.d(TAG, "--> units : "+unitsThisEarnBurnHave.size()+", costLIst : "+costList.size());


                        Log.d(TAG ,"1 units :"+unitsThisEarnBurnHave.size());
                        unitsThisEarnBurnHave.remove(position);
                        Log.d(TAG ,"2 units :"+unitsThisEarnBurnHave.size());

                        Log.d(TAG ,"1 cost :"+costList.size());
                        costList.remove(position);
                        Log.d(TAG ,"2 cost :"+costList.size());

                        costAdapter.clear();
                        costAdapter.addAll(costList);
                        //costAdapter.notifyDataSetChanged();

                        break;
                }
                //False: Close the menu
                //True: Did not close the menu
                return false;
            }
        });

        // set SwipeListener
        costListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

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

    private void save(){
        if(!nameEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "SAVING", Toast.LENGTH_SHORT).show();
            myRealm.beginTransaction();

            if(isEditMode){
                editEarnBurn.setName(nameEditText.getText().toString());
                editEarnBurn.setCostList(new RealmList<Cost>());

                Log.d(TAG, "------- SAVING ------");
                for(int i = 0; i < costList.size(); i++){
                    Log.d(TAG, i+") cost :"+costList.get(i).getId()+"-> "+ costList.get(i).getPointsEarnPer()+", "+costList.get(i).getUnitCost()+", "+costList.get(i).getUnitType());
                    Cost cost = myRealm.createObject(Cost.class);
                    cost.setId(costList.get(i).getId());
                    cost.setPointsEarnPer(costList.get(i).getPointsEarnPer());
                    cost.setUnitCost(costList.get(i).getUnitCost());
                    cost.setUnitType(costList.get(i).getUnitType());

                    editEarnBurn.getCostList().add(cost);
                }
                Log.d(TAG, "------- DONE SAVING ------");

                myRealm.copyToRealmOrUpdate(editEarnBurn);
            }else{
                EarnBurn earnBurn = myRealm.createObject(EarnBurn.class);
                earnBurn.setId(Util.generateUUID());
                earnBurn.setName(nameEditText.getText().toString());
                earnBurn.setType(balanceType);
                earnBurn.setIcon("svg_running_stick_figure");

                Log.d(TAG, "------- SAVING ------");
                for(int i = 0; i < costList.size(); i++){
                    Log.d(TAG, i+") cost :"+costList.get(i).getId()+"-> "+ costList.get(i).getPointsEarnPer()+", "+costList.get(i).getUnitCost()+", "+costList.get(i).getUnitType());
                    Cost cost = myRealm.createObject(Cost.class);
                    cost.setId(costList.get(i).getId());
                    cost.setPointsEarnPer(costList.get(i).getPointsEarnPer());
                    cost.setUnitCost(costList.get(i).getUnitCost());
                    cost.setUnitType(costList.get(i).getUnitType());

                    earnBurn.getCostList().add(cost);
                }
                Log.d(TAG, "------- DONE SAVING ------");
            }

            myRealm.commitTransaction();
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Please input a name for this "+balanceType.toLowerCase(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Do this so that we dont copy Realm version of Cost class which will throw an exception if
     * we try to modify data when not inside a transaction.
     *
     */
    private List<Cost> deepCopyCost(List<Cost> costList){
        List<Cost> newCostList = new ArrayList<>();
        for(int i = 0; i < costList.size(); i++){
            Cost cost = new Cost();
            cost.setId(costList.get(i).getId());
            cost.setPointsEarnPer(costList.get(i).getPointsEarnPer());
            cost.setUnitCost(costList.get(i).getUnitCost());
            cost.setUnitType(costList.get(i).getUnitType());
            newCostList.add(cost);
        }
        return newCostList;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Menu
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.formSaveBtn) {
            save();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
