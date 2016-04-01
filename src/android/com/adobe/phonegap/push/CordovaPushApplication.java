package com.adobe.phonegap.push;

/**
 * Created by MyOrpheoEdwin on 01/04/16.
 */
import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class CordovaPushApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();


        // Parse init
        Parse.initialize(this, "ycd0nCVUk0DHRPjZnyPebrxKCVJqbx3KtDGnXdeJ", "hw9Py0sva2eXwcS4u1GryZxwvDdzPETkheTgyseM");
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }
}