package com.adobe.phonegap.push;

/**
 * Created by MyOrpheoEdwin on 01/04/16.
 */
import android.app.Application;
import android.util.Log;
import com.parse.Parse;
import com.parse.ParseInstallation;

public class CordovaPushApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        // Parse init
        String appId = getStringByKey(this, "parse_app_id");
        String clientKey = getStringByKey(this, "parse_client_key");

        Log.d("Parse Installation", "Initializing with parse_app_id: " + appId + " and parse_client_key:" + clientKey);

        Parse.initialize(this, appId, clientKey);
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }


    private static String getStringByKey(Application app, String key) {
        int resourceId = app.getResources().getIdentifier(key, "string", app.getPackageName());
        return app.getString(resourceId);
    }

}