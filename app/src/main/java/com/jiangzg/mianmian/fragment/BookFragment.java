package com.jiangzg.mianmian.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
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
import com.jiangzg.mianmian.activity.book.TrendsListActivity;
import com.jiangzg.mianmian.activity.book.VideoListActivity;
import com.jiangzg.mianmian.activity.book.WhisperListActivity;
import com.jiangzg.mianmian.activity.book.WordListActivity;
import com.jiangzg.mianmian.activity.couple.CouplePairActivity;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.base.BasePagerFragment;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.Souvenir;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class BookFragment extends BasePagerFragment<BookFragment> {

    private static boolean canLock = false; // 是否开启锁
    private static boolean openLock = false; // 是否解开锁

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;

    @BindView(R.id.cvSouvenir)
    CardView cvSouvenir;
    @BindView(R.id.tvSouvenirEmpty)
    TextView tvSouvenirEmpty;
    @BindView(R.id.rlSouvenir)
    RelativeLayout rlSouvenir;
    @BindView(R.id.tvSouvenirYear)
    TextView tvSouvenirYear;
    @BindView(R.id.tvSouvenirTitle)
    TextView tvSouvenirTitle;
    @BindView(R.id.tvSouvenirCountDown)
    TextView tvSouvenirCountDown;

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

    private Souvenir souvenirLatest;
    private Call<Result> call;
    private Runnable souvenirCountDownTask;
    private String souvenirCountDownFormat;

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
        souvenirCountDownFormat = getString(R.string.count_down_space_holder);
        // menu
        tb.inflateMenu(R.menu.help_lock);
        // srl
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        // souvenir
        refreshBookView();
    }

    protected void loadData() {
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
        stopCoupleCountDownTask();
        RetrofitHelper.cancel(call);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mFragment, Help.INDEX_BOOK_HOME);
                return true;
            case R.id.menuLock: // 密码锁
                // TODO 用户决定是否开启 开关功能 + 初始化密码 + 修改密码 + 忘记密码
                // TODO book的密码+指纹验证(app启动后解密一次)
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
        switch (view.getId()) {
            case R.id.cvSouvenir: // 纪念日
                SouvenirListActivity.goActivity(mFragment);
                break;
            case R.id.cvTrends: // 动态
                TrendsListActivity.goActivity(mFragment);
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

    private void refreshData() {
        if (Couple.isBreak(SPHelper.getCouple())) {
            // 无效配对
            tvSouvenirEmpty.setVisibility(View.VISIBLE);
            rlSouvenir.setVisibility(View.GONE);
            return;
        }
        if (canLock && !openLock) {
            // 上锁且没有解开
            tvSouvenirEmpty.setVisibility(View.VISIBLE);
            rlSouvenir.setVisibility(View.GONE);
            return;
        }
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        long near = TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong());
        call = new RetrofitHelper().call(API.class).bookHomeGet(near);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                souvenirLatest = data.getSouvenirLatest();
                refreshBookView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void refreshBookView() {
        stopCoupleCountDownTask();// 先停止倒计时
        if (souvenirLatest == null || souvenirLatest.getId() <= 0) {
            tvSouvenirEmpty.setVisibility(View.VISIBLE);
            rlSouvenir.setVisibility(View.GONE);
            return;
        }
        tvSouvenirEmpty.setVisibility(View.GONE);
        rlSouvenir.setVisibility(View.VISIBLE);
        // data
        String title = souvenirLatest.getTitle();
        Calendar calHappen = DateUtils.getCurrentCalendar();
        long timeNow = calHappen.getTimeInMillis();
        int yearHappen = calHappen.get(Calendar.YEAR);
        calHappen.setTimeInMillis(TimeHelper.getJavaTimeByGo(souvenirLatest.getHappenAt()));
        int yearOriginal = calHappen.get(Calendar.YEAR);
        calHappen.set(Calendar.YEAR, yearHappen);
        long timeHappen = calHappen.getTimeInMillis();
        if (timeHappen < timeNow) {
            // 是明年的
            ++yearHappen;
            calHappen.set(Calendar.YEAR, yearHappen);
        }
        int yearBetween = yearHappen - yearOriginal;
        String yearShow = String.format(Locale.getDefault(), getString(R.string.holder_anniversary), yearBetween);
        // view
        tvSouvenirYear.setText(yearShow);
        tvSouvenirTitle.setText(title);
        // task
        MyApp.get().getHandler().post(getSouvenirCountDownTask(calHappen.getTimeInMillis()));
    }

    private Runnable getSouvenirCountDownTask(final long tartTime) {
        if (souvenirCountDownTask == null) {
            souvenirCountDownTask = new Runnable() {
                @Override
                public void run() {
                    long betweenTime = tartTime - DateUtils.getCurrentLong();
                    if (betweenTime <= 0) {
                        // TODO 纪念日到点彩蛋，刷新界面
                        stopCoupleCountDownTask(); // 停止倒计时
                        refreshData(); // 刷新数据
                    } else {
                        tvSouvenirCountDown.setText(getCountDownShow(betweenTime));
                        MyApp.get().getHandler().postDelayed(this, ConstantUtils.SEC);
                    }
                }
            };
        }
        return souvenirCountDownTask;
    }

    private String getCountDownShow(long betweenTime) {
        long day = betweenTime / ConstantUtils.DAY;
        long hourTotal = betweenTime - (day * ConstantUtils.DAY);
        long hour = hourTotal / ConstantUtils.HOUR;
        long minTotal = hourTotal - (hour * ConstantUtils.HOUR);
        long min = minTotal / ConstantUtils.MIN;
        long secTotal = minTotal - (min * ConstantUtils.MIN);
        long sec = secTotal / ConstantUtils.SEC;
        String dayShow = String.valueOf(day);
        String hourShow = String.valueOf(hour);
        if (hourShow.length() <= 1) {
            hourShow = "0" + hourShow;
        }
        String minShow = String.valueOf(min);
        if (minShow.length() <= 1) {
            minShow = "0" + minShow;
        }
        String secShow = String.valueOf(sec);
        if (secShow.length() <= 1) {
            secShow = "0" + secShow;
        }
        String timeShow = " " + dayShow + getString(R.string.dayT) + " " + hourShow + ":" + minShow + ":" + secShow;
        return String.format(Locale.getDefault(), souvenirCountDownFormat, timeShow);
    }

    private void stopCoupleCountDownTask() {
        if (souvenirCountDownTask != null) {
            MyApp.get().getHandler().removeCallbacks(souvenirCountDownTask);
            souvenirCountDownTask = null;
        }
    }

}
