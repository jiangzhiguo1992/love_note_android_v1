package com.jiangzg.lovenote.controller.adapter.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-11-9.
 * describe 通用tab-pager适配器
 */
public class CommonFragmentAdapter<T extends Fragment> extends FragmentStatePagerAdapter {

    private List<String> titleList;  // tab标题
    private List<T> fragmentList; // fragment

    public CommonFragmentAdapter(FragmentManager fm) {
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

    public void addData(int index, String titles, T fragment) {
        if (titles != null) {
            titleList.add(index, titles);
        }
        fragmentList.add(index, fragment);
        notifyDataSetChanged();
    }

    public void removeData(int index) {
        if (titleList.size() > index) {
            titleList.remove(index);
        }
        if (fragmentList.size() > index) {
            fragmentList.remove(index);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        titleList.clear();
        fragmentList.clear();
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
        if (titleList == null || titleList.size() <= 0) {
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
        if (titleList != null && titleList.size() > position) {
            return titleList.get(position);
        } else {
            return "";
        }
    }
}
