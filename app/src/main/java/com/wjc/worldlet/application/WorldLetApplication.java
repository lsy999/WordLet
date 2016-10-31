package com.wjc.worldlet.application;

import android.app.Application;

import org.xutils.x;

/**
 * Created by ${万嘉诚} on 2016/10/5.
 * 微信：wjc398556712
 * 作用：
 */
public class WorldLetApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
