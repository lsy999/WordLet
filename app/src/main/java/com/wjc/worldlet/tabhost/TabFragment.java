package com.wjc.worldlet.tabhost;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.wjc.worldlet.R;
import com.wjc.worldlet.activity.BaseActivity;
import com.wjc.worldlet.application.Constant;
import com.wjc.worldlet.entity.TabMode;

import java.lang.reflect.Field;
import java.util.ArrayList;


/**
 * Created by ${万嘉诚} on 2016/10/2.
 * 微信：wjc398556712
 * 自定义TAB控件
 */
public class TabFragment extends Fragment implements
        MyRadioGroup.OnCheckedChangeListener {
    private ArrayList<TabMode> mListTabModes = null;
    // 当前Tab页面索引
    private int currentTabIndex = -1;
    private IFocusChangeListener mListener;
    private MyRadioGroup mRadioGroup = null;

    private CustomViewPager mViewPager;


    /**
     * onCreateView返回的就是fragment要显示的view。
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_layout, container, false);
    }

    /**
     *  onViewCreated在onCreateView执行完后立即执行
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTabHost = (TabHost) getView().findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (CustomViewPager) getView().findViewById(
                R.id.tab_content_pager);
        // 禁止左右滑动
        mViewPager.setScanScroll(false);

        mRadioGroup = (MyRadioGroup) getView().findViewById(R.id.tabs_rg);
        mRadioGroup.setOnCheckedChangeListener(this);

        if (savedInstanceState != null && mTabHost != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState
                    .getString(Constant.KEY_TAB));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constant.KEY_TAB, mTabHost.getCurrentTabTag());
    }

    private TabHost mTabHost;
    private TabsAdapter mTabsAdapter;

    /**
     * 初始化Tab
     *
     * @param listTabModes
     */
    public void creatTab(BaseActivity activity,
                         ArrayList<TabMode> listTabModes, IFocusChangeListener listener) {
        if (listTabModes != null) {
            if (mTabsAdapter == null) {
                mTabsAdapter = new TabsAdapter(activity, mTabHost, mViewPager);
            }

            final int size = listTabModes.size();

            mViewPager.setOffscreenPageLimit(5);// 添加此处可以优化切换速度，但会占用内存

            mListTabModes = listTabModes;

            mListener = listener;

            for (int i = 0; i < size; i++) {
                final TabMode tabMode = listTabModes.get(i);
                if (tabMode == null) {
                    return;
                }

                final String radioButtonIdName = "tab_rb_layout" + i;
                // 根据名称获取资源id
                int radioButtonId = getResources().getIdentifier(
                        radioButtonIdName, "id", activity.getPackageName());
                if (radioButtonId <= 0) {
                    // 容错处理，以防某些手机不能通过getIdentifier查找到id
                    if (i == 0) {
                        radioButtonId = R.id.tab_rb_layout_0;
                    } else if (i == 1) {
                        radioButtonId = R.id.tab_rb_layout_1;
                    } else if (i == 2) {
                        radioButtonId = R.id.tab_rb_layout_2;
                    } else if (i == 3) {
                        radioButtonId = R.id.tab_rb_layout_3;
                    } else if (i == 4) {
                        radioButtonId = R.id.tab_rb_layout_4;
                    }
                }

                final View btnView = getView().findViewById(radioButtonId);
                if (btnView instanceof ViewStub) {
                    final ViewStub stub = (ViewStub) btnView;
                    stub.inflate();
                    final RadioButton radioButton = (RadioButton) getView()
                            .findViewById(R.id.tab_rb);
                    // 保存当前tab index和数据源
                    radioButton.setTag(R.id.tab_index, i);
                    radioButton.setTag(R.id.tab_mode_obj, tabMode);
                    {// 必设项
                        // id
                        radioButton.setId(tabMode.getTabId());
                        // Text
                        radioButton.setText(tabMode.getTabText());
                        // icon
                        radioButton.setCompoundDrawablesWithIntrinsicBounds(0,
                                tabMode.getIconResId(), 0, 0);
                        // 字体颜色
                        final int textColorResId = tabMode.getTextColorResId();
                        try {
                            final ColorStateList colors = getResources()
                                    .getColorStateList(textColorResId);
                            if (colors != null) {
                                radioButton.setTextColor(colors);
                            } else {
                                radioButton.setTextColor(getResources()
                                        .getColor(textColorResId));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            radioButton.setTextColor(getResources().getColor(
                                    textColorResId));
                        }

                    }

                    {// 可填项
                        // 设置字号，单位dip
                        final int textSize = tabMode.getTextSize();
                        if (textSize > -1) {
                            final CharSequence text = radioButton.getText();
                            final SpannableStringBuilder style = new SpannableStringBuilder(
                                    text);
                            style.setSpan(new AbsoluteSizeSpan(textSize, true),
                                    0, text.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            radioButton.setText(style);
                        }
                        // Background
                        final int tabSelectorResId = tabMode
                                .getTabSelectorResId();
                        if (tabSelectorResId > -1) {
                            radioButton.setBackgroundResource(tabSelectorResId);
                        }
                    }

                    if (!tabMode.isSetMidButton()) {
                        mTabsAdapter.addTab(
                                mTabHost.newTabSpec(tabMode.getTabText())
                                        .setIndicator(tabMode.getTabText()),
                                tabMode.getTabContent().getClass(), null);

                        stub.setVisibility(View.VISIBLE);

                        // 默认显示Tab
                        if (tabMode.isDefaultShow()) {
                            radioButton.setChecked(true);
                            mTabHost.setCurrentTabByTag(tabMode.getTabText());
                        }
                    }
                }

            }
        }
    }

    @Override
    public void onCheckedChanged(MyRadioGroup group, int checkedId) {
        if (getView() == null) {
            return;
        }
        final View view = getView().findViewById(checkedId);
        if (view instanceof RadioButton) {
            // 保存当前已显示的TAB id

            final RadioButton radioButton = (RadioButton) view;
            final TabMode tabMode = (TabMode) radioButton
                    .getTag(R.id.tab_mode_obj);
            if (tabMode == null) {
                return;
            }

            mTabHost.setCurrentTabByTag(tabMode.getTabText());

            final int index = (Integer) radioButton.getTag(R.id.tab_index);
            // 更新目标tab为当前tab
            currentTabIndex = index;

            if (mListener != null) {
                mListener.OnFocusChange(radioButton.getId(), index);
            }
        }
    }


    /**
     * 获取当前tab id
     *
     * @return
     */
    public int getCurrentTabId() {
        if (mListTabModes != null && currentTabIndex != -1) {
            final int size = mListTabModes.size();
            if (currentTabIndex < size) {
                return mListTabModes.get(currentTabIndex).getTabId();
            }
        }
        return -1;
    }

    public interface IFocusChangeListener {
        /**
         * Tab切换监听
         *
         * @param currentTabId
         * @param tabIndex
         */
        public void OnFocusChange(int currentTabId, int tabIndex);
    }


    /**
     * PagerAdapter
     * 没有能够复用View ,
     * 每次都是重新加载View这样挺耗性能的
     *
     * FragmentPagerAdapter
     * 他会把每个Fragment 都保存下来,
     * 他在 destroyItem 方法中, 只是把 Fragment detach 掉了并没有销毁 Fragment
     *
     * FragmentStatePagerAdapter
     * 只会保存Fragment的state保存下来,
     * 他在  destroyItem 方法中, 会把Fragment Remove 掉,
     * 但是会调用Fragment的 onSaveInstanceState 方法.
     * 然后销毁 Fragment
     */
    public class TabsAdapter extends FragmentStatePagerAdapter implements
            TabHost.OnTabChangeListener, CustomViewPager.OnPageChangeListener {
        private final Context mContext;
        private final TabHost mTabHost;
        private final CustomViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<>();

        public final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            public TabInfo(String _tag, Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }

        public class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                final View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabsAdapter(FragmentActivity activity, TabHost tabHost,
                           CustomViewPager pager) {
            super(getChildFragmentManager());
            mContext = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mContext));
            final String tag = tabSpec.getTag();
            final TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);
            mTabHost.addTab(tabSpec);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            final TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(),
                    info.args);
        }

        @Override
        public void onTabChanged(String tabId) {
            final int position = mTabHost.getCurrentTab();
            // mViewPager.setCurrentItem(position);
            mViewPager.setCurrentItem(position, false);// 不显示切换动画
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            final TabWidget widget = mTabHost.getTabWidget();
            final int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        public Object instantiateItem(ViewGroup container, int position) {
            // 解决程序切到后台，清理下进程在返回出现异常
            // android.os.BadParcelableException: ClassNotFoundException when
            // unmarshalling: android.support.v4.app.FragmentManagerState
            final Object fragment = super.instantiateItem(container, position);
            try {
                final Field saveFragmentStateField = Fragment.class
                        .getDeclaredField("mSavedFragmentState");
                saveFragmentStateField.setAccessible(true);
                final Bundle savedFragmentState = (Bundle) saveFragmentStateField
                        .get(fragment);
                if (savedFragmentState != null) {
                    savedFragmentState.setClassLoader(Fragment.class
                            .getClassLoader());
                }
            } catch (Exception e) {

            }
            return fragment;
        }
    }

}
