package com.jiangzg.mianmian.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.SettingsActivity;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.base.BasePagerFragment;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Version;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import butterknife.BindView;

public class MoreFragment extends BasePagerFragment<MoreFragment> {

    @BindView(R.id.tb)
    Toolbar tb;

    public static MoreFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(MoreFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_more;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.nav_more), false);
        fitToolBar(tb);
    }

    protected void loadData() {
        //ToastUtils.show(getString(R.string.nav_more) + " 加载数据");
    }

    @Override
    protected void onFinish(Bundle state) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // menu
        if (isVisibleToUser) tb.invalidate();
    }

    // 不能和note的一样，会显示不出来
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        long noticeNoReadCount = SPHelper.getNoticeNoReadCount();
        Version version = SPHelper.getVersion();
        boolean redPoint = (noticeNoReadCount > 0) || (version != null);
        inflater.inflate(redPoint ? R.menu.help_settings_point : R.menu.help_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mFragment, Help.INDEX_MORE_HOME);
                return true;
            case R.id.menuSettings: // 设置
                SettingsActivity.goActivity(mFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
