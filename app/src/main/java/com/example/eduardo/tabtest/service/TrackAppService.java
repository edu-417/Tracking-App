package com.example.eduardo.tabtest.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by EDUARDO on 10/07/2016.
 */
public class TrackAppService extends IntentService {

    public static final String EXTRA_1 = "EXTRA_1";
    public static final String EXTRA_2 = "EXTRA_2";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TrackAppService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
