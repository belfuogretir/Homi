package com.example.belfu.homi;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

import com.example.belfu.homi.Activity.ExpenseActivity;
import com.example.belfu.homi.Fragment.PersonFragment;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;


public class MyApplication extends Application {
    private static MyApplication singleton;

    public static MyApplication getInstance() {
        return singleton;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new OneSignal.NotificationOpenedHandler() {
                    @Override
                    public void notificationOpened(OSNotificationOpenResult result) {
                        OSNotificationAction.ActionType actionType = result.action.type;
                        JSONObject data = result.notification.payload.additionalData;
                        String customKey = null;
                        Log.wtf("data",data+"");
                        if (data != null) {
                            try {
                                customKey= data.getString("id");
                                Log.wtf("customkey",customKey);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(getApplicationContext(),ExpenseActivity.class);
                            intent.putExtra("KisiKey",customKey);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        if (actionType == OSNotificationAction.ActionType.ActionTaken)
                            Log.wtf("OneSignalExample", "Button pressed with id: " + result.action.actionID);
                    }
                })
                .init();
        //user id & token
        OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
        OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getPushToken();
    }


}
