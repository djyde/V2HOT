package com.randy.client.v2hot.app;

import android.app.Application;

/**
 * Created by wml on 14/11/24.
 * Application class used to ensure that MyVolley is initialized. {@see MyVolley}
 */
public class V2hotApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        init();

    }

    private void init() {
        MyVolley.init(this);
    }
}
