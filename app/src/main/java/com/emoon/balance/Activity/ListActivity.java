package com.emoon.balance.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.emoon.balance.Adapter.ListAdapter;
import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.EarnBurn;
import com.emoon.balance.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ListActivity extends AppCompatActivity {

    private Realm myRealm;
    private String balanceType;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private ListView listView;
    private List<EarnBurn> itemList;
    private ListAdapter listAdapter;
    private RealmResults<EarnBurn> realmResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        listView = (ListView) findViewById(R.id.listView);
        itemList = new ArrayList<>();
        listAdapter = new ListAdapter(getApplicationContext(), itemList);
        listView.setAdapter(listAdapter);

        //get intents from caller activity
        balanceType = (getIntent().getExtras().getString(Constants.REQUEST_LIST_OTHER_TYPE));

        createToolbar();

        init();
        addListeners();
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

        toolbar.setNavigationIcon(R.drawable.svg_ic_back);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        if(getSupportActionBar() != null){
            if(balanceType.equalsIgnoreCase(BalanceType.BURN.toString())){
                getSupportActionBar().setTitle("Burn");
                toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                fab.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));

            }else{
                getSupportActionBar().setTitle("Earn");
                toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                fab.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void init(){
        myRealm = Realm.getDefaultInstance();

        realmResults = myRealm.where(EarnBurn.class).equalTo("type", balanceType).findAllAsync();
        realmResults.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                Log.d("ZHAN", "There are "+realmResults.size()+" items 1");

                itemList = myRealm.copyFromRealm(realmResults);
                Log.d("ZHAN", "There are "+itemList.size()+" items 2");

                listAdapter.clear();
                listAdapter.addAll(itemList);
                listAdapter.notifyDataSetChanged();

                realmResults.removeChangeListener(this);
            }
        });

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
                intent.putExtra(Constants.REQUEST_CREATE_NEW, balanceType);
                startActivity(intent);
            }
        });
    }

}
