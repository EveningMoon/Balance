package com.emoon.balance.Util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.emoon.balance.Model.BalanceType;
import com.emoon.balance.Model.Cost;
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
        TypedArray name = context.getResources().obtainTypedArray(R.array.list_activity_name);
        TypedArray icon = context.getResources().obtainTypedArray(R.array.list_activity_icon);

        List<EarnBurn> activityList = new ArrayList<>();
        for(int i = 0; i < name.length(); i++){
            // get resource ID by index
            int s1 = name.getResourceId(i, 0);
            int s2 = icon.getResourceId(i, 0);

            EarnBurn activity = new EarnBurn();
            activity.setId(generateUUID());
            activity.setName(context.getResources().getString(s1));
            activity.setType(BalanceType.EARN.toString());
            activity.setIcon(context.getResources().getResourceEntryName(s2));

            activityList.add(activity);
        }
        name.recycle();
        return activityList;
    }

    public static List<EarnBurn> getListOfRewards(Context context){
        TypedArray name = context.getResources().obtainTypedArray(R.array.list_reward_name);
        TypedArray icon = context.getResources().obtainTypedArray(R.array.list_reward_icon);

        List<EarnBurn> activityList = new ArrayList<>();
        for(int i = 0; i < name.length(); i++){
            // get resource ID by index
            int s1 = name.getResourceId(i, 0);
            int s2 = icon.getResourceId(i, 0);

            EarnBurn activity = new EarnBurn();
            activity.setId(generateUUID());
            activity.setName(context.getResources().getString(s1));
            activity.setType(BalanceType.BURN.toString());
            activity.setIcon(context.getResources().getResourceEntryName(s2));

            activityList.add(activity);
        }
        name.recycle();
        return activityList;
    }

    public static String getRandomMotivationalSpeech(Context context){
        List<String> speech = getListOfMotivationalSpeech(context);
        Random random = new Random();
        return speech.get(random.nextInt(speech.size()));
    }

    public static List<String> getListOfMotivationalSpeech(Context context){
        TypedArray speech = context.getResources().obtainTypedArray(R.array.motivationalQuotes);

        List<String> listOfSpeech = new ArrayList<>();
        for(int i = 0; i < speech.length(); i++){
            // get resource ID by index
            int rs = speech.getResourceId(i, 0);

            listOfSpeech.add(context.getResources().getString(rs));
        }
        speech.recycle();
        return listOfSpeech;
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

    public static String[] getListOfUnits(){
        List<String> listString = new ArrayList<>();
        for(UnitType ut : UnitType.values()){
            listString.add(ut.toString());
        }

        return listString.toArray(new String[0]);
    }


    public static List<String> getListOfUnits1(){
        List<String> listString = new ArrayList<>();
        for(UnitType ut : UnitType.values()){
            listString.add(ut.toString());
        }
        return listString;
    }
}