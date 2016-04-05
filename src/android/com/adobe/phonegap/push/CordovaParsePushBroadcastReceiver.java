package com.adobe.phonegap.push;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.parse.ParsePushBroadcastReceiver;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;

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

            try{
                Iterator<String> it = intent.getExtras().keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    Object value = intent.getExtras().get(key);

                    if(key.equals("com.parse.Data")){
                        JSONObject obj = new JSONObject((String)value);

                        String message = obj.getString("alert");
                        if(message != null && message.length()>0){
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
            catch(JSONException e){

            }


        }
       

        
    }
	
}