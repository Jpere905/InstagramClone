package com.example.instagramclone;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

// This class will be used to initialize a connection to our database
public class ParseApplication extends Application {

    // has a life cycle
    @Override
    public void onCreate() {
        super.onCreate();

        // Registering our parse models
        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("C0SDUonvyEblKCnY3u4OCcTqRtdaP369y8KPxeiw")
                .clientKey("5XUlTsA8ljmuKoj9WuySTVMk7izIGhYg2Z7slEi9")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
