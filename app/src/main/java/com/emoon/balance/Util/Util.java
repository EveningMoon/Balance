package com.emoon.balance.Util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.util.TypedValue;

import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.EarnBurn;
import com.emoon.balance.Model.UnitType;
import com.emoon.balance.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by zhanyap on 2016-01-28.
 */
public final class Util {

    private Util() {
    }//private constructor

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static List<EarnBurn> getListOfActivities(Context context){
        TypedArray activities = context.getResources().obtainTypedArray(R.array.list);

        List<EarnBurn> earnList = new ArrayList<>();
        for(int i = 0; i < activities.length(); i++){

            int rs = activities.getResourceId(i, 0);


            EarnBurn earn = new EarnBurn();
            earn.setId(generateUUID());
            earn.setType(BalanceType.EARN.toString());
            //earn.setCost(10f);
            earn.setName(context.getResources().getResourceName(rs));
            earn.setUnit(UnitType.MINUTE.toString());
            earn.setIcon(context.getResources().getResourceEntryName(rs));

            Log.d("ZHAN", "name:"+earn.getName()+" -> "+earn.getIcon());
            earnList.add(earn);
        }

        //Important
        activities.recycle();

        return earnList;
    }

    public static List<EarnBurn> getListOfRewards(Context context){
        TypedArray rewards = context.getResources().obtainTypedArray(R.array.rewards);

        List<EarnBurn> burnList = new ArrayList<>();
        for(int i = 0; i < rewards.length(); i++){
            // get resource ID by index
            int rs = rewards.getResourceId(i, 0);

            EarnBurn burn = new EarnBurn();
            burn.setId(generateUUID());
            burn.setType(BalanceType.BURN.toString());
            //burn.setCost(10f);
            burn.setName(context.getResources().getResourceName(rs));
            burn.setUnit(UnitType.QUANTITY.toString());
            burn.setIcon(context.getResources().getResourceEntryName(rs));
            burnList.add(burn);
        }
        rewards.recycle();
        return burnList;
    }

    public static String getRandomMotivationalSpeech(Context context){
        TypedArray speech = context.getResources().obtainTypedArray(R.array.motivationalQuotes);

        List<String> listOfSpeech = new ArrayList<>();
        for(int i = 0; i < speech.length(); i++){
            // get resource ID by index
            int rs = speech.getResourceId(i, 0);

            listOfSpeech.add(context.getResources().getString(rs));
        }
        speech.recycle();

        Random random = new Random();
        return listOfSpeech.get(random.nextInt(speech.length()));
    }

    public static int getIconID(Context context, String value){
        return context.getResources().getIdentifier(value, "drawable", context.getPackageName());
    }

    /**
     * Converting DP to PX
     *
     * @param context Context
     * @param dp      dp to be converted to px
     * @return px
     */
    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Demonstrate checking for String that is not null, not empty, and not white
     * space only using standard Java classes.
     *
     * @param string String to be checked for not null, not empty, and not white
     *               space only.
     * @return {@code true} if provided String is not null, is not empty, and
     * has at least one character that is not considered white space.
     */
    public static boolean isNotNullNotEmptyNotWhiteSpaceOnlyByJava(final String string) {
        return string != null && !string.isEmpty() && !string.trim().isEmpty();
    }
}