package com.rtcdemo;

import android.app.Application;

import io.rong.imlib.RongIMClient;

public class MyApp extends Application {
    //    public static final String appkey="sfci50a7s4q5i";
//    public static final String token1="+XhpDeWs1dj/CtJqNBSHha5VbzBm9J5AteeZN+MbeIuyHPNP/QFxndTpzYmVzZKSTFPVWauINNqV8GB7+3x38w==";
//    public static final String token2="pa2MrRa848XreUpBbIDNKt/apXzcNtCFru7N98jVVrZb6mtUtJnrh6cWPS6Lu8ursDTJE5M5nto=";
    public static final String appkey = "z3v5yqkbv8v30";
    public static final String token1 = "ifD+g3G5yOd5u4WwVipNNM2yq+hfEluLjZ78E1qo4hFt+F9Wn9MiOOeOEoF6P0ekIgMfPz/y9zpH3fcfKkESX4Sb7ELIGs/NRbgu5/klaX4PKqeYRU6qrHOU4chaKbGs";
    public static final String token2 = "Gl3sKfKnIh1I0TuSlWDtI7I6ZiT8q7s0UEaMPWY0lMzUj/bahJYu1CIvww6HceBAqb3q4jQqkT1gB9cqqRFvxSsInDe4SvqdjfjhvsGXe+rnYnDnnye0buRNKy+58WBU";

    @Override
    public void onCreate() {
        super.onCreate();
        RongIMClient.init(this, appkey, false);
    }
}
