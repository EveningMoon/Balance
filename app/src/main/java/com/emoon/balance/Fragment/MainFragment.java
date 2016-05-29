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
import android.view.WindowManager;
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
import com.emoon.balance.R;
import com.emoon.balance.Util.BalancePreference;
import com.emoon.balance.Util.Util;
import com.emoon.balance.View.ExtendedNumberPicker;
import com.zhan.library.CircularView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private View view;

    private TextView headerText, motivationTextView;
    private ViewGroup earnBtn, burnBtn;

    private ImageView earnView, burnView;

    private RoundCornerProgressBar earnProgress, burnProgress;

    private RealmResults<EarnBurn> earnRealmResults, burnRealmResults;

    private List<EarnBurn> earnList, burnList;

    private ViewGroup earnGroup;
    private ImageView topEarn1Icon, topEarn2Icon, topEarn3Icon, otherEarnIcon;

    private ViewGroup burnGroup;
    private ImageView topBurn1Icon, topBurn2Icon, topBurn3Icon, otherBurnIcon;

    private Realm myRealm;

    private EarnBurn burn1Default, burn2Default, burn3Default;
    private EarnBurn earn1Default, earn2Default, earn3Default;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        }
        init();
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

        topEarn1Icon = (ImageView) view.findViewById(R.id.topEarn1);
        topEarn2Icon = (ImageView) view.findViewById(R.id.topEarn2);
        topEarn3Icon = (ImageView) view.findViewById(R.id.topEarn3);
        otherEarnIcon = (ImageView) view.findViewById(R.id.otherEarn);

        topBurn1Icon = (ImageView) view.findViewById(R.id.topBurn1);
        topBurn2Icon = (ImageView) view.findViewById(R.id.topBurn2);
        topBurn3Icon = (ImageView) view.findViewById(R.id.topBurn3);
        otherBurnIcon = (ImageView) view.findViewById(R.id.otherBurn);

        burnList = new ArrayList<>();
        earnList = new ArrayList<>();

        earnProgress.setMax(BalancePreference.getMinMax(getContext()));
        burnProgress.setMax(BalancePreference.getMinMax(getContext()));
        earnProgress.setProgress(0);
        burnProgress.setProgress(0);

        motivationTextView = (TextView) view.findViewById(R.id.topPanelIntro);
        motivationTextView.setText(Util.getRandomMotivationalSpeech(getContext()));

        addListeners();
        calculateTotalActivityAndReward();

        getDefaultEarnBurn();
    }

    private void addListeners(){
        earnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEarnItemsVisibility(true);
            }
        });

        burnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBurnItemsVisibility(true);
            }
        });

        topBurn1Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top 1 burn is " + burnList.get(0).getName());
                addEarnBurnTransaction(burnList.get(0));
            }
        });

        topBurn2Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top  2 burn is "+burnList.get(1).getName());
                addEarnBurnTransaction(burnList.get(1));
            }
        });

        topBurn3Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top 3 burn is " + burnList.get(2).getName());
                addEarnBurnTransaction(burnList.get(2));
            }
        });

        otherBurnIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "other burn ");
                goToOther(BalanceType.BURN);
            }
        });

        topEarn1Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top 1 earn is " + earnList.get(0).getName());
                addEarnBurnTransaction(earnList.get(0));
            }
        });

        topEarn2Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top  2 earn is "+earnList.get(1).getName());
                addEarnBurnTransaction(earnList.get(1));
            }
        });

        topEarn3Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top 3 earn is " + earnList.get(2).getName());
                addEarnBurnTransaction(earnList.get(2));
            }
        });

        otherEarnIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "other earn ");
                goToOther(BalanceType.EARN);
            }
        });
    }

    /**
     * Get Activity and Rewards that have priority. (1, 2, or 3)
     */
    private void getDefaultEarnBurn(){
        final RealmResults<EarnBurn> res = myRealm.where(EarnBurn.class).findAllAsync();
        res.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                //find default Activity
                for(int i = 0; i < res.size(); i++){
                    if(res.get(i).getType().equalsIgnoreCase(BalanceType.EARN.toString())){
                        if(res.get(i).getPriority() == 1){
                            earn1Default = res.get(i);
                        }else if(res.get(i).getPriority() == 2){
                            earn2Default = res.get(i);
                        }else if(res.get(i).getPriority() == 3){
                            earn3Default = res.get(i);
                        }
                    }
                }

                //find default Rewards
                for(int i = 0; i < res.size(); i++){
                    if(res.get(i).getType().equalsIgnoreCase(BalanceType.BURN.toString())){
                        if(res.get(i).getPriority() == 1){
                            burn1Default = res.get(i);
                        }else if(res.get(i).getPriority() == 2){
                            burn2Default = res.get(i);
                        }else if(res.get(i).getPriority() == 3){
                            burn3Default = res.get(i);
                        }
                    }
                }

                Log.d("DEFAULT", "-----------------------");
                Log.d("DEFAULT", "default 1 burn : "+burn1Default);
                Log.d("DEFAULT", "default 2 burn : "+burn2Default);
                Log.d("DEFAULT", "default 3 burn : "+burn3Default);
                Log.d("DEFAULT", "default 1 earn : "+earn1Default);
                Log.d("DEFAULT", "default 2 earn : "+earn2Default);
                Log.d("DEFAULT", "default 3 earn : "+earn3Default);
                Log.d("DEFAULT", "-----------------------");

                res.removeChangeListener(this);
            }
        });
    }

    /**
     * View the list of Activities or Rewards in another activity.
     * @param type
     */
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
                            setEarnItemsVisibility(false);
                            setBurnItemsVisibility(false);
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

                                setEarnItemsVisibility(false);
                                setBurnItemsVisibility(false);
                                calculateTotalActivityAndReward();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            setEarnItemsVisibility(false);
                            setBurnItemsVisibility(false);
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

            ss.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            ss.show();

        }else{
            Intent firstTimeEarnBurn = new Intent(getContext(), InfoActivity.class);
            firstTimeEarnBurn.putExtra(Constants.REQUEST_IS_EDIT_EARNBURN, true);
            firstTimeEarnBurn.putExtra(Constants.REQUEST_EDIT_EARNBURN, data.getId());
            startActivity(firstTimeEarnBurn);
        }
    }

    /**
     * Gets all transactions and calculate the net value based on Activity and Rewards.
     * Displays its value to the header value.
     */
    private void calculateTotalActivityAndReward(){
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

                updateHeaderValue(currentCount);
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
                    //if (transactionMap.containsKey(transactionRealmResults.get(i).getEarnBurn().getName())) {
                        int origValue = transactionMap.get(transactionRealmResults.get(i).getEarnBurn().getId());
                        //int origValue = transactionMap.get(transactionRealmResults.get(i).getEarnBurn().getName());
                        transactionMap.put(transactionRealmResults.get(i).getEarnBurn().getId(), origValue+1);
                        //transactionMap.put(transactionRealmResults.get(i).getEarnBurn().getName(), origValue+1);
                    } else {
                        transactionMap.put(transactionRealmResults.get(i).getEarnBurn().getId(), 1);
                        //transactionMap.put(transactionRealmResults.get(i).getEarnBurn().getName(), 1);
                    }
                }

                List<String> sortedList = Util.sortByComparatorList(transactionMap);

                Log.d(TAG, "Top 3 are : ");
                for (int i = 0; i < sortedList.size(); i++) {
                    Log.d(TAG, i+" : "+sortedList.get(i));
                }

                if(type == BalanceType.BURN) {
                    if(sortedList.size() == 0){
                        updateTop3EarnBurn(type.toString(), burn1Default.getId(), burn2Default.getId(), burn3Default.getId());
                    }else if (sortedList.size() == 1) {
                        updateTop3EarnBurn(type.toString(), sortedList.get(0), burn1Default.getId(), burn2Default.getId());
                    } else if (sortedList.size() == 2) {
                        updateTop3EarnBurn(type.toString(), sortedList.get(0), sortedList.get(1), burn1Default.getId());
                    } else if (sortedList.size() > 2) {
                        updateTop3EarnBurn(type.toString(), sortedList.get(0), sortedList.get(1), sortedList.get(2));
                    }
                }else if(type == BalanceType.EARN){
                    if(sortedList.size() == 0){
                        updateTop3EarnBurn(type.toString(), earn1Default.getId(), earn2Default.getId(), earn3Default.getId());
                    } else if (sortedList.size() == 1) {
                        updateTop3EarnBurn(type.toString(), sortedList.get(0), earn1Default.getId(), earn2Default.getId());
                    } else if (sortedList.size() == 2) {
                        updateTop3EarnBurn(type.toString(), sortedList.get(0), sortedList.get(1), earn1Default.getId());
                    } else if (sortedList.size() > 2) {
                        updateTop3EarnBurn(type.toString(), sortedList.get(0), sortedList.get(1), sortedList.get(2));
                    }
                }
            }
        });
    }

    private void updateTop3EarnBurn(String type, String first, String second, String third){
        EarnBurn earnBurn1 = myRealm.where(EarnBurn.class).equalTo("id", first).equalTo("type",type).findFirst();
        EarnBurn earnBurn2 = myRealm.where(EarnBurn.class).equalTo("id", second).equalTo("type",type).findFirst();
        EarnBurn earnBurn3 = myRealm.where(EarnBurn.class).equalTo("id", third).equalTo("type",type).findFirst();

        if(type.equalsIgnoreCase(BalanceType.EARN.toString())) {
            topEarn1Icon.setImageResource(Util.getIconID(getContext(), earnBurn1.getIcon()));
            topEarn2Icon.setImageResource(Util.getIconID(getContext(), earnBurn2.getIcon()));
            topEarn3Icon.setImageResource(Util.getIconID(getContext(), earnBurn3.getIcon()));

            earnList.set(0, earnBurn1);
            earnList.set(1, earnBurn2);
            earnList.set(2, earnBurn3);
        }else{
            topBurn1Icon.setImageResource(Util.getIconID(getContext(), earnBurn1.getIcon()));
            topBurn2Icon.setImageResource(Util.getIconID(getContext(), earnBurn2.getIcon()));
            topBurn3Icon.setImageResource(Util.getIconID(getContext(), earnBurn3.getIcon()));

            burnList.set(0, earnBurn1);
            burnList.set(1, earnBurn2);
            burnList.set(2, earnBurn3);
        }
    }

    private void setEarnItemsVisibility(boolean value){
        if(value) { //display horizontal list view
            earnRealmResults = myRealm.where(EarnBurn.class).equalTo("type", BalanceType.EARN.toString()).findAllAsync();
            earnRealmResults.addChangeListener(new RealmChangeListener() {
                @Override
                public void onChange() {
                    earnList = myRealm.copyFromRealm(earnRealmResults);

                    earnView.setVisibility(View.GONE);
                    earnGroup.setVisibility(View.VISIBLE);

                    getTop3(BalanceType.EARN);
                    otherEarnIcon.setImageResource(R.drawable.svg_other);

                    earnRealmResults.removeChangeListener(this);
                }
            });
        }else{ //hide horizontal list view
            earnGroup.setVisibility(View.GONE);
            earnView.setVisibility(View.VISIBLE);
        }
    }

    private void setBurnItemsVisibility(boolean value){
        if(value){ //display horizontal list view
            burnRealmResults = myRealm.where(EarnBurn.class).equalTo("type", BalanceType.BURN.toString()).findAllAsync();
            burnRealmResults.addChangeListener(new RealmChangeListener() {
                @Override
                public void onChange() {
                    burnList = myRealm.copyFromRealm(burnRealmResults);

                    burnGroup.setVisibility(View.VISIBLE);
                    burnView.setVisibility(View.GONE);

                    getTop3(BalanceType.BURN);
                    otherBurnIcon.setImageResource(R.drawable.svg_other);

                    burnRealmResults.removeChangeListener(this);
                }
            });
        }else{ //hide horizontal list view
            burnGroup.setVisibility(View.GONE);
            burnView.setVisibility(View.VISIBLE);
        }
    }

    private void updateMinMaxProgressBar(){
        earnProgress.setMax(BalancePreference.getMinMax(getContext()));
        burnProgress.setMax(BalancePreference.getMinMax(getContext()));
    }

    private void updateHeaderValue(float value){
        if(value > 0){
            headerText.setText("+"+Math.round(value));
        }else if(value <= 0){
            headerText.setText(""+Math.round(value));
        }
        Log.d("ZHAN", "actual  value is "+value);
        updateProgressBar(value);
    }

    private void updateProgressBar(float total){
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
        calculateTotalActivityAndReward();
        updateMinMaxProgressBar();
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
