package com.emoon.balance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.emoon.balance.Adapter.EarnBurnHorizontalListAdapter;
import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.EarnBurn;
import com.emoon.balance.Util.Util;
import com.zhan.library.CircularView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private TextView headerText;
    private ViewGroup earnBtn;
    private ViewGroup burnBtn;

    private ImageView earnView;
    private ImageView burnView;

    private RecyclerView earnRecyclerView;
    private RecyclerView burnRecyclerView;

    private RoundCornerProgressBar earnProgress;
    private RoundCornerProgressBar burnProgress;

    private int maxEarn = 20;
    private int maxBurn = 20;

    private int total = 0;

    private List<EarnBurn> earnList;
    private List<EarnBurn> burnList;

    private EarnBurnHorizontalListAdapter earnAdapter;
    private EarnBurnHorizontalListAdapter burnAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        init();
    }

    private void init(){
        headerText = (TextView) findViewById(R.id.topPanelHeader);
        earnBtn = (ViewGroup) findViewById(R.id.earnPanel);
        burnBtn = (ViewGroup) findViewById(R.id.burnPanel);

        earnProgress = (RoundCornerProgressBar) findViewById(R.id.earnProgressBar);
        burnProgress = (RoundCornerProgressBar) findViewById(R.id.burnProgressBar);

        earnView = (ImageView) findViewById(R.id.earnView);
        burnView = (ImageView) findViewById(R.id.burnView);

        earnRecyclerView = (RecyclerView) findViewById(R.id.earnRecyclerView);
        burnRecyclerView = (RecyclerView) findViewById(R.id.burnRecyclerView);

        burnList = new ArrayList<>();
        burnAdapter = new EarnBurnHorizontalListAdapter(this, burnList);
        burnRecyclerView.setAdapter(burnAdapter);
        burnRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        earnList = new ArrayList<>();
        earnAdapter = new EarnBurnHorizontalListAdapter(this, earnList);
        earnRecyclerView.setAdapter(earnAdapter);
        earnRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        earnProgress.setMax(maxEarn);
        burnProgress.setMax(maxBurn);
        earnProgress.setProgress(0);
        burnProgress.setProgress(0);

        initRealm();
        putFakeData();
        addListeners();
    }

    private void initRealm(){
        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext())
                .name(Constants.REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private void putFakeData(){
        for(int i = 0; i < 14; i ++){
            EarnBurn earn = new EarnBurn();
            earn.setId(Util.generateUUID());
            earn.setName("Earn " + i);
            earn.setType(BalanceType.EARN.toString());

            earnList.add(earn);
        }

        for(int i = 0; i < 14; i ++){
            EarnBurn burn = new EarnBurn();
            burn.setId(Util.generateUUID());
            burn.setName("Burn " + i);
            burn.setType(BalanceType.BURN.toString());

            burnList.add(burn);
        }

        earnAdapter.notifyDataSetChanged();
        burnAdapter.notifyDataSetChanged();
    }

    private void addListeners(){
        earnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total++;
                headerText.setText(addSign(total));
                setProgressBar();

                displayEarnItems(true);
            }
        });

        burnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total--;
                headerText.setText(addSign(total));
                setProgressBar();

                displayBurnItems(true);
            }
        });



        burnAdapter.setOnItemClickListener(new EarnBurnHorizontalListAdapter.EarnBurnInterfaceListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("MAIN", "burn adapter:" + false);
                //after clicking an item, close it
                //displayBurnItems(false);
                addEarnBurnTransaction(burnList.get(position));
            }
        });

        earnAdapter.setOnItemClickListener(new EarnBurnHorizontalListAdapter.EarnBurnInterfaceListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("MAIN", "earn adapter:" + false);
                //after clicking an item, close it
                //displayEarnItems(false);
                addEarnBurnTransaction(earnList.get(position));
            }
        });
    }

    /**
     * Displays prompt for user to add new EarnBurn.
     */
    private void addEarnBurnTransaction(EarnBurn data){
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        //It is ok to put null as the 2nd parameter as this custom layout is being attached to a
        //AlertDialog, where it not necessary to know what the parent is.
        View promptView = layoutInflater.inflate(R.layout.alertdialog_generic, null);

        final EditText input = (EditText) promptView.findViewById(R.id.genericEditText);
        input.setHint(data.getType());

        TextView title = (TextView) promptView.findViewById(R.id.genericTitle);
        title.setText(data.getName());

        CircularView cv = (CircularView) promptView.findViewById(R.id.genericCircularView);

        if(data.getType().equalsIgnoreCase(BalanceType.BURN.toString())){
            cv.setCircleColor(R.color.dark_blue);
            cv.setIconResource(R.drawable.svg_ic_add);
        }else{
            cv.setCircleColor(R.color.dark_red);
            cv.setIconResource(R.drawable.ic_person);
        }

        new AlertDialog.Builder(this)
                .setView(promptView)
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        displayEarnItems(false);
                        displayBurnItems(false);
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*myRealm.beginTransaction();

                        Account account = myRealm.createObject(Account.class);
                        account.setId(Util.generateUUID());
                        account.setName(input.getText().toString());

                        accountListAdapter.clear();
                        myRealm.commitTransaction();*/

                        displayEarnItems(false);
                        displayBurnItems(false);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        displayEarnItems(false);
                        displayBurnItems(false);
                    }
                })
                .create()
                .show();
    }

    private String addSign(int value){
        if(value > 0){
            return "+"+value;
        }else{
            return ""+value;
        }
    }

    private void displayEarnItems(boolean value){ Log.d("MAIN", "display earn items :"+value);
        if(value) { //display horizontal list view
            earnRecyclerView.setVisibility(View.VISIBLE);
            earnView.setVisibility(View.GONE);
        }else{ //hide horizontal list view
            earnRecyclerView.setVisibility(View.GONE);
            earnView.setVisibility(View.VISIBLE);
        }
    }

    private void displayBurnItems(boolean value){ Log.d("MAIN", "display burn items :"+value);
        if(value){ //display horizontal list view
            burnRecyclerView.setVisibility(View.VISIBLE);
            burnView.setVisibility(View.GONE);
        }else{ //hide horizontal list view
            burnRecyclerView.setVisibility(View.GONE);
            burnView.setVisibility(View.VISIBLE);
        }
    }

    private void setProgressBar(){
        if(total > 0){
            earnProgress.setProgress(total);
            burnProgress.setProgress(0);
            headerText.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else if(total == 0){
            earnProgress.setProgress(0);
            burnProgress.setProgress(0);
            headerText.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }else{
            earnProgress.setProgress(0);
            burnProgress.setProgress(Math.abs(total));
            headerText.setTextColor(ContextCompat.getColor(this, R.color.blue));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
