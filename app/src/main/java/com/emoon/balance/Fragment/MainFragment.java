package com.emoon.balance.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.emoon.balance.Activity.InfoActivity;
import com.emoon.balance.Activity.ListActivity;
import com.emoon.balance.Etc.Constants;
import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.EarnBurn;
import com.emoon.balance.Model.IconType;
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
import java.util.Vector;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainFragment extends BaseRealmFragment {
    private static final String TAG = "MainFragment";

    private TextView headerText, motivationTextView;
    private ViewGroup earnBtn, burnBtn;
    private ImageView earnView, burnView;
    private RoundCornerProgressBar earnProgress, burnProgress;

    private RealmResults<EarnBurn> earnRealmResults, burnRealmResults;
    private List<EarnBurn> earnList, burnList;

    private ViewGroup earnGroup, topEarn1Panel, topEarn2Panel, topEarn3Panel, otherEarnPanel;
    private ImageView topEarn1Icon, topEarn2Icon, topEarn3Icon, otherEarnIcon;
    private TextView topEarn1Text, topEarn2Text, topEarn3Text;

    private ViewGroup burnGroup, topBurn1Panel, topBurn2Panel, topBurn3Panel, otherBurnPanel;
    private ImageView topBurn1Icon, topBurn2Icon, topBurn3Icon, otherBurnIcon;
    private TextView topBurn1Text, topBurn2Text, topBurn3Text;

    private EarnBurn burn1Default, burn2Default, burn3Default;
    private EarnBurn earn1Default, earn2Default, earn3Default;

    //Keep track of alertdialogs
    private Vector<AlertDialog> dialogs = new Vector<>();

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_main;
    }

    @Override
    protected void init() {
        super.init();

        isFirstTime();

        headerText = (TextView) view.findViewById(R.id.topPanelHeader);
        earnBtn = (ViewGroup) view.findViewById(R.id.earnPanel);
        burnBtn = (ViewGroup) view.findViewById(R.id.burnPanel);

        earnProgress = (RoundCornerProgressBar) view.findViewById(R.id.earnProgressBar);
        burnProgress = (RoundCornerProgressBar) view.findViewById(R.id.burnProgressBar);

        earnView = (ImageView) view.findViewById(R.id.earnView);
        burnView = (ImageView) view.findViewById(R.id.burnView);

        earnGroup = (LinearLayout) view.findViewById(R.id.earnGroup);
        burnGroup = (LinearLayout) view.findViewById(R.id.burnGroup);

        topBurn1Panel = (ViewGroup) view.findViewById(R.id.topBurn1Panel);
        topBurn2Panel = (ViewGroup) view.findViewById(R.id.topBurn2Panel);
        topBurn3Panel = (ViewGroup) view.findViewById(R.id.topBurn3Panel);
        otherBurnPanel = (ViewGroup) view.findViewById(R.id.otherBurnPanel);
        topEarn1Panel = (ViewGroup) view.findViewById(R.id.topEarn1Panel);
        topEarn2Panel = (ViewGroup) view.findViewById(R.id.topEarn2Panel);
        topEarn3Panel = (ViewGroup) view.findViewById(R.id.topEarn3Panel);
        otherEarnPanel = (ViewGroup) view.findViewById(R.id.otherEarnPanel);

        topEarn1Icon = (ImageView) view.findViewById(R.id.topEarn1);
        topEarn2Icon = (ImageView) view.findViewById(R.id.topEarn2);
        topEarn3Icon = (ImageView) view.findViewById(R.id.topEarn3);
        otherEarnIcon = (ImageView) view.findViewById(R.id.otherEarn);
        topBurn1Icon = (ImageView) view.findViewById(R.id.topBurn1);
        topBurn2Icon = (ImageView) view.findViewById(R.id.topBurn2);
        topBurn3Icon = (ImageView) view.findViewById(R.id.topBurn3);
        otherBurnIcon = (ImageView) view.findViewById(R.id.otherBurn);

        topEarn1Text = (TextView) view.findViewById(R.id.topEarn1Text);
        topEarn2Text = (TextView) view.findViewById(R.id.topEarn2Text);
        topEarn3Text = (TextView) view.findViewById(R.id.topEarn3Text);
        topBurn1Text = (TextView) view.findViewById(R.id.topBurn1Text);
        topBurn2Text = (TextView) view.findViewById(R.id.topBurn2Text);
        topBurn3Text = (TextView) view.findViewById(R.id.topBurn3Text);

        burnList = new ArrayList<>();
        earnList = new ArrayList<>();

        earnProgress.setMax(BalancePreference.getMinMax(getContext()));
        burnProgress.setMax(BalancePreference.getMinMax(getContext()));
        earnProgress.setProgress(0);
        burnProgress.setProgress(0);

        motivationTextView = (TextView) view.findViewById(R.id.topPanelIntro);
        motivationTextView.setText(Util.getRandomMotivationalSpeech(getContext()));
        Log.d(TAG, "INITS");
        addListeners();
        getDefaultEarnBurn();
        calculateTotalActivityAndReward();
    }

    private void isFirstTime(){
        if(BalancePreference.getFirstTime(getContext())){
            BalancePreference.setFirstTime(getContext());
            createDefaultItems();
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

        topBurn1Panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top 1 burn is " + burnList.get(0).getName());
                addEarnBurnTransaction(burnList.get(0));
            }
        });

        topBurn2Panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top  2 burn is "+burnList.get(1).getName());
                addEarnBurnTransaction(burnList.get(1));
            }
        });

        topBurn3Panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top 3 burn is " + burnList.get(2).getName());
                addEarnBurnTransaction(burnList.get(2));
            }
        });

        otherBurnPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "other burn ");
                goToOther(BalanceType.BURN);
            }
        });

        topEarn1Panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top 1 earn is " + earnList.get(0).getName());
                addEarnBurnTransaction(earnList.get(0));
            }
        });

        topEarn2Panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top  2 earn is "+earnList.get(1).getName());
                addEarnBurnTransaction(earnList.get(1));
            }
        });

        topEarn3Panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ZHAN", "top 3 earn is " + earnList.get(2).getName());
                addEarnBurnTransaction(earnList.get(2));
            }
        });

        otherEarnPanel.setOnClickListener(new View.OnClickListener() {
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
        res.addChangeListener(new RealmChangeListener<RealmResults<EarnBurn>>() {
            @Override
            public void onChange(RealmResults<EarnBurn> element) {
                //find default Activity
                for(int i = 0; i < element.size(); i++){
                    if(element.get(i).getType().equalsIgnoreCase(BalanceType.EARN.toString())){
                        if(element.get(i).getPriority() == 1){
                            earn1Default = element.get(i);
                        }else if(element.get(i).getPriority() == 2){
                            earn2Default = element.get(i);
                        }else if(element.get(i).getPriority() == 3){
                            earn3Default = element.get(i);
                        }
                    }
                }

                //find default Rewards
                for(int i = 0; i < element.size(); i++){
                    if(element.get(i).getType().equalsIgnoreCase(BalanceType.BURN.toString())){
                        if(element.get(i).getPriority() == 1){
                            burn1Default = element.get(i);
                        }else if(element.get(i).getPriority() == 2){
                            burn2Default = element.get(i);
                        }else if(element.get(i).getPriority() == 3){
                            burn3Default = element.get(i);
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

                element.removeChangeListener(this);
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
        if(data.getIconType().equalsIgnoreCase(IconType.ICON.toString())){
            cv.setIconResource(Util.getIconID(getContext(), data.getIcon()));
            cv.setIconColor(R.color.white);
        }else if(data.getIconType().equalsIgnoreCase(IconType.NUMBER.toString())){
            cv.setText(""+Util.getFirstCharacterFromString(data.getName().toUpperCase()));
            cv.setTextColor(R.color.white);
        }

        //Edit Button
        ImageView editBtn = (ImageView) promptView.findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InfoActivity.class);
                intent.putExtra(Constants.REQUEST_IS_EDIT_EARNBURN, true);
                intent.putExtra(Constants.REQUEST_EDIT_EARNBURN, data1.getId());
                startActivity(intent);
            }
        });

        if(data.getType().equalsIgnoreCase(BalanceType.BURN.toString())){
            cv.setCircleColor(R.color.blue);
            editBtn.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
        }else{
            cv.setCircleColor(R.color.red);
            editBtn.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
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

            dialogs.add(ss);
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
        Log.d(TAG, "=======> calculateTotalActivityAndReward");

        final RealmResults<Transaction> transactionRealmResults = myRealm.where(Transaction.class).findAllAsync();
        transactionRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Transaction>>() {
            @Override
            public void onChange(RealmResults<Transaction> element) {
                element.removeChangeListener(this);

                Log.d(TAG, "thjere are "+element.size()+" tr");

                float currentCount = 0;
                for(int i = 0; i < element.size(); i++){
                    for(int k = 0; k < element.get(i).getEarnBurn().getCostList().size(); k++){
                        if(element.get(i).getEarnBurn().getCostList().get(k).getUnitType().equalsIgnoreCase(element.get(i).getCostType())){
                            int pointsPer = element.get(i).getEarnBurn().getCostList().get(k).getPointsEarnPer();
                            int unit = element.get(i).getEarnBurn().getCostList().get(k).getUnitCost();
                            int costUserInput = element.get(i).getUnitCost();
                            float thisCost = (((float)costUserInput / unit) * pointsPer);

                            if(element.get(i).getEarnBurn().getType().equalsIgnoreCase(BalanceType.BURN.toString())){
                                currentCount -= thisCost;
                            }else{
                                currentCount += thisCost;
                            }

                            Log.d(TAG, element.get(i).getEarnBurn().getType()+" Val is ("+pointsPer+" per "+unit+"). User put "+costUserInput+" => "+thisCost);
                        }
                    }
                }

                updateHeaderValue(currentCount);
            }
        });
    }

    public void getTop3(final BalanceType type){
        final RealmResults<Transaction> transactionRealmResults = myRealm.where(Transaction.class).equalTo("earnBurn.type",type.toString()).findAllAsync();
        transactionRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Transaction>>() {
            @Override
            public void onChange(RealmResults<Transaction> element) {
                element.removeChangeListener(this);

                Log.d(TAG, "there are "+element.size()+" transactions for type :"+type.toString());

                Map<String,Integer> transactionMap = new HashMap<>();

                for(int i = 0; i < element.size(); i++){
                    if (transactionMap.containsKey(element.get(i).getEarnBurn().getId())) {
                        //if (transactionMap.containsKey(transactionRealmResults.get(i).getEarnBurn().getName())) {
                        int origValue = transactionMap.get(element.get(i).getEarnBurn().getId());
                        //int origValue = transactionMap.get(transactionRealmResults.get(i).getEarnBurn().getName());
                        transactionMap.put(element.get(i).getEarnBurn().getId(), origValue+1);
                        //transactionMap.put(transactionRealmResults.get(i).getEarnBurn().getName(), origValue+1);
                    } else {
                        transactionMap.put(element.get(i).getEarnBurn().getId(), 1);
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
            if(earnBurn1.getIconType().equalsIgnoreCase(IconType.ICON.toString())){
                topEarn1Icon.setImageResource(Util.getIconID(getContext(), earnBurn1.getIcon()));
                topEarn1Icon.setVisibility(View.VISIBLE);
                topEarn1Text.setVisibility(View.INVISIBLE);
            }else if(earnBurn1.getIconType().equalsIgnoreCase(IconType.NUMBER.toString())){
                topEarn1Text.setText(""+Util.getFirstCharacterFromString(earnBurn1.getName().toUpperCase()));
                topEarn1Icon.setVisibility(View.INVISIBLE);
                topEarn1Text.setVisibility(View.VISIBLE);
            }

            if(earnBurn2.getIconType().equalsIgnoreCase(IconType.ICON.toString())){
                topEarn2Icon.setImageResource(Util.getIconID(getContext(), earnBurn2.getIcon()));
                topEarn2Icon.setVisibility(View.VISIBLE);
                topEarn2Text.setVisibility(View.INVISIBLE);
            }else if(earnBurn2.getIconType().equalsIgnoreCase(IconType.NUMBER.toString())){
                topEarn2Text.setText(""+Util.getFirstCharacterFromString(earnBurn1.getName().toUpperCase()));
                topEarn2Icon.setVisibility(View.INVISIBLE);
                topEarn2Text.setVisibility(View.VISIBLE);
            }

            if(earnBurn3.getIconType().equalsIgnoreCase(IconType.ICON.toString())){
                topEarn3Icon.setImageResource(Util.getIconID(getContext(), earnBurn3.getIcon()));
                topEarn3Icon.setVisibility(View.VISIBLE);
                topEarn3Text.setVisibility(View.INVISIBLE);
            }else if(earnBurn3.getIconType().equalsIgnoreCase(IconType.NUMBER.toString())){
                topEarn3Text.setText(""+Util.getFirstCharacterFromString(earnBurn1.getName().toUpperCase()));
                topEarn3Icon.setVisibility(View.INVISIBLE);
                topEarn3Text.setVisibility(View.VISIBLE);
            }

            earnList.set(0, earnBurn1);
            earnList.set(1, earnBurn2);
            earnList.set(2, earnBurn3);
        }else{
            if(earnBurn1.getIconType().equalsIgnoreCase(IconType.ICON.toString())){
                topBurn1Icon.setImageResource(Util.getIconID(getContext(), earnBurn1.getIcon()));
                topBurn1Icon.setVisibility(View.VISIBLE);
                topBurn1Text.setVisibility(View.INVISIBLE);
            }else if(earnBurn1.getIconType().equalsIgnoreCase(IconType.NUMBER.toString())){
                topBurn1Text.setText(""+Util.getFirstCharacterFromString(earnBurn1.getName().toUpperCase()));
                topBurn1Icon.setVisibility(View.INVISIBLE);
                topBurn1Text.setVisibility(View.VISIBLE);
            }

            if(earnBurn2.getIconType().equalsIgnoreCase(IconType.ICON.toString())){
                topBurn2Icon.setImageResource(Util.getIconID(getContext(), earnBurn2.getIcon()));
                topBurn2Icon.setVisibility(View.VISIBLE);
                topBurn2Text.setVisibility(View.INVISIBLE);
            }else if(earnBurn2.getIconType().equalsIgnoreCase(IconType.NUMBER.toString())){
                topBurn2Text.setText(""+Util.getFirstCharacterFromString(earnBurn1.getName().toUpperCase()));
                topBurn2Icon.setVisibility(View.INVISIBLE);
                topBurn2Text.setVisibility(View.VISIBLE);
            }

            if(earnBurn3.getIconType().equalsIgnoreCase(IconType.ICON.toString())){
                topBurn3Icon.setImageResource(Util.getIconID(getContext(), earnBurn3.getIcon()));
                topBurn3Icon.setVisibility(View.VISIBLE);
                topBurn3Text.setVisibility(View.INVISIBLE);
            }else if(earnBurn3.getIconType().equalsIgnoreCase(IconType.NUMBER.toString())){
                topBurn3Text.setText(""+Util.getFirstCharacterFromString(earnBurn1.getName().toUpperCase()));
                topBurn3Icon.setVisibility(View.INVISIBLE);
                topBurn3Text.setVisibility(View.VISIBLE);
            }

            burnList.set(0, earnBurn1);
            burnList.set(1, earnBurn2);
            burnList.set(2, earnBurn3);
        }
    }

    private void setEarnItemsVisibility(boolean value){
        if(value) { //display horizontal list view
            earnRealmResults = myRealm.where(EarnBurn.class).equalTo("type", BalanceType.EARN.toString()).findAllAsync();
            earnRealmResults.addChangeListener(new RealmChangeListener<RealmResults<EarnBurn>>() {
                @Override
                public void onChange(RealmResults<EarnBurn> element) {
                    earnList = myRealm.copyFromRealm(element);

                    earnView.setVisibility(View.GONE);
                    earnGroup.setVisibility(View.VISIBLE);

                    getTop3(BalanceType.EARN);
                    otherEarnIcon.setImageResource(R.drawable.svg_other);

                    element.removeChangeListener(this);
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
            burnRealmResults.addChangeListener(new RealmChangeListener<RealmResults<EarnBurn>>() {
                @Override
                public void onChange(RealmResults<EarnBurn> element) {
                    burnList = myRealm.copyFromRealm(element);

                    burnGroup.setVisibility(View.VISIBLE);
                    burnView.setVisibility(View.GONE);

                    getTop3(BalanceType.BURN);
                    otherBurnIcon.setImageResource(R.drawable.svg_other);

                    element.removeChangeListener(this);
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

    private void closeAllAlertDialog(){
        for (AlertDialog dialog : dialogs) {
            if (dialog.isShowing()) dialog.dismiss();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Lifecycle
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////



    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
        calculateTotalActivityAndReward();
        updateMinMaxProgressBar();
        setBurnItemsVisibility(false);
        setEarnItemsVisibility(false);
        closeAllAlertDialog();
    }


}
