package com.jiangzg.lovenote.fragment;

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
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.activity.note.AlbumListActivity;
import com.jiangzg.lovenote.activity.note.AngryListActivity;
import com.jiangzg.lovenote.activity.note.AudioListActivity;
import com.jiangzg.lovenote.activity.note.AwardListActivity;
import com.jiangzg.lovenote.activity.note.DiaryListActivity;
import com.jiangzg.lovenote.activity.note.DreamListActivity;
import com.jiangzg.lovenote.activity.note.FoodListActivity;
import com.jiangzg.lovenote.activity.note.GiftListActivity;
import com.jiangzg.lovenote.activity.note.LockActivity;
import com.jiangzg.lovenote.activity.note.MensesActivity;
import com.jiangzg.lovenote.activity.note.MovieListActivity;
import com.jiangzg.lovenote.activity.note.NoteTotalActivity;
import com.jiangzg.lovenote.activity.note.PromiseListActivity;
import com.jiangzg.lovenote.activity.note.ShyActivity;
import com.jiangzg.lovenote.activity.note.SleepActivity;
import com.jiangzg.lovenote.activity.note.SouvenirListActivity;
import com.jiangzg.lovenote.activity.note.TravelListActivity;
import com.jiangzg.lovenote.activity.note.TrendsListActivity;
import com.jiangzg.lovenote.activity.note.VideoListActivity;
import com.jiangzg.lovenote.activity.note.WhisperListActivity;
import com.jiangzg.lovenote.activity.note.WordListActivity;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.base.BaseFragment;
import com.jiangzg.lovenote.base.BasePagerFragment;
import com.jiangzg.lovenote.base.MyApp;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Help;
import com.jiangzg.lovenote.domain.Lock;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.Souvenir;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;
import com.jiangzg.lovenote.view.MultiLoveUpLayout;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class NoteFragment extends BasePagerFragment<NoteFragment> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.loveSouvenir)
    MultiLoveUpLayout loveSouvenir;

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

    @BindView(R.id.cvShy)
    CardView cvShy;
    @BindView(R.id.cvMenses)
    CardView cvMenses;
    @BindView(R.id.cvSleep)
    CardView cvSleep;

    @BindView(R.id.cvAudio)
    CardView cvAudio;
    @BindView(R.id.cvVideo)
    CardView cvVideo;
    @BindView(R.id.cvAlbum)
    CardView cvAlbum;

    @BindView(R.id.cvWord)
    CardView cvWord;
    @BindView(R.id.cvWhisper)
    CardView cvWhisper;
    @BindView(R.id.cvDiary)
    CardView cvDiary;
    @BindView(R.id.cvAward)
    CardView cvAward;
    @BindView(R.id.cvDream)
    CardView cvDream;
    @BindView(R.id.cvGift)
    CardView cvGift;
    @BindView(R.id.cvFood)
    CardView cvFood;
    @BindView(R.id.cvTravel)
    CardView cvTravel;
    @BindView(R.id.cvAngry)
    CardView cvAngry;
    @BindView(R.id.cvPromise)
    CardView cvPromise;
    @BindView(R.id.cvMovie)
    CardView cvMovie;

    @BindView(R.id.cvTotal)
    CardView cvTotal;
    @BindView(R.id.cvTrends)
    CardView cvTrends;
    @BindView(R.id.cvRecycle)
    CardView cvRecycle;

    private Lock lock; // 默认没锁 + 没解开
    private Souvenir souvenirLatest;
    private Observable<Lock> obLockRefresh;
    private Runnable souvenirCountDownTask;
    private String souvenirCountDownFormat;
    private Call<Result> call;

    public static NoteFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(NoteFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_note;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, mActivity.getString(R.string.nav_note), false);
        fitToolBar(tb);
        souvenirCountDownFormat = mActivity.getString(R.string.count_down_space_holder);
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
        refreshNoteView();
    }

    protected void loadData() {
        // event
        obLockRefresh = RxBus.register(ConsHelper.EVENT_LOCK_REFRESH, new Action1<Lock>() {
            @Override
            public void call(Lock lock) {
                NoteFragment.this.lock = lock;
                refreshData();
            }
        });
        // data
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RxBus.unregister(ConsHelper.EVENT_LOCK_REFRESH, obLockRefresh);
        stopSouvenirCountDownTask();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mFragment, Help.INDEX_NOTE_HOME);
                return true;
            case R.id.menuLock: // 密码锁
                LockActivity.goActivity(mFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvSouvenir,
            R.id.cvMenses, R.id.cvShy, R.id.cvSleep,
            R.id.cvAudio, R.id.cvVideo, R.id.cvAlbum,
            R.id.cvWord, R.id.cvWhisper, R.id.cvDiary, R.id.cvAward,
            R.id.cvDream, R.id.cvMovie, R.id.cvFood, R.id.cvTravel,
            R.id.cvAngry, R.id.cvGift, R.id.cvPromise,
            R.id.cvTrends, R.id.cvTotal, R.id.cvRecycle})
    public void onViewClicked(View view) {
        if (Couple.isBreak(SPHelper.getCouple())) {
            // 无效配对
            CouplePairActivity.goActivity(mFragment);
            return;
        }
        if (lock == null) {
            // 锁信息没有返回
            refreshData(); // 防止没配对前就来过这里，配对后这里不刷新
            return;
        } else if (lock.isLock()) {
            // 上锁且没有解开
            LockActivity.goActivity(mFragment);
            return;
        }
        switch (view.getId()) {
            case R.id.cvSouvenir: // 纪念日
                SouvenirListActivity.goActivity(mFragment);
                break;
            case R.id.cvShy: // 羞羞
                ShyActivity.goActivity(mFragment);
                break;
            case R.id.cvMenses: // 姨妈
                MensesActivity.goActivity(mFragment);
                break;
            case R.id.cvSleep: // 睡眠
                SleepActivity.goActivity(mFragment);
                break;
            case R.id.cvAudio: // 音频
                AudioListActivity.goActivity(mFragment);
                break;
            case R.id.cvVideo: // 视频
                VideoListActivity.goActivity(mFragment);
                break;
            case R.id.cvAlbum: // 相册
                AlbumListActivity.goActivity(mFragment);
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
            case R.id.cvAward: // 奖励
                AwardListActivity.goActivity(mFragment);
                break;
            case R.id.cvDream: // 梦里
                DreamListActivity.goActivity(mFragment);
                break;
            case R.id.cvMovie: // 电影
                MovieListActivity.goActivity(mFragment);
                break;
            case R.id.cvFood: // 美食
                FoodListActivity.goActivity(mFragment);
                break;
            case R.id.cvTravel: // 游记
                TravelListActivity.goActivity(mFragment);
                break;
            case R.id.cvAngry: // 生气
                AngryListActivity.goActivity(mFragment);
                break;
            case R.id.cvGift: // 礼物
                GiftListActivity.goActivity(mFragment);
                break;
            case R.id.cvPromise: // 承诺
                PromiseListActivity.goActivity(mFragment);
                break;
            case R.id.cvTotal: // 统计
                NoteTotalActivity.goActivity(mFragment);
                break;
            case R.id.cvTrends: // 动态
                TrendsListActivity.goActivity(mFragment);
                break;
            case R.id.cvRecycle: // 回收箱
                ToastUtils.show(mActivity.getString(R.string.function_no_open_please_wait));
                break;
        }
    }

    private void refreshData() {
        if (Couple.isBreak(SPHelper.getCouple())) {
            // 无效配对
            srl.setRefreshing(false);
            tvSouvenirEmpty.setVisibility(View.VISIBLE);
            rlSouvenir.setVisibility(View.GONE);
            return;
        }
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api
        long near = TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong());
        call = new RetrofitHelper().call(API.class).noteHomeGet(near);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                lock = data.getLock();
                if (lock == null) {
                    lock = new Lock();
                    lock.setLock(false);
                }
                souvenirLatest = data.getSouvenirLatest();
                refreshNoteView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void refreshNoteView() {
        stopSouvenirCountDownTask();// 先停止倒计时
        if (lock == null || lock.isLock()) {
            // 锁信息没有返回，或者是上锁且没有解开
            tvSouvenirEmpty.setVisibility(View.VISIBLE);
            rlSouvenir.setVisibility(View.GONE);
            return;
        }
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
        String yearShow = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_anniversary), yearBetween);
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
                        stopSouvenirCountDownTask(); // 停止倒计时
                        refreshData(); // 刷新数据
                    } else {
                        if (betweenTime / ConstantUtils.HOUR < 24) {
                            startLoveAnim();
                        } else {
                            stopLoveAnim();
                        }
                        tvSouvenirCountDown.setText(getCountDownShow(betweenTime));
                        MyApp.get().getHandler().postDelayed(this, ConstantUtils.SEC);
                    }
                }
            };
        }
        return souvenirCountDownTask;
    }

    private void startLoveAnim() {
        if (loveSouvenir.getVisibility() != View.VISIBLE) {
            loveSouvenir.setVisibility(View.VISIBLE);
        }
        if (!loveSouvenir.isGoing()) {
            loveSouvenir.startUp(500);
        }
    }

    private void stopLoveAnim() {
        if (loveSouvenir.getVisibility() == View.VISIBLE) {
            loveSouvenir.setVisibility(View.GONE);
        }
        if (loveSouvenir.isGoing()) {
            loveSouvenir.cancelUp();
        }
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
        String timeShow = " " + dayShow + mActivity.getString(R.string.dayT) + " " + hourShow + ":" + minShow + ":" + secShow;
        return String.format(Locale.getDefault(), souvenirCountDownFormat, timeShow);
    }

    private void stopSouvenirCountDownTask() {
        if (souvenirCountDownTask != null) {
            MyApp.get().getHandler().removeCallbacks(souvenirCountDownTask);
            souvenirCountDownTask = null;
        }
    }

}
