package com.adobe.phonegap.push;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

/*
 * Implementation of GCMBroadcastReceiver that hard-wires the intent service to be 
 * com.plugin.gcm.GCMIntentService, instead of your_package.GCMIntentService 
 */
public class CordovaParsePushBroadcastReceiver extends ParsePushBroadcastReceiver implements PushConstants {
	
	public CordovaParsePushBroadcastReceiver() {
        super();
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        
    }
	
}