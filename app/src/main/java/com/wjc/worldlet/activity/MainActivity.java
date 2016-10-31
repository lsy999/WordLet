package com.wjc.worldlet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wjc.worldlet.R;
import com.wjc.worldlet.entity.TabMode;
import com.wjc.worldlet.fragment.FriendFragment;
import com.wjc.worldlet.fragment.HomeFragment;
import com.wjc.worldlet.fragment.MessageFragment;
import com.wjc.worldlet.fragment.SearchFriendFragment;
import com.wjc.worldlet.tabhost.TabFragment;
import com.wjc.worldlet.view.DragLayout;
import com.wjc.worldlet.view.MyRelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${万嘉诚} on 2016/10/1.
 * 主页面
 */
public class MainActivity extends BaseActivity {

    /**
     * 控制底部标题栏图标颜色改变
     */
    public static final int HOME_TAB = 1000;
    public static final int SEARCH_TAB = 2000;
    public static final int MESSAGE_TAB = 3000;
    public static final int FRIEND_TAB = 4000;

    private String TAG = "MainActivity";
    private Context context;

    /**
     * 左边侧滑菜单
     */
    private DragLayout mDragLayout;
    private LinearLayout menu_header;
    private TextView menu_setting, tv_name;

    private ListView menuListView;// 菜单列表

    private TabFragment actionBarFragment;
    private MyRelativeLayout myRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        // 左侧页面listView添加数据
        List<Map<String, Object>> data = getMenuData();

