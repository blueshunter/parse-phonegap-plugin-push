package com.adobe.phonegap.push;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

    ParsePushBroadcastReceiver receiver = null;

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
        if(INITIALIZE.equals(action)){
            pushContext = callbackContext;

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            intentFilter.addAction(ParsePushBroadcastReceiver.ACTION_PUSH_RECEIVE);
            intentFilter.addAction(ParsePushBroadcastReceiver.ACTION_PUSH_OPEN);
            intentFilter.addAction(ParsePushBroadcastReceiver.ACTION_PUSH_DELETE);

            if(receiver == null){

                receiver = new ParsePushBroadcastReceiver(){

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

                                            //Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                                            new AlertDialog.Builder(context)
                                                    .setTitle("URCA")
                                                    .setMessage(message)
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // continue with delete
                                                        }
                                                    })
                                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // do nothing
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_info)
                                                    .show();


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


    public static void sendEvent(JSONObject _json) {

        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, _json);
        pluginResult.setKeepCallback(true);
        pushContext.sendPluginResult(pluginResult);


    }

    public static void sendError(String message) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, message);
        pluginResult.setKeepCallback(true);
        pushContext.sendPluginResult(pluginResult);
    }

    /*
     * Sends the pushbundle extras to the client application.
     * If the client application isn't currently active, it is cached for later processing.
     */
    public static void sendExtras(Bundle extras) {
        if (extras != null) {


            if (gWebView != null) {
                sendEvent(convertBundleToJson(extras));
            } else {
                Log.v(LOG_TAG, "sendExtras: caching extras to send at a later time.");
                gCachedExtras = extras;
            }

            //sendEvent(convertBundleToJson(extras));


        }
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

    /*
     * serializes a bundle to JSON.
     */
    private static JSONObject convertBundleToJson(Bundle extras) {
        try {
            JSONObject json = new JSONObject();
            JSONObject additionalData = new JSONObject();
            Iterator<String> it = extras.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Object value = extras.get(key);
                 
                Log.d(LOG_TAG, "key = " + key);
                if (key.startsWith(GCM_NOTIFICATION)) {
                    key = key.substring(GCM_NOTIFICATION.length()+1, key.length());
                }

                // System data from Android
                if (key.equals(FROM) || key.equals(COLLAPSE_KEY)) {
                    additionalData.put(key, value);
                }
                else if (key.equals(FOREGROUND)) {
                    additionalData.put(key, extras.getBoolean(FOREGROUND));
                }
                else if (key.equals(COLDSTART)){
                    additionalData.put(key, extras.getBoolean(COLDSTART));
                } else if (key.equals(MESSAGE) || key.equals(BODY)) {
                    json.put(MESSAGE, value);
                } else if (key.equals(TITLE)) {
                    json.put(TITLE, value);
                } else if (key.equals(MSGCNT) || key.equals(BADGE)) {
                    json.put(COUNT, value);
                } else if (key.equals(SOUNDNAME) || key.equals(SOUND)) {
                    json.put(SOUND, value);
                } else if (key.equals(IMAGE)) {
                    json.put(IMAGE, value);
                } else if (key.equals(CALLBACK)) {
                    json.put(CALLBACK, value);
                }
                else if ( value instanceof String ) {
                    String strValue = (String)value;
                    try {
                        // Try to figure out if the value is another JSON object
                        if (strValue.startsWith("{")) {
                            additionalData.put(key, new JSONObject(strValue));
                        }
                        // Try to figure out if the value is another JSON array
                        else if (strValue.startsWith("[")) {
                            additionalData.put(key, new JSONArray(strValue));
                        }
                        else {
                            additionalData.put(key, value);
                        }                       
                    } catch (Exception e) {
                        additionalData.put(key, value);
                    }
                }
            } // while
            
            json.put(ADDITIONAL_DATA, additionalData);
            Log.v(LOG_TAG, "extrasToJSON: " + json.toString());

            return json;
        }
        catch( JSONException e) {
            Log.e(LOG_TAG, "extrasToJSON: JSON exception");
        }
        return null;
    }

    public static boolean isInForeground() {
      return gForeground;
    }

    public static boolean isActive() {
        return gWebView != null;
    }
}