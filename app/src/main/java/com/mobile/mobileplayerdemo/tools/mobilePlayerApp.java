package com.mobile.mobileplayerdemo.tools;

import android.app.Application;

import com.mobile.mobileplayerdemo.tools.CrashHandler;
import com.mobile.mobileplayerdemo.tools.GlobalToast;

public class mobilePlayerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);//全局收集异常类，并且log储存在本地
        GlobalToast.init(this);//全局Toast初始化
    }
}
