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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.emoon.balance.Activity.InfoActivity;
import com.emoon.balance.Activity.ListActivity;
import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.EarnBurn;
import com.emoon.balance.Model.Transaction;
import com.emoon.balance.Model.UnitType;
import com.emoon.balance.R;
import com.emoon.balance.Util.BalancePreference;
import com.emoon.balance.Util.Util;
import com.emoon.balance.View.ExtendedNumberPicker;
import com.zhan.library.CircularView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

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

    private RealmResults<EarnBurn> earnRealmResults;
    private RealmResults<EarnBurn> burnRealmResults;

    private List<EarnBurn> earnList;
    private List<EarnBurn> burnList;

    private ViewGroup earnGroup;
    private ImageView topEarn1, topEarn2, topEarn3, otherEarn;

    private ViewGroup burnGroup;
    private ImageView topBurn1, topBurn2, topBurn3, otherBurn;

    private Realm myRealm;

    private TextView motivationTextView;

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
        initRealm();

        super.onActivityCreated(savedInstanceState);

        resumeRealm();
        isFirstTime();
    }

    private void initRealm(){
        RealmConfiguration config = new RealmConfiguration.Builder(getContext())
                .name(Constants.REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private void isFirstTime(){
        if(BalancePreference.getFirstTime(getContext())){
            BalancePreference.setFirstTime(getContext());
            createDefaultItems();
            init();
        }else{
            init();
        }
    }

    private void createDefaultItems(){
        List<EarnBurn> listOfActivity = Util.getListOfActivities(getContext());
        List<EarnBurn> listOfReward = Util.getListOfRewards(getContext());

        myRealm.beginTransaction();
        myRealm.copyToRealmOrUpdate(listOfActivity);
        myRealm.copyToRealmOrUpdate(listOfReward);
        myRealm.commitTransaction();
    }

    private void init(){
        headerText = (TextView) view.findViewById(R.id.topPanelHeader);
        earnBtn = (ViewGroup) view.findViewById(R.id.earnPanel);
        burnBtn = (ViewGroup) view.findViewById(R.id.burnPanel);

        earnProgress = (RoundCornerProgressBar) view.findViewById(R.id.earnProgressBar);
        burnProgress = (RoundCornerProgressBar) view.findViewById(R.id.burnProgressBar);

        earnView = (ImageView) view.findViewById(R.id.earnView);
        burnView = (ImageView) view.findViewById(R.id.burnView);

        earnGroup = (LinearLayout) view.findViewById(R.id.earnGroup);
        burnGroup = (LinearLayout) view.findViewById(R.id.burnGroup);

        topEarn1 = (ImageView) view.findViewById(R.id.topEarn1);
        topEarn2 = (ImageView) view.findViewById(R.id.topEarn2);
        topEarn3 = (ImageView) view.findViewById(R.id.topEarn3);
        otherEarn = (ImageView) view.findViewById(R.id.otherEarn);

        topBurn1 = (ImageView) view.findViewById(R.id.topBurn1);
        topBurn2 = (ImageView) view.findViewById(R.id.topBurn2);
        topBurn3 = (ImageView) view.findViewById(R.id.topBurn3);
        otherBurn = (ImageView) view.findViewById(R.id.otherBurn);

        burnList = new ArrayList<>();
        earnList = new ArrayList<>();

        earnProgress.setMax(MAX_EARN);
        burnProgress.setMax(MAX_BURN);
        earnProgress.setProgress(0);
        burnProgress.setProgress(0);

        motivationTextView = (TextView) view.findViewById(R.id.topPanelIntro);
        motivationTextView.setText(Util.getRandomMotivationalSpeech(getContext()));

        addListeners();

        getAllTransactions();
    }

    private void addListeners(){
        earnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayEarnItems(true);
            }
        });

        burnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Log.d("ZHAN", "top 3 earn is " + earnList.get(2).getName());
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

    /**
     * Displays prompt for user to add new transaction.
     */
    private void addEarnBurnTransaction(final EarnBurn data1){
        final EarnBurn data = myRealm.where(EarnBurn.class).equalTo("id", data1.getId()).findFirst();

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
        cv.setIconColor(R.color.white);

        if(data.getType().equalsIgnoreCase(BalanceType.BURN.toString())){
            cv.setCircleColor(R.color.blue);
        }else{
            cv.setCircleColor(R.color.red);
        }

        //Edit text
        final EditText input = (EditText) promptView.findViewById(R.id.genericEditText);
        input.setHint(data.getType());

        //NumberPicker
        final ExtendedNumberPicker unitNumberPicker = (ExtendedNumberPicker) promptView.findViewById(R.id.genericNumberPicker);

        List<String> ssList = new ArrayList<>();
        for(int i = 0; i < data.getCostList().size(); i++){
            ssList.add(data.getCostList().get(i).getUnitType());
        }

        final String[] values = ssList.toArray(new String[0]);

        unitNumberPicker.setMinValue(0);

        //If this earnBurn have been init with values
        if(values.length > 0){
            unitNumberPicker.setMaxValue(values.length - 1);
            unitNumberPicker.setDisplayedValues(values);
            unitNumberPicker.setWrapSelectorWheel(true);

            final AlertDialog ss = new AlertDialog.Builder(getActivity())
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
                            if (!input.getText().toString().isEmpty()) {
                                myRealm.beginTransaction();
                                Transaction transaction = myRealm.createObject(Transaction.class);
                                transaction.setId(Util.generateUUID());
                                transaction.setDate(new Date());
                                transaction.setEarnBurn(data);
                                transaction.setUnitCost(Integer.parseInt(input.getText().toString()));
                                transaction.setCostType(values[unitNumberPicker.getValue()]);
                                myRealm.commitTransaction();

                                displayEarnItems(false);
                                displayBurnItems(false);
                                getAllTransactions();
                            }
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
                    .create();

            ss.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    if(data.getType().equalsIgnoreCase(BalanceType.BURN.toString())){
                        //BLUE
                        ss.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        ss.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    }else{
                        //RED
                        ss.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                        ss.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    }
                }
            });

            ss.show();
        }else{
            Intent firstTimeEarnBurn = new Intent(getContext(), InfoActivity.class);
            firstTimeEarnBurn.putExtra(Constants.REQUEST_IS_EDIT_EARNBURN, true);
            firstTimeEarnBurn.putExtra(Constants.REQUEST_EDIT_EARNBURN, data.getId());
            startActivity(firstTimeEarnBurn);
        }
    }

    private void getAllTransactions(){
        final RealmResults<Transaction> transactionRealmResults = myRealm.where(Transaction.class).findAllAsync();
        transactionRealmResults.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                transactionRealmResults.removeChangeListener(this);

                Log.d(TAG, "thjere are "+transactionRealmResults.size()+" tr");

                float currentCount = 0;
                for(int i = 0; i < transactionRealmResults.size(); i++){
                    for(int k = 0; k < transactionRealmResults.get(i).getEarnBurn().getCostList().size(); k++){
                        if(transactionRealmResults.get(i).getEarnBurn().getCostList().get(k).getUnitType().equalsIgnoreCase(transactionRealmResults.get(i).getCostType())){
                            int pointsPer = transactionRealmResults.get(i).getEarnBurn().getCostList().get(k).getPointsEarnPer();
                            int unit = transactionRealmResults.get(i).getEarnBurn().getCostList().get(k).getUnitCost();
                            int costUserInput = transactionRealmResults.get(i).getUnitCost();
                            float thisCost = (((float)costUserInput / unit) * pointsPer);

                            if(transactionRealmResults.get(i).getEarnBurn().getType().equalsIgnoreCase(BalanceType.BURN.toString())){
                                currentCount -= thisCost;
                            }else{
                                currentCount += thisCost;
                            }

                            Log.d(TAG, transactionRealmResults.get(i).getEarnBurn().getType()+" Val is ("+pointsPer+" per "+unit+"). User put "+costUserInput+" => "+thisCost);
                        }
                    }
                }

                addSign(currentCount);
            }
        });
    }

    public void getTop3(final BalanceType type){
        final RealmResults<Transaction> transactionRealmResults = myRealm.where(Transaction.class).equalTo("earnBurn.type",type.toString()).findAllAsync();
        transactionRealmResults.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                transactionRealmResults.removeChangeListener(this);

                Log.d(TAG, "there are "+transactionRealmResults.size()+" transactions for type :"+type.toString());

                Map<String,Integer> transactionMap = new HashMap<>();

                for(int i = 0; i < transactionRealmResults.size(); i++){
                    if (transactionMap.containsKey(transactionRealmResults.get(i).getEarnBurn().getId())) {
                        int origValue = transactionMap.get(transactionRealmResults.get(i).getEarnBurn().getId());
                        transactionMap.put(transactionRealmResults.get(i).getEarnBurn().getId(), origValue+1);
                    } else {
                        transactionMap.put(transactionRealmResults.get(i).getEarnBurn().getId(), 1);
                    }
                }

                //Alternative
                List<String> sortedList = Util.sortByComparatorList(transactionMap);

                for (int i = 0; i < sortedList.size(); i++) {
                    Log.d(TAG, i+" : "+sortedList.get(i));
                }

                //need to handle if the string is empty or null

                top3EarnItems(type.toString(), sortedList.get(0), sortedList.get(1), sortedList.get(2));
            }
        });
    }

    private void top3EarnItems(String type, String first, String second, String third){
        EarnBurn earnBurn1 = myRealm.where(EarnBurn.class).equalTo("id", first).equalTo("type",type).findFirst();
        EarnBurn earnBurn2 = myRealm.where(EarnBurn.class).equalTo("id", second).equalTo("type",type).findFirst();
        EarnBurn earnBurn3 = myRealm.where(EarnBurn.class).equalTo("id", third).equalTo("type",type).findFirst();

        if(type.equalsIgnoreCase(BalanceType.EARN.toString())) {
            topEarn1.setImageResource(Util.getIconID(getContext(), earnBurn1.getIcon()));
            topEarn2.setImageResource(Util.getIconID(getContext(), earnBurn2.getIcon()));
            topEarn3.setImageResource(Util.getIconID(getContext(), earnBurn3.getIcon()));
        }else{
            topBurn1.setImageResource(Util.getIconID(getContext(), earnBurn1.getIcon()));
            topBurn2.setImageResource(Util.getIconID(getContext(), earnBurn2.getIcon()));
            topBurn3.setImageResource(Util.getIconID(getContext(), earnBurn3.getIcon()));
        }
    }

    private void addSign(float value){
        if(value > 0){
            headerText.setText("+"+Math.round(value));
        }else if(value <= 0){
            headerText.setText(""+Math.round(value));
        }
        Log.d("ZHAN", "actual  value is "+value);
        setProgressBar(value);
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

                    topEarn1.setImageResource(Util.getIconID(getContext(), earnList.get(0).getIcon()));
                    topEarn1.setImageResource(Util.getIconID(getContext(), earnList.get(1).getIcon()));
                    topEarn1.setImageResource(Util.getIconID(getContext(), earnList.get(2).getIcon()));
                    otherEarn.setImageResource(R.drawable.svg_other);

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

                    topBurn1.setImageResource(Util.getIconID(getContext(), burnList.get(0).getIcon()));
                    topBurn2.setImageResource(Util.getIconID(getContext(), burnList.get(1).getIcon()));
                    topBurn3.setImageResource(Util.getIconID(getContext(), burnList.get(2).getIcon()));
                    otherBurn.setImageResource(R.drawable.svg_other);

                    burnRealmResults.removeChangeListener(this);
                }
            });
        }else{ //hide horizontal list view
            burnGroup.setVisibility(View.GONE);
            burnView.setVisibility(View.VISIBLE);
        }
    }

    private void setProgressBar(float total){
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Lifecycle
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
        resumeRealm();
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
        resumeRealm();
        getAllTransactions();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
        closeRealm();
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop");
        closeRealm();
    }

    public void resumeRealm(){
        //if(myRealm == null || myRealm.isClosed()){
            myRealm = Realm.getDefaultInstance();
            Log.d(TAG, "resumeRealm");
        //}
    }

    public void closeRealm(){
        //if(myRealm != null && !myRealm.isClosed()){
            myRealm.close();
            Log.d(TAG, "closeRealm");
        //}
    }
}
