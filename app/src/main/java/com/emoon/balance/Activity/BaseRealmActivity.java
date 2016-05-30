package com.emoon.balance.Activity;

import android.os.Bundle;
import android.util.Log;

import io.realm.Realm;

/**
 * Created by Zhan on 16-05-18.
 */
public abstract class BaseRealmActivity extends BaseActivity {
    private static final String TAG = "BaseRealmActivity";

    protected Realm myRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        resumeRealm();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeRealm();
    }

    /**
     * Every Activity has to inflate a layout in the onCreate method. Added this method to
     * avoid duplicate all the inflate code in every activity. You only have to return the layout to
     * inflate in this method when extends BaseActivity.
     */
    protected abstract int getActivityLayout();

    /**
     * Every Activity should override this function as it should be where other initialization
     * occurs once only in the lifecycle.
     * Note: I would put init in the onStart function but it will call multiple times when the user
     * comes back into the activity which is unnecessary.
     */
    @Override
    protected void init(){
        //resumeRealm();
    }

    public void resumeRealm(){
        //if(myRealm == null || myRealm.isClosed()){
        myRealm = Realm.getDefaultInstance();
        Log.d(TAG, "resumeRealm");
        //}
    }

    /**
     * Close Realm if possible
     */
    public void closeRealm(){
        //if(myRealm != null && !myRealm.isClosed()){
        myRealm.close();
        Log.d(TAG, "closeRealm");
        //}
    }
}
