package cn.rongcloud.sealcall;


import android.support.multidex.MultiDexApplication;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class SealCallApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        RongIMClient.setServerInfo(AppConfigure.IM_NAV_SERVER, AppConfigure.IM_FILE_SERVER);
        RongIM.init(getApplicationContext(), AppConfigure.APP_KEY, true);
    }
}
