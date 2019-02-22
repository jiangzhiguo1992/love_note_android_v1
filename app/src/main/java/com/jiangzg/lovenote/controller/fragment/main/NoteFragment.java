package com.jiangzg.lovenote.controller.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.controller.activity.note.LockActivity;
import com.jiangzg.lovenote.controller.activity.note.SouvenirListActivity;
import com.jiangzg.lovenote.controller.activity.note.TrendsListActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.adapter.note.ModelAdapter;
import com.jiangzg.lovenote.controller.fragment.base.BaseFragment;
import com.jiangzg.lovenote.controller.fragment.base.BasePagerFragment;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.engine.NoteCustom;
import com.jiangzg.lovenote.model.entity.Lock;
import com.jiangzg.lovenote.model.entity.Souvenir;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;
import com.jiangzg.lovenote.view.MultiLoveUpLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class NoteFragment extends BasePagerFragment<NoteFragment> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.loveSouvenir)
    MultiLoveUpLayout loveSouvenir;

    @BindView(R.id.rootSouvenir)
    RelativeLayout rootSouvenir;
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

    @BindView(R.id.lineLive)
    LinearLayout lineLive;
    @BindView(R.id.rvLive)
    RecyclerView rvLive;
    @BindView(R.id.lineMedia)
    LinearLayout lineMedia;
    @BindView(R.id.rvMedia)
    RecyclerView rvMedia;
    @BindView(R.id.lineNote)
    LinearLayout lineNote;
    @BindView(R.id.rvNote)
    RecyclerView rvNote;
    @BindView(R.id.lineOther)
    LinearLayout lineOther;
    @BindView(R.id.rvOther)
    RecyclerView rvOther;

    private Lock lock;
    private Runnable souvenirCountDownTask;
    private RecyclerHelper rhLive, rhNote, rhMedia, rhOther;

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
        // srl
        srl.setOnRefreshListener(this::refreshData);
        // rv
        rhLive = new RecyclerHelper(rvLive)
                .initLayoutManager(new GridLayoutManager(mActivity, 4, LinearLayoutManager.VERTICAL, false))
                .initAdapter(new ModelAdapter(mFragment))
                .setAdapter()
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        goActivity(adapter, position);
                    }
                });
        rhNote = new RecyclerHelper(rvNote)
                .initLayoutManager(new GridLayoutManager(mActivity, 4, LinearLayoutManager.VERTICAL, false))
                .initAdapter(new ModelAdapter(mFragment))
                .setAdapter()
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        goActivity(adapter, position);
                    }
                });
        rhMedia = new RecyclerHelper(rvMedia)
                .initLayoutManager(new GridLayoutManager(mActivity, 4, LinearLayoutManager.VERTICAL, false))
                .initAdapter(new ModelAdapter(mFragment))
                .setAdapter()
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        goActivity(adapter, position);
                    }
                });
        rhOther = new RecyclerHelper(rvOther)
                .initLayoutManager(new GridLayoutManager(mActivity, 4, LinearLayoutManager.VERTICAL, false))
                .initAdapter(new ModelAdapter(mFragment))
                .setAdapter()
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        goActivity(adapter, position);
                    }
                });
        // custom
        customView();
        // souvenir
        refreshNoteView(null);
    }

    protected void loadData() {
        // event
        Observable<Lock> obLockRefresh = RxBus.register(RxBus.EVENT_LOCK_REFRESH, lock -> {
            NoteFragment.this.lock = lock;
            refreshData();
        });
        pushBus(RxBus.EVENT_LOCK_REFRESH, obLockRefresh);
        Observable<NoteCustom> obCustomRefresh = RxBus.register(RxBus.EVENT_CUSTOM_REFRESH, custom -> customView());
        pushBus(RxBus.EVENT_CUSTOM_REFRESH, obCustomRefresh);
        Observable<List<Souvenir>> obSouvenirRefresh = RxBus.register(RxBus.EVENT_SOUVENIR_LIST_REFRESH, custom -> refreshData());
        pushBus(RxBus.EVENT_SOUVENIR_LIST_REFRESH, obSouvenirRefresh);
        Observable<Souvenir> obSouvenirDeleteRefresh = RxBus.register(RxBus.EVENT_SOUVENIR_LIST_ITEM_DELETE, custom -> refreshData());
        pushBus(RxBus.EVENT_SOUVENIR_LIST_ITEM_DELETE, obSouvenirDeleteRefresh);
        // data
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
        stopSouvenirCountDownTask();
        RecyclerHelper.release(rhLive);
        RecyclerHelper.release(rhNote);
        RecyclerHelper.release(rhMedia);
        RecyclerHelper.release(rhOther);
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
                HelpActivity.goActivity(mFragment, HelpActivity.INDEX_NOTE_HOME);
                return true;
            case R.id.menuLock: // 密码锁
                LockActivity.goActivity(mFragment);
                return true;
            case R.id.menuTrends: // 动态
                if (refuseNext()) return true;
                TrendsListActivity.goActivity(mActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvSouvenir})
    public void onViewClicked(View view) {
        if (refuseNext()) return;
        switch (view.getId()) {
            case R.id.cvSouvenir: // 纪念日
                SouvenirListActivity.goActivity(mFragment);
                break;
        }
    }

    private boolean refuseNext() {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            // 无效配对
            CouplePairActivity.goActivity(mFragment);
            return true;
        }
        if (lock == null) {
            // 锁信息没有返回，防止没配对前就来过这里，配对后这里不刷新
            refreshData();
            return true;
        } else if (lock.isLock()) {
            // 上锁且没有解开
            LockActivity.goActivity(mFragment);
            return true;
        }
        return false;
    }

    private void customView() {
        NoteCustom custom = SPHelper.getNoteCustom();
        // souvenir
        rootSouvenir.setVisibility(custom.isSouvenir() ? View.VISIBLE : View.GONE);
        // live
        boolean isLive = custom.isShy() || custom.isMenses() || custom.isSleep();
        lineLive.setVisibility(isLive ? View.VISIBLE : View.GONE);
        rvLive.setVisibility(isLive ? View.VISIBLE : View.GONE);
        ArrayList<Integer> dataLive = new ArrayList<>();
        if (custom.isMenses()) dataLive.add(ModelAdapter.MENSES);
        if (custom.isShy()) dataLive.add(ModelAdapter.SHY);
        if (custom.isSleep()) dataLive.add(ModelAdapter.SLEEP);
        rhLive.dataNew(dataLive);
        // note
        boolean isNote = custom.isWord() || custom.isWhisper() || custom.isDiary() || custom.isAward()
                || custom.isDream() || custom.isMovie() || custom.isFood() || custom.isTravel()
                || custom.isAngry() || custom.isGift() || custom.isPromise();
        lineNote.setVisibility(isNote ? View.VISIBLE : View.GONE);
        ArrayList<Integer> dataNote = new ArrayList<>();
        if (custom.isWord()) dataNote.add(ModelAdapter.WORD);
        if (custom.isWhisper()) dataNote.add(ModelAdapter.WHISPER);
        if (custom.isAward()) dataNote.add(ModelAdapter.AWARD);
        if (custom.isDiary()) dataNote.add(ModelAdapter.DIARY);
        if (custom.isTravel()) dataNote.add(ModelAdapter.TRAVEL);
        if (custom.isAngry()) dataNote.add(ModelAdapter.ANGRY);
        if (custom.isGift()) dataNote.add(ModelAdapter.GIFT);
        if (custom.isPromise()) dataNote.add(ModelAdapter.PROMISE);
        if (custom.isDream()) dataNote.add(ModelAdapter.DREAM);
        if (custom.isMovie()) dataNote.add(ModelAdapter.MOVIE);
        if (custom.isFood()) dataNote.add(ModelAdapter.FOOD);
        rhNote.dataNew(dataNote);
        // media
        boolean isMedia = custom.isAudio() || custom.isVideo() || custom.isAlbum();
        lineMedia.setVisibility(isMedia ? View.VISIBLE : View.GONE);
        ArrayList<Integer> dataMedia = new ArrayList<>();
        if (custom.isAudio()) dataMedia.add(ModelAdapter.AUDIO);
        if (custom.isVideo()) dataMedia.add(ModelAdapter.VIDEO);
        if (custom.isAlbum()) dataMedia.add(ModelAdapter.ALBUM);
        rhMedia.dataNew(dataMedia);
        // other
        boolean isOther = custom.isTotal() || custom.isTrends();
        lineOther.setVisibility(isOther ? View.VISIBLE : View.GONE);
        ArrayList<Integer> dataOther = new ArrayList<>();
        if (custom.isCustom()) dataOther.add(ModelAdapter.CUSTOM);
        if (custom.isTotal()) dataOther.add(ModelAdapter.TOTAL);
        rhOther.dataNew(dataOther);
    }

    private void goActivity(BaseQuickAdapter adapter, int position) {
        if (refuseNext()) return;
        ModelAdapter modelAdapter = (ModelAdapter) adapter;
        modelAdapter.goActivity(position);
    }

    private void refreshData() {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
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
        Call<Result> api = new RetrofitHelper().call(API.class).noteHomeGet(near);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                lock = data.getLock();
                if (lock == null) {
                    // 默认没锁
                    lock = new Lock();
                    lock.setLock(false);
                }
                refreshMenu();
                refreshNoteView(data.getSouvenirLatest());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void refreshMenu() {
        boolean isLock = lock == null || lock.isLock();
        tb.getMenu().clear();
        tb.inflateMenu(isLock ? R.menu.help_lock_on_trends : R.menu.help_lock_off_trends);
    }

    private void refreshNoteView(Souvenir souvenirLatest) {
        if (mActivity == null || !mFragment.isAdded()) return; // 防止已经脱离后加载
        stopSouvenirCountDownTask(); // 先停止倒计时
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
        Calendar calNow = DateUtils.getCurrentCal();
        Calendar calHappen = DateUtils.getCal(TimeHelper.getJavaTimeByGo(souvenirLatest.getHappenAt()));
        Calendar calTrigger = DateUtils.getCal(TimeHelper.getJavaTimeByGo(souvenirLatest.getHappenAt()));
        calTrigger.set(Calendar.YEAR, calNow.get(Calendar.YEAR));
        if (calTrigger.getTimeInMillis() < calNow.getTimeInMillis()) {
            // 是明年的
            calTrigger.set(Calendar.YEAR, calTrigger.get(Calendar.YEAR) + 1);
        }
        int yearBetween = calTrigger.get(Calendar.YEAR) - calHappen.get(Calendar.YEAR);
        String yearShow = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_anniversary), yearBetween);
        // view
        tvSouvenirYear.setText(yearShow);
        tvSouvenirTitle.setText(title);
        // countdown
        MyApp.get().getHandler().post(getSouvenirCountDownTask(calTrigger.getTimeInMillis()));
    }

    private Runnable getSouvenirCountDownTask(final long triggerTime) {
        if (souvenirCountDownTask == null) {
            souvenirCountDownTask = new Runnable() {
                @Override
                public void run() {
                    if (mActivity == null || !mFragment.isAdded()) return; // 防止已经脱离后加载
                    long betweenTime = triggerTime - DateUtils.getCurrentLong();
                    if (betweenTime <= 0) {
                        stopSouvenirCountDownTask(); // 停止倒计时
                        refreshData(); // 刷新数据
                    } else {
                        if (betweenTime / TimeUnit.HOUR < 24) {
                            startLoveAnim();
                        } else {
                            stopLoveAnim();
                        }
                        tvSouvenirCountDown.setText(getSouvenirCountDownShow(betweenTime));
                        MyApp.get().getHandler().postDelayed(this, TimeUnit.SEC);
                    }
                }
            };
        }
        return souvenirCountDownTask;
    }

    private void stopSouvenirCountDownTask() {
        if (souvenirCountDownTask != null) {
            MyApp.get().getHandler().removeCallbacks(souvenirCountDownTask);
            souvenirCountDownTask = null;
        }
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

    private String getSouvenirCountDownShow(long betweenTime) {
        long day = betweenTime / TimeUnit.DAY;
        long hourTotal = betweenTime - (day * TimeUnit.DAY);
        long hour = hourTotal / TimeUnit.HOUR;
        long minTotal = hourTotal - (hour * TimeUnit.HOUR);
        long min = minTotal / TimeUnit.MIN;
        long secTotal = minTotal - (min * TimeUnit.MIN);
        long sec = secTotal / TimeUnit.SEC;
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
        String timeShow = dayShow + mActivity.getString(R.string.dayT) + " " + hourShow + ":" + minShow + ":" + secShow;
        return String.format(Locale.getDefault(), getString(R.string.count_down_space_holder), timeShow);
    }

}
