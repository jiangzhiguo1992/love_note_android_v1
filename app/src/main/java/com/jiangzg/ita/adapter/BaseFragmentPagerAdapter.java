package com.jiangzg.ita.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-11-9.
 * describe 通用tab-pager适配器
 */
public class BaseFragmentPagerAdapter<T extends Fragment> extends FragmentPagerAdapter {

    private List<String> titleList;  // tab标题
    private List<T> fragmentList; // fragment

    public BaseFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        titleList = new ArrayList<>();
        fragmentList = new ArrayList<>();
    }

    public void newData(List<String> titles, List<T> fragments) {
        titleList.clear();
        if (titles != null) {
            titleList.addAll(titles);
        }
        fragmentList.clear();
        fragmentList.addAll(fragments);
        notifyDataSetChanged();
    }

    public void addData(List<String> titles, List<T> fragments) {
        if (titles != null) {
            titleList.addAll(titles);
        }
        fragmentList.addAll(fragments);
        notifyDataSetChanged();
    }

    public void addData(String titles, T fragment) {
        if (titles != null) {
            titleList.add(titles);
        }
        fragmentList.add(fragment);
        notifyDataSetChanged();
    }

    public List<T> getFragmentList() {
        return fragmentList;
    }

    public List<String> getTitleList() {
        return titleList;
    }

    @Override
    public int getCount() {
        if (titleList.size() <= 0) {
            return fragmentList.size();
        } else {
            return titleList.size();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titleList.size() > position) {
            return titleList.get(position);
        } else {
            return "";
        }
    }
}
