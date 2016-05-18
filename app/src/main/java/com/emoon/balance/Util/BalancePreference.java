package com.emoon.balance.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.emoon.balance.Etc.Constants;

import java.util.Calendar;

/**
 * Created by Zhan on 16-03-25.
 */
public final class BalancePreference {

    private BalancePreference(){}


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // First time functions
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void resetFirstTime(Context context){
        setPreferenceBoolean(context, Constants.FIRST_TIME, true);
    }

    public static boolean getFirstTime(Context context){
        return getPreferenceBoolean(context, Constants.FIRST_TIME, true);
    }

    public static void setFirstTime(Context context){
        setPreferenceBoolean(context, Constants.FIRST_TIME, false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Helper functions
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void setPreferenceBoolean(Context context, String key, boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getPreferenceBoolean(Context context, String key, boolean defVal){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defVal);
    }

    public static void setPreferenceInt(Context context, String key, int value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getPreferenceInt(Context context, String key, int defVal){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, defVal);
    }

    public static void setPreferenceString(Context context, String key, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getPreferenceString(Context context, String key, String defVal){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, defVal);
    }
}
