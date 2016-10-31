package com.wjc.worldlet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.wjc.worldlet.application.Constant;

/**
 * Created by ${万嘉诚} on 2016/10/1.
 * 自定义Fragment，用来处理多层Fragment时，点击back键返回上一层
 */
public abstract class MyFragment extends Fragment {
    private boolean isOnInit = true;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null && isSaveState()) {
            isOnInit = savedInstanceState
                    .getBoolean(Constant.KEY_TAB_INIT);
        }

        if (isVisibleToUser) {
            onVisible(isOnInit);
            isOnInit = false;
        }
    }

    private boolean isVisibleToUser = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;

        if (isVisibleToUser && getView() != null) {
            onVisible(isOnInit);
            isOnInit = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (isSaveState()) {
            outState.putBoolean(Constant.KEY_TAB_INIT, isOnInit);
        }
    }


    /**
     * 是否保存初始化状态
     *
     * @return
     */
    public boolean isSaveState() {
        return true;
    }

    /**
     * 监听Fragment是否显示，isInit是否初为首次初始化，当把Fragment加入TabFragment时使用
     *
     * 由于ViewPager是预加载，所以联网获取数据需要判断当前Fragment是否显示，然后在获取数据
     */
    protected abstract void onVisible(boolean isInit);
}