        menuListView.setAdapter(new SimpleAdapter(this, data,
                R.layout.leftitem,
                new String[] { "item", "image", "iv" }, new int[] {
                R.id.tv_item, R.id.iv_item, R.id.iv }));
    }


    private void initView() {
        /**
         * 控件初始化
         */
        context = this;
        MangerActivitys.addActivity(context);

        // 点击back按钮
        actionBarFragment = (TabFragment) getSupportFragmentManager()
                .findFragmentById(R.id.tab_bar_fragment);

        int code = 1;
        final ArrayList<TabMode> listTabModes = new ArrayList<>();
        {// 底部导航栏首页
            final TabMode tabMode = new TabMode(HOME_TAB,
                    R.drawable.tab_1_selector, "主页",
                    R.drawable.tab_text_color_selector, new HomeFragment(),
                    code == 1);//code == 1表示是默认的界面
            listTabModes.add(tabMode);
        }
        {// 底部搜索栏
            final TabMode tabMode = new TabMode(SEARCH_TAB,
                    R.drawable.tab_2_selector, "搜索",
                    R.drawable.tab_text_color_selector,
                    new SearchFriendFragment(), code == 2);//
            listTabModes.add(tabMode);

        }

        {// 底部消息栏
            final TabMode tabMode = new TabMode(MESSAGE_TAB,
                    R.drawable.tab_3_selector, "消息",
                    R.drawable.tab_text_color_selector, new MessageFragment(),
                    code == 3);
            listTabModes.add(tabMode);
        }

        {// 底部好友栏
            final TabMode tabMode = new TabMode(FRIEND_TAB,
                    R.drawable.tab_5_selector, "好友",
                    R.drawable.tab_text_color_selector, new FriendFragment(),
                    code == 4);
            listTabModes.add(tabMode);

        }
        actionBarFragment.creatTab(MainActivity.this, listTabModes,
                new TabFragment.IFocusChangeListener() {

                    @Override
                    public void OnFocusChange(int currentTabId, int tabIndex) {

                    }
                });

        // 这部分是底部menu的view控件
        menu_setting = (TextView) this.findViewById(R.id.iv_setting);
        menu_header = (LinearLayout) this.findViewById(R.id.menu_header);
        mDragLayout = (DragLayout) findViewById(R.id.dl);
        tv_name = (TextView) findViewById(R.id.tv_name);
        myRelativeLayout = (MyRelativeLayout) findViewById(R.id.rl_layout);
        mDragLayout.setBorder(actionBarFragment);
        myRelativeLayout.setDragLayout(mDragLayout);

        /**
         * 抽屜动作监听(侧滑时的动作监听)
         */
        mDragLayout
                .setOnLayoutDragingListener(new DragLayout.OnLayoutDragingListener() {

                    @Override
                    public void onOpen() {
                        // 打开
                        Log.d(TAG, "抽屉打开-------------");
                    }

                    @Override
                    public void onDraging(float percent) {
                        // 滑动中
                        Log.d(TAG, "抽屉滑动---------------");
                    }

                    @Override
                    public void onClose() {
                        // 关闭
                        Log.d(TAG, "抽屉关闭----------------");
                    }
                });

        menuListView = (ListView) findViewById(R.id.menu_listview);// 抽屉列表
        // 侧滑栏抽屉的点击事件(listview de点击事件)
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                if (position == 0) {
                    Toast.makeText(MainActivity.this, "我的会员特权", Toast.LENGTH_SHORT)
                            .show();

                }
                if (position == 1) {
                    Toast.makeText(MainActivity.this, "我的钱包", Toast.LENGTH_SHORT)
                            .show();

                }
                if (position == 2) {
                    Toast.makeText(MainActivity.this, "个性装扮", Toast.LENGTH_SHORT)
                            .show();

                }
                if (position == 3) {
                    Toast.makeText(MainActivity.this, "我的收藏", Toast.LENGTH_SHORT)
                            .show();

                }
                if (position == 4) {
                    Toast.makeText(MainActivity.this, "我的相册", Toast.LENGTH_SHORT)
                            .show();

                }
                if (position == 5) {
                    Toast.makeText(MainActivity.this, "我的文件", Toast.LENGTH_SHORT)
                            .show();

                }
                if (position == 6) {
                    Toast.makeText(MainActivity.this, "我的名片夹", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });
        initResideListener();// 个人中心、设置点击事件
    }

    private void initResideListener() {
        // 点击个人中心
        menu_header.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "头部点击事件", Toast.LENGTH_SHORT).show();

            }
        });

        // 昵称的点击事件
        tv_name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "请输入昵称", Toast.LENGTH_SHORT).show();
            }
        });

        // 进入设置界面(跳转到另一个界面)
        menu_setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 加载抽屉列表数据
     *
     * @return
     */
    private List<Map<String, Object>> getMenuData() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> item;

        item=new HashMap<>();
        item.put("item", "了解会员特权");
        item.put("image", R.drawable.my_privilege);
        data.add(item);

        item = new HashMap<>();
        item.put("item", "我的钱包");
        item.put("image", R.drawable.my_money);
        data.add(item);

        item = new HashMap<>();
        item.put("item", "个性装扮");
        item.put("image", R.drawable.my_charactor);
        data.add(item);

        item = new HashMap<>();
        item.put("item", "我的收藏");
        item.put("image", R.drawable.my_collect);
        data.add(item);

        item = new HashMap<>();
        item.put("item", "我的相册");
        item.put("image", R.drawable.my_picture);
        data.add(item);

        item = new HashMap<>();
        item.put("item", "我的文件");
        item.put("image", R.drawable.my_file);
        item.put("iv", R.drawable.icon_kehu_arrow);
        data.add(item);

        item=new HashMap<>();
        item.put("item", "我的名片夹");
        item.put("image", R.drawable.my_card);
        item.put("iv", R.drawable.icon_kehu_arrow);
        data.add(item);

        return data;
    }

    /**
     * activity对象回收
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < MangerActivitys.activitys.size(); i++) {
            if (MangerActivitys.activitys.get(i) != null) {
                ((Activity) MangerActivitys.activitys.get(i)).finish();
            }
        }
        finish();
        //gc()函数的作用只是提醒虚拟机：程序员希望进行一次垃圾回收
        System.gc();

    }
    /**
     * 返回键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("确定要退出吗？")
                    .setCancelable(false)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Intent intent = new Intent(
                                            Intent.ACTION_MAIN);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addCategory(Intent.CATEGORY_HOME);
                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
