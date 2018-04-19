package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.book.DiaryListActivity;
import com.jiangzg.ita.activity.book.WordListActivity;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.activity.settings.SettingsActivity;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

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
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 帮助
                        HelpActivity.goActivity(mFragment, Help.TYPE_BOOK_HOME);
                        break;
                    case R.id.menuSettings: // 设置
                        SettingsActivity.goActivity(mFragment);
                        break;
                }
                return true;
            }
        });
    }

    protected void loadData() {
        refreshData();
    }

    @OnClick({R.id.cvAccount, R.id.cvWhisper, R.id.cvDiary, R.id.cvWord,
            R.id.cvAudio, R.id.cvVideo, R.id.cvAlbum, R.id.cvMeet,
            R.id.cvGift, R.id.cvDream, R.id.cvPromise, R.id.cvAngry,
            R.id.cvAward})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvAccount: // TODO 账本
                break;
            case R.id.cvWhisper: // TODO 耳语
                break;
            case R.id.cvDiary: // 日记
                DiaryListActivity.goActivity(mFragment);
                break;
            case R.id.cvWord: // 留言
                WordListActivity.goActivity(mFragment);
                break;
            case R.id.cvAudio: // TODO 录音
                break;
            case R.id.cvVideo: // TODO 视频
                break;
            case R.id.cvAlbum: // TODO 相册
                break;
            case R.id.cvMeet: // TODO 相见
                break;
            case R.id.cvGift: // TODO 礼物
                break;
            case R.id.cvDream: // TODO 梦里
                break;
            case R.id.cvPromise: // TODO 承诺
                break;
            case R.id.cvAngry: // TODO 生气
                break;
            case R.id.cvAward: // TODO 补偿
                break;
        }
    }

    private void refreshData() {
        // TODO api
    }

}
