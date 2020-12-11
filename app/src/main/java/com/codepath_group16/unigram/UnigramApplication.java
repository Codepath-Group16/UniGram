package com.codepath_group16.unigram;

import android.app.Application;

import com.codepath_group16.unigram.data.models.Post;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

public class UnigramApplication extends Application {
    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("ZosqWM97rtI4N4wI9i7xZvidrTpIe2GkN94Moren")
                .clientKey("IX3ooTetZxsoueioaCxgR4cJ28PecCLEwrnDJ0Kk")
                .server("https://parseapi.back4app.com")
                .build()
        );

        // Default ACL Public Read, User Write
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
