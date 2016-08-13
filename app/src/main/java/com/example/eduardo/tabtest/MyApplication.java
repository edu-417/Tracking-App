package com.example.eduardo.tabtest;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by EDUARDO on 28/07/2016.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
