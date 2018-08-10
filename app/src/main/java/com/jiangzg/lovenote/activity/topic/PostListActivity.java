package com.jiangzg.lovenote.activity.topic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.FragmentPagerAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.PostKindInfo;
import com.jiangzg.lovenote.domain.PostSubKindInfo;
import com.jiangzg.lovenote.domain.RxEvent;
import com.jiangzg.lovenote.fragment.PostListFragment;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PostListActivity extends BaseActivity<PostListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tl)
    TabLayout tl;
    @BindView(R.id.vpFragment)
    ViewPager vpFragment;
    @BindView(R.id.tvSearch)
    TextView tvSearch;
    @BindView(R.id.llTop)
    LinearLayout llTop;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;

    private PostKindInfo kindInfo;
    private int searchIndex;
    private PostSubKindInfo subKindInfo;

    public static void goActivity(Fragment from, PostKindInfo kindInfo) {
        if (kindInfo == null) {
            LogUtils.w(PostListActivity.class, "goActivity", "kindInfo == null");
            return;
        }
        Intent intent = new Intent(from.getActivity(), PostListActivity.class);
        intent.putExtra("kindInfo", kindInfo);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        kindInfo = intent.getParcelableExtra("kindInfo");
        if (kindInfo.getPostSubKindInfoList() != null && kindInfo.getPostSubKindInfoList().size() > 0) {
            subKindInfo = kindInfo.getPostSubKindInfoList().get(0);
        }
        return R.layout.activity_post_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, kindInfo.getName(), true);
        // search
        searchIndex = 0;
        tvSearch.setText(ApiHelper.LIST_TOPIC_SHOW[searchIndex]);
        // fragment
        List<PostSubKindInfo> postSubKindInfoList = kindInfo.getPostSubKindInfoList();
        List<String> titleList = new ArrayList<>();
        final List<PostListFragment> fragmentList = new ArrayList<>();
        if (postSubKindInfoList != null && postSubKindInfoList.size() > 0) {
            for (PostSubKindInfo subKindInfo : postSubKindInfoList) {
                if (subKindInfo == null || !subKindInfo.isEnable()) continue;
                fragmentList.add(PostListFragment.newFragment(kindInfo, subKindInfo));
                titleList.add(subKindInfo.getName());
            }
        }
        // adapter
        FragmentPagerAdapter<PostListFragment> adapter = new FragmentPagerAdapter<>(getSupportFragmentManager());
        adapter.newData(titleList, fragmentList);
        // view
        vpFragment.setOffscreenPageLimit(fragmentList.size());
        vpFragment.setAdapter(adapter);
        tl.setupWithViewPager(vpFragment);
        vpFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                PostListFragment fragment = fragmentList.get(position);
                int searchType = fragment.getSearchType();
                for (int i = 0; i < ApiHelper.LIST_TOPIC_TYPE.length; i++) {
                    if (searchType == ApiHelper.LIST_TOPIC_TYPE[i]) {
                        searchIndex = i;
                        break;
                    }
                }
                subKindInfo = fragment.getSubKindInfo();
                tvSearch.setText(ApiHelper.LIST_TOPIC_SHOW[searchIndex]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSearch: // 搜索
                PostSearchActivity.goActivity(mActivity, kindInfo, subKindInfo);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llTop, R.id.llSearch, R.id.llAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llTop: // 置顶
                RxEvent<Boolean> event = new RxEvent<>(ConsHelper.EVENT_POST_GO_TOP, true);
                RxBus.post(event);
                break;
            case R.id.llSearch: // 搜索
                showSearchDialog();
                break;
            case R.id.llAdd: // 添加
                if (vpFragment == null || kindInfo == null) return;
                int currentItem = vpFragment.getCurrentItem();
                List<PostSubKindInfo> postSubKindInfoList = kindInfo.getPostSubKindInfoList();
                if (postSubKindInfoList != null && currentItem >= 0 && currentItem < postSubKindInfoList.size()) {
                    PostSubKindInfo subKindInfo = postSubKindInfoList.get(currentItem);
                    if (subKindInfo == null) return;
                    PostAddActivity.goActivity(mActivity, kindInfo, subKindInfo.getKind());
                }
                break;
        }
    }

    private void showSearchDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(ApiHelper.LIST_TOPIC_SHOW)
                .itemsCallbackSingleChoice(searchIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which < 0 || which >= ApiHelper.LIST_TOPIC_TYPE.length
                                || which >= ApiHelper.LIST_TOPIC_SHOW.length) {
                            return true;
                        }
                        searchIndex = which;
                        tvSearch.setText(ApiHelper.LIST_TOPIC_SHOW[searchIndex]);
                        RxEvent<Boolean> event;
                        switch (ApiHelper.LIST_TOPIC_TYPE[searchIndex]) {
                            case ApiHelper.LIST_TOPIC_OFFICIAL: // 官方
                                event = new RxEvent<>(ConsHelper.EVENT_POST_SEARCH_OFFICIAL, true);
                                break;
                            case ApiHelper.LIST_TOPIC_WELL: // 精华
                                event = new RxEvent<>(ConsHelper.EVENT_POST_SEARCH_WELL, true);
                                break;
                            default: // 普通
                                event = new RxEvent<>(ConsHelper.EVENT_POST_SEARCH_ALL, true);
                                break;
                        }
                        RxBus.post(event);
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

}
