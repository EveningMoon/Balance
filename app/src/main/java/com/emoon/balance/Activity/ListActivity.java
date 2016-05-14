package com.emoon.balance.Activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.emoon.balance.Adapter.ListAdapter;
import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.EarnBurn;
import com.emoon.balance.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ListActivity extends BaseActivity {

    private static final String TAG = "ListActivity";

    private String balanceType;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private ListView listView;
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
        listView = (ListView) findViewById(R.id.listView);
        itemList = new ArrayList<>();
        listAdapter = new ListAdapter(getApplicationContext(), itemList);
        listView.setAdapter(listAdapter);

        //get intent's data from caller activity
        balanceType = (getIntent().getExtras().getString(Constants.REQUEST_LIST_OTHER_TYPE));
        Log.d(TAG, "balance type :"+balanceType);

        createToolbar();
        addListeners();
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

                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                intent.putExtra(Constants.REQUEST_IS_EDIT_EARNBURN, true);
                intent.putExtra(Constants.REQUEST_EDIT_EARNBURN, itemList.get(position).getId());
                startActivity(intent);
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
}
