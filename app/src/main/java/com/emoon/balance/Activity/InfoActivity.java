package com.emoon.balance.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.R;

public class InfoActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText activityNameEditText, pointsEarnEditText1, timeValueEditText1, pointsEarnEditText2, distanceValueEditText;
    private Spinner timeSpinner, distanceSpinner;
    private FloatingActionButton fab;

    private String balanceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //get intents from caller activity
        balanceType = (getIntent().getExtras().getString(Constants.REQUEST_CREATE_NEW));

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
        activityNameEditText = (EditText) findViewById(R.id.activityNameEditText);
        pointsEarnEditText1 = (EditText) findViewById(R.id.pointsEarned1EditText);
        pointsEarnEditText2 = (EditText) findViewById(R.id.pointsEarned2EditText);
        timeValueEditText1 = (EditText) findViewById(R.id.timeValueEditText);
        distanceValueEditText = (EditText) findViewById(R.id.distanceValueEditText);

        timeSpinner = (Spinner) findViewById(R.id.timeMeasureSpinner);
        distanceSpinner = (Spinner) findViewById(R.id.distanceMeasureSpinner);


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
    }

    private void save(){

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
