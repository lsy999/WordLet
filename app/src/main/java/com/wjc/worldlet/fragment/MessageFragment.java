package com.wjc.worldlet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjc.worldlet.R;

/**
 * Created by ${万嘉诚} on 2016/10/2.
 * 底部信息栏
 */
public class MessageFragment extends MyFragment implements View.OnClickListener{
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_message, container, false);

        return view;
    }
    /**
     * onClick事件
     */
    @Override
    public void onClick(View view) {
    }




    @Override
    protected void onVisible(boolean isInit) {

    }
}
