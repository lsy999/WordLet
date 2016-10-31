package com.wjc.worldlet.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by ${万嘉诚} on 2016/10/1.
 * 微信：wjc398556712
 * 作用：获得全局信息:边界的view位置
 */
public class SoftApplication extends Application {
    public static Context applicationContext ;
    private static SoftApplication instance = new SoftApplication();
    //设置边界的view位置，如果是边界的view则支持左滑切换到底部的菜单
    public int borderViewPosition;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
    }

    //单一实例
    public static SoftApplication getInstance() {
        return instance;
    }

    public int getBorderViewPosition() {
        return borderViewPosition;
    }

    public void setBorderViewPosition(int borderViewPosition) {
        this.borderViewPosition = borderViewPosition;
    }
}
