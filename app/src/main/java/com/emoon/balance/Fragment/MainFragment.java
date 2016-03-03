package com.emoon.balance.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.emoon.balance.Activity.ListActivity;
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

    private RoundCornerProgressBar earnProgress;
    private RoundCornerProgressBar burnProgress;

    private final int MAX_EARN = 20;
    private final int MAX_BURN = 20;

    private int total = 0;

    private RealmResults<EarnBurn> earnRealmResults;
    private RealmResults<EarnBurn> burnRealmResults;

    private List<EarnBurn> earnList;
    private List<EarnBurn> burnList;

    private ViewGroup earnGroup;
    private CircularView topEarn1;
    private CircularView topEarn2;
    private CircularView topEarn3;
    private CircularView otherEarn;

    private ViewGroup burnGroup;
    private CircularView topBurn1;
    private CircularView topBurn2;
    private CircularView topBurn3;
    private CircularView otherBurn;




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
        //Util.getListOfActivities(getContext());
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

        earnGroup = (LinearLayout) view.findViewById(R.id.earnGroup);
        burnGroup = (LinearLayout) view.findViewById(R.id.burnGroup);

        topEarn1 = (CircularView) view.findViewById(R.id.topEarn1);
        topEarn2 = (CircularView) view.findViewById(R.id.topEarn2);
        topEarn3 = (CircularView) view.findViewById(R.id.topEarn3);
        otherEarn = (CircularView) view.findViewById(R.id.otherEarn);

        topBurn1 = (CircularView) view.findViewById(R.id.topBurn1);
        topBurn2 = (CircularView) view.findViewById(R.id.topBurn2);
        topBurn3 = (CircularView) view.findViewById(R.id.topBurn3);
        otherBurn = (CircularView) view.findViewById(R.id.otherBurn);

        burnList = new ArrayList<>();
        earnList = new ArrayList<>();

        earnProgress.setMax(MAX_EARN);
        burnProgress.setMax(MAX_BURN);
        earnProgress.setProgress(0);
        burnProgress.setProgress(0);

        addListeners();
        addUnits();
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


        topBurn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top 1 burn is " + burnList.get(0).getName());
                addEarnBurnTransaction(burnList.get(0));
            }
        });

        topBurn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top  2 burn is "+burnList.get(1).getName());
                addEarnBurnTransaction(burnList.get(1));
            }
        });

        topBurn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top 3 burn is " + burnList.get(2).getName());
                addEarnBurnTransaction(burnList.get(2));
            }
        });

        otherBurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "other burn ");
                goToOther(BalanceType.BURN);
            }
        });

        topEarn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top 1 earn is " + earnList.get(0).getName());
                addEarnBurnTransaction(earnList.get(0));
            }
        });

        topEarn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top  2 earn is "+earnList.get(1).getName());
                addEarnBurnTransaction(earnList.get(1));
            }
        });

        topEarn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top 3 earn is "+earnList.get(2).getName());
                addEarnBurnTransaction(earnList.get(2));
            }
        });

        otherEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "other earn ");
                goToOther(BalanceType.EARN);
            }
        });

    }

    private void goToOther(BalanceType type){
        Intent intent = new Intent(getContext(), ListActivity.class);
        intent.putExtra(Constants.REQUEST_LIST_OTHER_TYPE, type.toString());
        startActivity(intent);
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
     * Displays prompt for user to add new transaction.
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
        cv.setIconResource(Util.getIconID(getContext(), data.getIcon()));

        if(data.getType().equalsIgnoreCase(BalanceType.BURN.toString())){
            cv.setCircleColor(R.color.dark_blue);
            cv.setIconColor(R.color.icon_blue);
        }else{
            cv.setCircleColor(R.color.dark_red);
            cv.setIconColor(R.color.icon_red);
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

                    earnView.setVisibility(View.GONE);
                    earnGroup.setVisibility(View.VISIBLE);

                    topEarn1.setIconResource(Util.getIconID(getContext(), earnList.get(0).getIcon()));
                    topEarn2.setIconResource(Util.getIconID(getContext(), earnList.get(1).getIcon()));
                    topEarn3.setIconResource(Util.getIconID(getContext(), earnList.get(2).getIcon()));
                    otherEarn.setIconResource(R.drawable.svg_other);

                    earnRealmResults.removeChangeListener(this);
                }
            });
        }else{ //hide horizontal list view
            earnGroup.setVisibility(View.GONE);
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

                    burnGroup.setVisibility(View.VISIBLE);
                    burnView.setVisibility(View.GONE);

                    topBurn1.setIconResource(Util.getIconID(getContext(), burnList.get(0).getIcon()));
                    topBurn2.setIconResource(Util.getIconID(getContext(), burnList.get(1).getIcon()));
                    topBurn3.setIconResource(Util.getIconID(getContext(), burnList.get(2).getIcon()));
                    otherBurn.setIconResource(R.drawable.svg_other);

                    burnRealmResults.removeChangeListener(this);
                }
            });
        }else{ //hide horizontal list view
            burnGroup.setVisibility(View.GONE);
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
