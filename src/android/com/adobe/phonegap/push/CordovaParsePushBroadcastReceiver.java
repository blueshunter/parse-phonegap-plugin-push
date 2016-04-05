package com.adobe.phonegap.push;

import android.content.Context;
import android.content.Intent;
import com.parse.ParsePushBroadcastReceiver;
import android.widget.Toast;

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

    	if(PushPlugin.isRegistered){
    		 super.onPushReceive(context, intent);
    		 Toast.makeText(context, "this is not shown"     , Toast.LENGTH_LONG);
    	}
       

        
    }
	
}