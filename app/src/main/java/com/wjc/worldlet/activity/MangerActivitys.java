package com.wjc.worldlet.activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/10/1.
 * 微信：wjc398556712
 * 作用：管理Activity，把Activity添加到集合中，便于退出时全部销毁
 */
public class MangerActivitys {
    public static List<Object> activitys = new ArrayList<>();

    // 添加新創建的activity
    public static void addActivity(Object object) {
        activitys.add(object);
    }
}
