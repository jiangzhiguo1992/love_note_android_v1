package com.jiangzg.mianmian.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import butterknife.BindView;

public class MapSearchActivity extends BaseActivity<MapSearchActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, MapSearchActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_map_search;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.address_search), true);

    }

    @Override
    protected void initData(Bundle state) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuComplete: // 完成
                // TODO
                //if (locationInfo == null) {
                //    ToastUtils.show(getString(R.string.please_select_location));
                //    return true;
                //}
                //RxEvent<LocationInfo> event = new RxEvent<>(ConsHelper.EVENT_MAP_SEARCH, locationInfo);
                //RxBus.post(event);
                mActivity.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
