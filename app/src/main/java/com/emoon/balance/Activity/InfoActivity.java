package com.emoon.balance.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

    private ImageButton addCostBtn;

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
        addCostBtn = (ImageButton) findViewById(R.id.addCostButton);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        addCostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numCost++;

                if (numCost <= 5) {
                    Cost cost = new Cost();
                    cost.setId(Util.generateUUID());

                    costList.add(cost);
                    costAdapter.notifyDataSetChanged();
                }
            }
        });
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
