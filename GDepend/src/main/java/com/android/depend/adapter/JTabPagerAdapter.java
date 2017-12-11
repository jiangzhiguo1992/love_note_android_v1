package com.android.depend.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-11-9.
 * describe 通用tab-pager适配器
 */
public class JTabPagerAdapter<T extends Fragment> extends FragmentPagerAdapter {

    private List<String> titleList;  // fragment
    private List<T> fragmentList; // tab标题

    public JTabPagerAdapter(FragmentManager fm) {
        super(fm);
        titleList = new ArrayList<>();
        fragmentList = new ArrayList<>();
    }

    public void newData(List<String> titles, List<T> fragments) {
        fragmentList.clear();
        fragmentList.addAll(fragments);
        titleList.clear();
        titleList.addAll(titles);
        notifyDataSetChanged();
    }

    public void addData(List<String> titles, List<T> fragments) {
        titleList.addAll(titles);
        fragmentList.addAll(fragments);
        notifyDataSetChanged();
    }

    public void addData(String titles, T fragment) {
        fragmentList.add(fragment);
        titleList.add(titles);
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
        return titleList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titleList != null && titleList.size() > position) {
            return titleList.get(position);
        } else {
            return "";
        }
    }
}
