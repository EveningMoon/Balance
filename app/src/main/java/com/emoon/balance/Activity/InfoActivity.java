package com.emoon.balance.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.emoon.balance.Adapter.CostAdapter;
import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.Cost;
import com.emoon.balance.Model.UnitType;
import com.emoon.balance.R;
import com.emoon.balance.Util.Util;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FloatingActionButton fab;

    private String balanceType;
    private CostAdapter costAdapter;
    private SwipeMenuListView costListView;
    private List<Cost> costList;


    private int numCost = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //get intents from caller activity
        balanceType = (getIntent().getExtras().getString(Constants.REQUEST_CREATE_NEW));

        createToolbar();
        init();
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
                getSupportActionBar().setTitle("Add Reward");
                toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
            }else{
                getSupportActionBar().setTitle("Add Activity");
                toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            }
        }
    }

    private void init(){

        costListView = (SwipeMenuListView) findViewById(R.id.costListView);
        costList = new ArrayList<>();
        costAdapter = new CostAdapter(this, costList);
        costListView.setAdapter(costAdapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);
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
                loadCostDialog(position);
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
    private Spinner measureSpinner;
    private Cost editCost;
    private void loadCostDialog(final int position){
        // get cost.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        //It is ok to put null as the 2nd parameter as this custom layout is being attached to a
        //AlertDialog, where it not necessary to know what the parent is.
        View promptView = layoutInflater.inflate(R.layout.alertdialog_cost, null);

        editCost = costList.get(position);

        pointsEditText = (EditText) promptView.findViewById(R.id.pointsEditText);
        valueEditText = (EditText) promptView.findViewById(R.id.valueEditText);
        measureSpinner = (Spinner) promptView.findViewById(R.id.measureSpinner);

        pointsEditText.setHint("Points earned per");

        pointsEditText.setText(editCost.getPointsEarnPer());
        valueEditText.setText(editCost.getUnitCost());
        //measure.setSelection(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(promptView)
                .setTitle("Add new cost")
                .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Util.isNotNullNotEmptyNotWhiteSpaceOnlyByJava(pointsEditText.getText().toString()) &&
                                Util.isNotNullNotEmptyNotWhiteSpaceOnlyByJava(valueEditText.getText().toString())) {

                            editCost.setPointsEarnPer(Integer.parseInt(pointsEditText.getText().toString()));
                            editCost.setUnitCost(Integer.parseInt(valueEditText.getText().toString()));
                            editCost.setUnitType(UnitType.HOUR.toString());

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
        noteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        noteDialog.show();
    }

    private void createNewCostDialog(){
        // get cost.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        //It is ok to put null as the 2nd parameter as this custom layout is being attached to a
        //AlertDialog, where it not necessary to know what the parent is.
        View promptView = layoutInflater.inflate(R.layout.alertdialog_cost, null);

        final EditText points = (EditText) promptView.findViewById(R.id.pointsEditText);
        final EditText value = (EditText) promptView.findViewById(R.id.valueEditText);
        final Spinner measure = (Spinner) promptView.findViewById(R.id.measureSpinner);

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
                            cost.setUnitType(UnitType.HOUR.toString());

                            costList.add(cost);
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
        noteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        noteDialog.show();
    }

    private void save(){
        Toast.makeText(getApplicationContext(), "SAVING", Toast.LENGTH_SHORT).show();

        for(int i = 0; i < costList.size(); i++){

            View v = getViewByPosition(i, costListView);


            Log.d("COST_DEBUG", i+" cost id : "+costList.get(i).getId());
            Log.d("COST_DEBUG", i+" cost points : "+((EditText)v.findViewById(R.id.pointsEditText)).getText());
            Log.d("COST_DEBUG", i+" cost unit : "+((EditText)v.findViewById(R.id.valueEditText)).getText());
            Log.d("COST_DEBUG", i+" cost type : "+((Spinner)v.findViewById(R.id.measureSpinner)).getSelectedItem().toString());
            Log.d("COST_DEBUG", "----------");
        }


    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
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
                        numCost--;
                        Toast.makeText(getApplicationContext(), "DELETING @ "+position, Toast.LENGTH_SHORT).show();
                        costList.remove(position);

                        costAdapter.notifyDataSetChanged();

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
