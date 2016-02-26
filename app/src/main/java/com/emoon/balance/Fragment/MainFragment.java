package com.emoon.balance.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.emoon.balance.Adapter.EarnBurnHorizontalListAdapter;
import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.EarnBurn;
import com.emoon.balance.Model.UnitType;
import com.emoon.balance.R;
import com.emoon.balance.Util.Util;
import com.zhan.library.CircularView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainFragment extends Fragment {

    private View view;

    private TextView headerText;
    private ViewGroup earnBtn;
    private ViewGroup burnBtn;

    private ImageView earnView;
    private ImageView burnView;

    private RecyclerView earnRecyclerView;
    private RecyclerView burnRecyclerView;

    private RoundCornerProgressBar earnProgress;
    private RoundCornerProgressBar burnProgress;

    private final int MAX_EARN = 20;
    private final int MAX_BURN = 20;

    private int total = 0;

    private RealmResults<EarnBurn> earnRealmResults;
    private RealmResults<EarnBurn> burnRealmResults;

    private List<EarnBurn> earnList;
    private List<EarnBurn> burnList;

    private EarnBurnHorizontalListAdapter earnAdapter;
    private EarnBurnHorizontalListAdapter burnAdapter;

    //Spinner
    private List<String> unitList;
    private ArrayAdapter<String> unitAdapter;

    private Realm myRealm;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init(){
        myRealm = Realm.getDefaultInstance();

        headerText = (TextView) view.findViewById(R.id.topPanelHeader);
        earnBtn = (ViewGroup) view.findViewById(R.id.earnPanel);
        burnBtn = (ViewGroup) view.findViewById(R.id.burnPanel);

        earnProgress = (RoundCornerProgressBar) view.findViewById(R.id.earnProgressBar);
        burnProgress = (RoundCornerProgressBar) view.findViewById(R.id.burnProgressBar);

        earnView = (ImageView) view.findViewById(R.id.earnView);
        burnView = (ImageView) view.findViewById(R.id.burnView);

        earnRecyclerView = (RecyclerView) view.findViewById(R.id.earnRecyclerView);
        burnRecyclerView = (RecyclerView) view.findViewById(R.id.burnRecyclerView);

        burnList = new ArrayList<>();
        burnAdapter = new EarnBurnHorizontalListAdapter(getActivity(), burnList);
        burnRecyclerView.setAdapter(burnAdapter);
        burnRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        earnList = new ArrayList<>();
        earnAdapter = new EarnBurnHorizontalListAdapter(getActivity(), earnList);
        earnRecyclerView.setAdapter(earnAdapter);
        earnRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        earnProgress.setMax(MAX_EARN);
        burnProgress.setMax(MAX_BURN);
        earnProgress.setProgress(0);
        burnProgress.setProgress(0);

        isFirstTime();
        addListeners();
        addUnits();
    }

    private void isFirstTime(){
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean isFirstTIme = sharedPreferences.getBoolean(Constants.FIRST_TIME, true);

        if(isFirstTIme){
            Toast.makeText(getContext(), "first time", Toast.LENGTH_SHORT).show();
            createDefaultEarnBurnData();

            //set Constants.FIRST_TIME shared preferences to false
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.FIRST_TIME, false);
            editor.apply();
        }else{
            Toast.makeText(getContext(), "second time", Toast.LENGTH_SHORT).show();
        }
    }

    private void createDefaultEarnBurnData(){
        myRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                for (int i = 0; i < 14; i++) {
                    EarnBurn earn = bgRealm.createObject(EarnBurn.class);
                    earn.setId(Util.generateUUID());
                    earn.setName("Earn " + i);
                    earn.setType(BalanceType.EARN.toString());

                    earnList.add(earn);
                }

                for (int i = 0; i < 14; i++) {
                    EarnBurn burn = bgRealm.createObject(EarnBurn.class);
                    burn.setId(Util.generateUUID());
                    burn.setName("Burn " + i);
                    burn.setType(BalanceType.BURN.toString());

                    burnList.add(burn);
                }
            }
        });
    }

    private void addListeners(){
        earnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //total++;
                headerText.setText(addSign(total));
                setProgressBar();

                displayEarnItems(true);
            }
        });

        burnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //total--;
                headerText.setText(addSign(total));
                setProgressBar();

                displayBurnItems(true);
            }
        });

        burnAdapter.setOnItemClickListener(new EarnBurnHorizontalListAdapter.EarnBurnInterfaceListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("MAIN", "burn adapter:" + false);
                addEarnBurnTransaction(burnList.get(position));
            }
        });

        earnAdapter.setOnItemClickListener(new EarnBurnHorizontalListAdapter.EarnBurnInterfaceListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("MAIN", "earn adapter:" + false);
                addEarnBurnTransaction(earnList.get(position));
            }
        });
    }

    public void addUnits(){
        unitList = new ArrayList<>();
        unitList.add(UnitType.MINUTE.toString());
        unitList.add(UnitType.HOUR.toString());
        unitList.add(UnitType.KM.toString());
        unitList.add(UnitType.MILE.toString());
        unitList.add(UnitType.QUANTITY.toString());

        unitAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, unitList);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    /**
     * Displays prompt for user to add new EarnBurn.
     */
    private void addEarnBurnTransaction(EarnBurn data){
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

        //It is ok to put null as the 2nd parameter as this custom layout is being attached to a
        //AlertDialog, where it not necessary to know what the parent is.
        View promptView = layoutInflater.inflate(R.layout.alertdialog_generic, null);

        //Title
        TextView title = (TextView) promptView.findViewById(R.id.genericTitle);
        title.setText(data.getName());

        //Circular view
        CircularView cv = (CircularView) promptView.findViewById(R.id.genericCircularView);

        if(data.getType().equalsIgnoreCase(BalanceType.BURN.toString())){
            cv.setCircleColor(R.color.dark_blue);
            cv.setIconResource(R.drawable.svg_ic_add);
        }else{
            cv.setCircleColor(R.color.dark_red);
            cv.setIconResource(R.drawable.ic_person);
        }

        //Edit text
        final EditText input = (EditText) promptView.findViewById(R.id.genericEditText);
        input.setHint(data.getType());

        //Spinner
        final Spinner unitSpinner = (Spinner) promptView.findViewById(R.id.genericSpinner);
        unitSpinner.setAdapter(unitAdapter);
        unitSpinner.setPrompt(unitList.get(0));
        unitSpinner.setSelected(true);

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                unitSpinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new AlertDialog.Builder(getActivity())
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

    private void displayEarnItems(boolean value){
        if(value) { //display horizontal list view
            earnRealmResults = myRealm.where(EarnBurn.class).equalTo("type", BalanceType.EARN.toString()).findAllAsync();
            earnRealmResults.addChangeListener(new RealmChangeListener() {
                @Override
                public void onChange() {
                    earnList = myRealm.copyFromRealm(earnRealmResults);
                    earnRecyclerView.setVisibility(View.VISIBLE);
                    earnView.setVisibility(View.GONE);
                    earnAdapter.setData(earnList);

                    earnRealmResults.removeChangeListener(this);
                }
            });
        }else{ //hide horizontal list view
            earnRecyclerView.setVisibility(View.GONE);
            earnView.setVisibility(View.VISIBLE);
        }
    }

    private void displayBurnItems(boolean value){
        if(value){ //display horizontal list view
            burnRealmResults = myRealm.where(EarnBurn.class).equalTo("type", BalanceType.BURN.toString()).findAllAsync();
            burnRealmResults.addChangeListener(new RealmChangeListener() {
                @Override
                public void onChange() {
                    burnList = myRealm.copyFromRealm(burnRealmResults);
                    burnRecyclerView.setVisibility(View.VISIBLE);
                    burnView.setVisibility(View.GONE);
                    burnAdapter.setData(burnList);

                    burnRealmResults.removeChangeListener(this);
                }
            });
        }else{ //hide horizontal list view
            burnRecyclerView.setVisibility(View.GONE);
            burnView.setVisibility(View.VISIBLE);
        }
    }

    private void setProgressBar(){
        if(total > 0){
            earnProgress.setProgress(total);
            burnProgress.setProgress(0);
            headerText.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }else if(total == 0){
            earnProgress.setProgress(0);
            burnProgress.setProgress(0);
            headerText.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
        }else{
            earnProgress.setProgress(0);
            burnProgress.setProgress(Math.abs(total));
            headerText.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(!myRealm.isClosed()) {
            myRealm.close();
        }
    }
}
