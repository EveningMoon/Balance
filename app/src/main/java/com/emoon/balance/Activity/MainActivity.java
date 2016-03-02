package com.emoon.balance.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Fragment.MainFragment;
import com.emoon.balance.Fragment.SettingFragment;
import com.emoon.balance.R;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private MainFragment mainFragment;
    private SettingFragment settingFragment;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createFragments();
        init();
    }

    private void init(){
        initRealm();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isFirstTime();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //code to load my fragment
        getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, mainFragment).commit();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void createFragments(){
        mainFragment = new MainFragment();
        settingFragment = new SettingFragment();
    }

    private void initRealm(){
        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext())
                .name(Constants.REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private void isFirstTime(){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        boolean isFirstTIme = sharedPreferences.getBoolean(Constants.FIRST_TIME, true);

        if(isFirstTIme){
            Toast.makeText(getApplicationContext(), "first time", Toast.LENGTH_SHORT).show();
            //createFakeTransactions();

            //set Constants.FIRST_TIME shared preferences to false
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.FIRST_TIME, false);
            editor.apply();
        }
    }
/*
    private void createFakeTransactions(){
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                String[] initialActivityName = new String[]{"Running","Walking","Biking"};

                //Create default activity
                for(int i = 0; i < initialActivityName.length; i++){
                    EarnBurn earn = bgRealm.createObject(EarnBurn.class);
                    earn.setId(Util.generateUUID());
                    earn.setName(initialActivityName[i]);
                    earn.setType(BalanceType.EARN.toString());
                    earn.setCost();
                    earn.setUnit();
                }


            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                // transaction is automatically rolled-back, do any cleanup here
                e.printStackTrace();
            }
        });
    }*/

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
        displayView(item.getItemId());
        return true;
    }

    private void displayView(final int viewId){
        //Close drawer first
        drawer.closeDrawer(GravityCompat.START);

        //Creates a 250 millisecond delay to remove lag when drawer is closing
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = null;
                String title = getString(R.string.app_name);

                switch (viewId) {
                    case R.id.nav_main:
                        fragment = mainFragment;
                        title = "Main";
                        break;
                    case R.id.nav_setting:
                        fragment = settingFragment;
                        title = "Setting";
                        break;
                }

                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.contentFrame, fragment);
                    ft.commit();
                }

                //set the toolbar title
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(title);
                }
            }
        }, 300);
    }
}
