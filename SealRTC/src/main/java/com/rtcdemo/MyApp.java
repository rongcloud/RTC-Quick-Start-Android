package com.rtcdemo;

import android.app.Application;

import io.rong.imlib.RongIMClient;

public class MyApp extends Application {
    public static final String appkey = 修改为自己的AppKey;
    public static final String token1 = 修改为可用的token;
    public static final String token2 = 修改为不同于token1的可用token;

    @Override
    public void onCreate() {
        super.onCreate();
        RongIMClient.init(this, appkey, false);
    }
}
