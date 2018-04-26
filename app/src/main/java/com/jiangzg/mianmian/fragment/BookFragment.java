package com.jiangzg.mianmian.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.DiaryListActivity;
import com.jiangzg.mianmian.activity.book.WhisperListActivity;
import com.jiangzg.mianmian.activity.book.WordListActivity;
import com.jiangzg.mianmian.activity.common.HelpActivity;
import com.jiangzg.mianmian.activity.settings.SettingsActivity;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.base.BasePagerFragment;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.OnClick;

public class BookFragment extends BasePagerFragment<BookFragment> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.cvAccount)
    CardView cvAccount;
    @BindView(R.id.cvWhisper)
    CardView cvWhisper;
    @BindView(R.id.cvDiary)
    CardView cvDiary;
    @BindView(R.id.cvWord)
    CardView cvWord;
    @BindView(R.id.cvAudio)
    CardView cvAudio;
    @BindView(R.id.cvVideo)
    CardView cvVideo;
    @BindView(R.id.cvAlbum)
    CardView cvAlbum;
    @BindView(R.id.cvMeet)
    CardView cvMeet;
    @BindView(R.id.cvGift)
    CardView cvGift;
    @BindView(R.id.cvDream)
    CardView cvDream;
    @BindView(R.id.cvPromise)
    CardView cvPromise;
    @BindView(R.id.cvAngry)
    CardView cvAngry;
    @BindView(R.id.cvAward)
    CardView cvAward;
    @BindView(R.id.cvSchedule)
    CardView cvSchedule;
    @BindView(R.id.cvStatistics)
    CardView cvStatistics;

    public static BookFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(BookFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_book;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.small_book), false);
        fitToolBar(tb);
        // srl
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        // menu
        tb.inflateMenu(R.menu.help_settings);
    }

    protected void loadData() {
        refreshData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mFragment, Help.TYPE_BOOK_HOME);
                return true;
            case R.id.menuSettings: // 设置
                SettingsActivity.goActivity(mFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvWord, R.id.cvWhisper, R.id.cvDiary, R.id.cvAlbum,
            R.id.cvAudio, R.id.cvVideo, R.id.cvMeet, R.id.cvAward,
            R.id.cvAngry, R.id.cvGift, R.id.cvPromise, R.id.cvDream,
            R.id.cvStatistics, R.id.cvAccount, R.id.cvSchedule})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvWord: // 留言
                WordListActivity.goActivity(mFragment);
                break;
            case R.id.cvWhisper: // 耳语
                WhisperListActivity.goActivity(mActivity);
                break;
            case R.id.cvDiary: // 日记
                DiaryListActivity.goActivity(mFragment);
                break;
            case R.id.cvAlbum: // TODO 相册
                break;
            case R.id.cvAudio: // TODO 录音
                break;
            case R.id.cvVideo: // TODO 视频
                break;
            case R.id.cvMeet: // TODO 相见
                break;
            case R.id.cvAward: // TODO 补偿
                break;
            case R.id.cvAngry: // TODO 生气
                break;
            case R.id.cvGift: // TODO 礼物
                break;
            case R.id.cvPromise: // TODO 承诺
                break;
            case R.id.cvDream: // TODO 梦里
                break;
            case R.id.cvStatistics: // TODO 统计
                break;
            case R.id.cvAccount: // TODO 账本
                break;
            case R.id.cvSchedule: // TODO 日程
                break;
        }
    }

    private void refreshData() {
        // TODO api
    }

}
