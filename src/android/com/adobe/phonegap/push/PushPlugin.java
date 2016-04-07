package com.adobe.phonegap.push;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.myorpheo.urc.R;
import com.parse.ParsePushBroadcastReceiver;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;

public class PushPlugin extends CordovaPlugin implements PushConstants {

    public static final String LOG_TAG = "PushPlugin";

    private static CallbackContext pushContext;
    private static CordovaWebView gWebView;
    private static Bundle gCachedExtras = null;
    private static boolean gForeground = false;

    ParseBroadCastReceiver receiver = null;
    AlertDialog notificationAlert = null;

    /**
     * Gets the application context from cordova's main activity.
     * @return the application context
     */
    private  Context getApplicationContext() {
        return this.cordova.getActivity().getApplicationContext();

    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {

        boolean result = false;

        Log.v(LOG_TAG, "execute: action=" + action);
        if(REGISTER.equals(action)){
            pushContext = callbackContext;

            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(COM_ADOBE_PHONEGAP_PUSH, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(SOUND, true);
            editor.putBoolean(VIBRATE, true);
            editor.putBoolean(CLEAR_NOTIFICATIONS, true);
            editor.commit();

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            intentFilter.addAction(ParsePushBroadcastReceiver.ACTION_PUSH_RECEIVE);
            intentFilter.addAction(ParsePushBroadcastReceiver.ACTION_PUSH_OPEN);
            intentFilter.addAction(ParsePushBroadcastReceiver.ACTION_PUSH_DELETE);

            if(receiver == null){

                receiver = new ParseBroadCastReceiver(){

                    @Override
                    protected void onPushReceive(Context context, Intent intent) {
                        
                            super.onPushReceive(context, intent);

                            try {
                                Iterator<String> it = intent.getExtras().keySet().iterator();
                                while (it.hasNext()) {
                                    String key = it.next();
                                    Object value = intent.getExtras().get(key);

                                    if (key.equals("com.parse.Data")) {
                                        JSONObject obj = new JSONObject((String) value);

                                        String message = obj.getString("alert");

                                        if (message != null && message.length() > 0) {

                                            int stringId = context.getApplicationInfo().labelRes;
                                            String appName =  context.getString(stringId);
                                            //Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                                            if(gForeground){

                                                if(notificationAlert != null && notificationAlert.isShowing()){
                                                    notificationAlert.dismiss();
                                                }

                                                notificationAlert = new AlertDialog.Builder(context)
                                                        .setTitle(appName)
                                                        .setMessage(message)
                                                        .setPositiveButton(android.R.string.yes, null)
                                                        .setIcon(android.R.drawable.ic_dialog_info)
                                                        .show();
                                            }
                                            else{

                                                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra(PUSH_BUNDLE, intent.getExtras());

                                                Notification mBuilder =
                                                        new Notification.Builder(context)
                                                                .setContentTitle(appName)
                                                                .setContentText(message)
                                                                .setSmallIcon(R.drawable.icon)
                                                                .setDefaults(Notification.DEFAULT_ALL)
                                                                .build();

                                                mNotificationManager.notify(appName, 0, mBuilder);

                                            }




                                        }
                                    }
                                }
                            } catch (JSONException e) {

                            }



                        }
                    //}


                };
                webView.getContext().registerReceiver(receiver, intentFilter);
            }


        }
        else if (UNREGISTER.equals(action)) {

            Log.v(LOG_TAG, "UNREGISTER");
            result = true;
            
            webView.getContext().unregisterReceiver(receiver);
            receiver = null;
            callbackContext.success();
        }
        else {

            result = false;
            Log.e(LOG_TAG, "Invalid action : " + action);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
        }

        return result;
    }



    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        gForeground = true;
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        gForeground = false;

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(COM_ADOBE_PHONEGAP_PUSH, Context.MODE_PRIVATE);
        if (prefs.getBoolean(CLEAR_NOTIFICATIONS, true)) {
            final NotificationManager notificationManager = (NotificationManager) cordova.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        gForeground = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gForeground = false;
        gWebView = null;
    }

    

    public static boolean isInForeground() {
      return gForeground;
    }

    public static boolean isActive() {
        return gWebView != null;
    }

    private int parseInt(String value, Bundle extras) {
        int retval = 0;

        try {
            retval = Integer.parseInt(getString(extras, value));
        }
        catch(NumberFormatException e) {
            Log.e(LOG_TAG, "Number format exception - Error parsing " + value + ": " + e.getMessage());
        }
        catch(Exception e) {
            Log.e(LOG_TAG, "Number format exception - Error parsing " + value + ": " + e.getMessage());
        }

        return retval;
    }

    private String getString(Bundle extras,String key) {
        String message = extras.getString(key);
        if (message == null) {
            message = extras.getString(GCM_NOTIFICATION+"."+key);
        }
        return message;
    }
}