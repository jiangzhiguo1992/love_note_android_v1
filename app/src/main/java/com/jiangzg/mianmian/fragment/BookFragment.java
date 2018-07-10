package com.jiangzg.mianmian.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.AlbumListActivity;
import com.jiangzg.mianmian.activity.book.AngryListActivity;
import com.jiangzg.mianmian.activity.book.AudioListActivity;
import com.jiangzg.mianmian.activity.book.AwardListActivity;
import com.jiangzg.mianmian.activity.book.DiaryListActivity;
import com.jiangzg.mianmian.activity.book.DreamListActivity;
import com.jiangzg.mianmian.activity.book.FoodListActivity;
import com.jiangzg.mianmian.activity.book.GiftListActivity;
import com.jiangzg.mianmian.activity.book.MensesActivity;
import com.jiangzg.mianmian.activity.book.PromiseListActivity;
import com.jiangzg.mianmian.activity.book.ShyActivity;
import com.jiangzg.mianmian.activity.book.SleepActivity;
import com.jiangzg.mianmian.activity.book.SouvenirListActivity;
import com.jiangzg.mianmian.activity.book.TravelListActivity;
import com.jiangzg.mianmian.activity.book.VideoListActivity;
import com.jiangzg.mianmian.activity.book.WhisperListActivity;
import com.jiangzg.mianmian.activity.book.WordListActivity;
import com.jiangzg.mianmian.activity.common.SettingsActivity;
import com.jiangzg.mianmian.activity.couple.CouplePairActivity;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.base.BasePagerFragment;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Version;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.OnClick;

public class BookFragment extends BasePagerFragment<BookFragment> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;

    @BindView(R.id.cvSouvenir)
    CardView cvSouvenir;

    @BindView(R.id.cvTrends)
    CardView cvTrends;
    @BindView(R.id.cvMenses)
    CardView cvMenses;
    @BindView(R.id.cvShy)
    CardView cvShy;
    @BindView(R.id.cvSleep)
    CardView cvSleep;

    @BindView(R.id.cvWord)
    CardView cvWord;
    @BindView(R.id.cvWhisper)
    CardView cvWhisper;
    @BindView(R.id.cvDiary)
    CardView cvDiary;
    @BindView(R.id.cvAlbum)
    CardView cvAlbum;

    @BindView(R.id.cvAudio)
    CardView cvAudio;
    @BindView(R.id.cvVideo)
    CardView cvVideo;
    @BindView(R.id.cvFood)
    CardView cvFood;
    @BindView(R.id.cvTravel)
    CardView cvTravel;

    @BindView(R.id.cvGift)
    CardView cvGift;
    @BindView(R.id.cvPromise)
    CardView cvPromise;
    @BindView(R.id.cvAngry)
    CardView cvAngry;
    @BindView(R.id.cvDream)
    CardView cvDream;

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
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.nav_book), false);
        fitToolBar(tb);
        // srl
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    protected void loadData() {
        refreshData();
    }

    @Override
    public void onStart() {
        super.onStart();
        // menu
        refreshMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mFragment, Help.INDEX_BOOK_HOME);
                return true;
            case R.id.menuSettings: // 设置
                SettingsActivity.goActivity(mFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvSouvenir,
            R.id.cvTrends, R.id.cvMenses, R.id.cvShy, R.id.cvSleep,
            R.id.cvWord, R.id.cvWhisper, R.id.cvDiary, R.id.cvAlbum,
            R.id.cvAudio, R.id.cvVideo, R.id.cvFood, R.id.cvTravel,
            R.id.cvGift, R.id.cvPromise, R.id.cvAngry, R.id.cvDream,
            R.id.cvAward})
    public void onViewClicked(View view) {
        if (Couple.isBreak(SPHelper.getCouple())) {
            // 无配对
            CouplePairActivity.goActivity(mFragment);
            return;
        }
        // TODO book的密码+指纹验证(app启动后解密一次)
        switch (view.getId()) {
            case R.id.cvSouvenir: // 纪念日+愿望清单
                SouvenirListActivity.goActivity(mFragment);
                break;
            case R.id.cvTrends: // TODO 动态+统计
                break;
            case R.id.cvMenses: // 姨妈
                MensesActivity.goActivity(mFragment);
                break;
            case R.id.cvShy: // 羞羞
                ShyActivity.goActivity(mFragment);
                break;
            case R.id.cvSleep: // 睡眠
                SleepActivity.goActivity(mFragment);
                break;
            case R.id.cvWord: // 留言
                WordListActivity.goActivity(mFragment);
                break;
            case R.id.cvWhisper: // 耳语
                WhisperListActivity.goActivity(mFragment);
                break;
            case R.id.cvDiary: // 日记
                DiaryListActivity.goActivity(mFragment);
                break;
            case R.id.cvAlbum: // 相册
                AlbumListActivity.goActivity(mFragment);
                break;
            case R.id.cvAudio: // 音频
                AudioListActivity.goActivity(mFragment);
                break;
            case R.id.cvVideo: // 视频
                VideoListActivity.goActivity(mFragment);
                break;
            case R.id.cvFood: // 美食
                FoodListActivity.goActivity(mFragment);
                break;
            case R.id.cvTravel: // 游记
                TravelListActivity.goActivity(mFragment);
                break;
            case R.id.cvGift: // 礼物
                GiftListActivity.goActivity(mFragment);
                break;
            case R.id.cvPromise: // 承诺
                PromiseListActivity.goActivity(mFragment);
                break;
            case R.id.cvAngry: // 生气
                AngryListActivity.goActivity(mFragment);
                break;
            case R.id.cvDream: // 梦里
                DreamListActivity.goActivity(mFragment);
                break;
            case R.id.cvAward: // 奖励
                AwardListActivity.goActivity(mFragment);
                break;
        }
    }

    private void refreshMenu() {
        long noticeNoReadCount = SPHelper.getNoticeNoReadCount();
        Version version = SPHelper.getVersion();
        boolean redPoint = (noticeNoReadCount > 0) || (version != null);
        tb.getMenu().clear();
        tb.inflateMenu(redPoint ? R.menu.help_settings_point : R.menu.help_settings);
    }

    private void refreshData() {
        // TODO api
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        MyApp.get().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    protected void onFinish(Bundle state) {

    }

}
