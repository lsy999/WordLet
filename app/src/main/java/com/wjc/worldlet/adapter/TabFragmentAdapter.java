package com.wjc.worldlet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wjc.worldlet.fragment.MyFragment;

import java.util.ArrayList;

/**
 * Created by ${万嘉诚} on 2016/10/2.
 * 微信：wjc398556712
 * 适配fragment标题和fragment
 */
public class TabFragmentAdapter extends FragmentPagerAdapter {
    private String[] strs = null ;
    private ArrayList<MyFragment> fragments ;

    public TabFragmentAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    public void setData(String[] strs,ArrayList<MyFragment> fragments){
        if(strs == null){
            strs = new String[]{} ;
        }

        this.strs = strs ;

        if(fragments == null){
            fragments = new ArrayList<MyFragment>();
        }

        this.fragments = fragments ;
    }

    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        return  fragments != null && fragments.size() > 0 ? fragments.get(position) : null;
    }

    /**
      *String 继承于CharSequence，也就是说String也是CharSequence类型。
      * CharSequence是一个接口，它只包括length(), charAt(int index),
      * subSequence(int start, int end)这几个API接口。除了String实现了CharSequence之外，
      * StringBuffer和StringBuilder也实现了CharSequence接口。
      * 需要说明的是，CharSequence就是字符序列，String,
      * StringBuilder和StringBuffer本质上都是通过字符数组实现的！
      */
    @Override
    public CharSequence getPageTitle(int position) {
        return strs != null && strs.length > position ? strs[position] : "";
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return strs != null ? strs.length : 0;
    }

}
