package cn.rongcloud.quickstart;

import android.app.Application;
import io.rong.imlib.RongIMClient;

public class MyApp extends Application {

  public static final String appkey = 请替换成您自己申请的AppKey;
  public static final String token1 = 请填写您生成的一个token;
  public static final String token2 = 请填写您生成的不同于 token1 的一个token;

  @Override
  public void onCreate() {
    super.onCreate();
    RongIMClient.init(this, appkey, false);
  }
}
