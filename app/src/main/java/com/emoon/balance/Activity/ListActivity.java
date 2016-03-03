package com.emoon.balance.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.R;

import io.realm.Realm;

public class ListActivity extends AppCompatActivity {

    private Realm myRealm;
    private String balanceType;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        fab = (FloatingActionButton) findViewById(R.id.fab);





        //get intents from caller activity
        balanceType = (getIntent().getExtras().getString(Constants.REQUEST_LIST_OTHER_TYPE));

        createToolbar();


        init();
        addListeners();
    }

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

        //get
    }

    private void addListeners(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
