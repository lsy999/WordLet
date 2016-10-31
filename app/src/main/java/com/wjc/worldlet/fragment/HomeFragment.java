package com.wjc.worldlet.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wjc.worldlet.R;
import com.wjc.worldlet.adapter.TabFragmentAdapter;
import com.wjc.worldlet.application.SoftApplication;
import com.wjc.worldlet.indicator.TabPageIndicator;

import java.util.ArrayList;

/**
 * Created by ${万嘉诚} on 2016/10/2.
 * 微信：wjc398556712
 */
public class HomeFragment extends MyFragment implements View.OnClickListener {
    private Context context;
    private View view;

    /*
    * 注意：有的引入控件TabPageIndicator后效果不一样需要在当前activity里面引入android:theme="@style/StyledIndicators"
    * 即，我们这里需要对HomeActivity里面的清单列表文件添加android:theme="@style/StyledIndicators"
    * */
    private TabPageIndicator indicator;

    private ViewPager mPager;

    private ArrayList<MyFragment> fragmentsList;

    private String[] tabContent = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_home, container, false);

//        setListener();//控件监听事件处理
        // 缓存的view需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个view已经有parent的错误。
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    protected void onVisible(boolean isInit) {
        //这个方法的目的就是当页面可见的时候才初始化，目的是避免fragment切换的时候每次都加载，
        //这里面控制加载过的数据切换页面的时候就不用在加载数据了
        if (isInit) {
            initView();//初始化控件
        }
    }

    /**
     * 适配器填充listView数据
     */
    private void initView() {
        context = this.getActivity();// 初始化上下文

        mPager = (ViewPager) view.findViewById(R.id.pager);
        indicator = (TabPageIndicator) view.findViewById(R.id.indicator);
        if (fragmentsList == null) {
            fragmentsList = new ArrayList<MyFragment>();
        }

        /**
         * 添加多个fragment往集合中（重点关注）
         *
         * **/
        fragmentsList.add(new RecommendedFragment());
        fragmentsList.add(new audioFragment());
        fragmentsList.add(new NetVideoFragment());
        fragmentsList.add(new PhotoFragment());

        /**
         *  给fragment添加标题（重点关注）
         */
        //给fragment添加标题
        if (tabContent == null) {
            tabContent = new String[]{getResources().getString(R.string.tv_top1),
                    getResources().getString(R.string.tv_top2), getResources().getString(R.string.tv_top3), getResources().getString(R.string.tv_top4)};
        }

        final FragmentPagerAdapter adapter = new TabFragmentAdapter(getChildFragmentManager());
        ((TabFragmentAdapter) adapter).setData(tabContent, fragmentsList);


        mPager.setAdapter(adapter);
        // 控制内存最多有几个页面
        mPager.setOffscreenPageLimit(fragmentsList.size());//由集合长度控制
        mPager.setCurrentItem(0);

        indicator.setViewPager(mPager);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                //这个是设置左滑切换底部view的边界，必须要设置
                Log.e("TAG", "position -------->" + position);
                Log.e("TAG", "SoftApplication.getInstance()" + SoftApplication.getInstance());
                SoftApplication.getInstance().setBorderViewPosition(position);
                Log.e("TAG", "SoftApplication.getInstance()" + SoftApplication.getInstance());
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


    }


    /**
     * onClick事件
     */
    @Override
    public void onClick(View view) {

        Toast.makeText(getActivity(), "view被点击了", Toast.LENGTH_SHORT).show();
    }

    /**
     * 清空Fragment栈
     */
    @Override
    public void onDestroy() {
        super.onPause();
        int backStackCount = getFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            getFragmentManager().popBackStack();
        }
    }



}
