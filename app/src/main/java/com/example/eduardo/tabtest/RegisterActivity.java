package com.example.eduardo.tabtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class RegisterActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";

    public static final String REGISTERED_PREF = "Registered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        boolean registered = settings.getBoolean(REGISTERED_PREF, false);

        Log.d("SHARED PREFERENCES","registered: " + registered );

        if( registered ){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }

}
