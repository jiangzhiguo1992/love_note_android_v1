package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.ImgSquareShowAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class DiaryDetailActivity extends BaseActivity<DiaryDetailActivity> {

    private static final int FROM_NONE = 0;
    private static final int FROM_ID = 1;
    private static final int FROM_ALL = 2;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvAuthor)
    TextView tvAuthor;
    @BindView(R.id.tvUpdateAt)
    TextView tvUpdateAt;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tvContent)
    TextView tvContent;

    private Diary diary;
    private Call<Result> callGet;
    private Call<Result> callDel;
    private Observable<Diary> obDetailRefresh;

    public static void goActivity(Activity from, Diary diary) {
        Intent intent = new Intent(from, DiaryDetailActivity.class);
        intent.putExtra("from", FROM_ALL);
        intent.putExtra("diary", diary);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long did) {
        Intent intent = new Intent(from, DiaryDetailActivity.class);
        intent.putExtra("from", FROM_ID);
        intent.putExtra("did", did);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_diary_detail;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.diary), true);
        srl.setEnabled(false);
    }

    @Override
    protected void initData(Bundle state) {
        obDetailRefresh = RxBus.register(ConsHelper.EVENT_DIARY_DETAIL_REFRESH, new Action1<Diary>() {
            @Override
            public void call(Diary diary) {
                if (diary == null) return;
                refreshData(diary.getId());
            }
        });
        Intent intent = getIntent();
        int from = intent.getIntExtra("from", FROM_NONE);
        if (from == FROM_ALL) {
            diary = intent.getParcelableExtra("diary");
            refreshView();
        } else if (from == FROM_ID) {
            long did = intent.getLongExtra("did", 0);
            refreshData(did);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (diary == null) return super.onPrepareOptionsMenu(menu);
        menu.clear();
        if (diary.isMine()) {
            getMenuInflater().inflate(R.menu.help_del_edit, menu);
        } else {
            getMenuInflater().inflate(R.menu.help, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callDel);
        RetrofitHelper.cancel(callGet);
        RxBus.unregister(ConsHelper.EVENT_DIARY_DETAIL_REFRESH, obDetailRefresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_DIARY_DETAIL);
                return true;
            case R.id.menuEdit: // 编辑
                goEditActivity();
                return true;
            case R.id.menuDel: // 删除
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData(long did) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).diaryGet(did);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                diary = data.getDiary();
                refreshView();
            }

            @Override
            public void onFailure(String errMsg) {
                srl.setRefreshing(false);
            }
        });
    }

    private void refreshView() {
        if (diary == null) return;
        User user = SPHelper.getUser();
        long userId = diary.getUserId();
        long updateAt = diary.getUpdateAt();
        // menu
        invalidateOptionsMenu();
        // happen
        String happenAt = ConvertHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(diary.getHappenAt());
        tb.setTitle(happenAt);
        // author
        String authorName = user.getNameById(userId);
        String authorShow = String.format(Locale.getDefault(), getString(R.string.author_space_colon_space_holder), authorName);
        tvAuthor.setText(authorShow);
        // updateAt
        String update = ConvertHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(updateAt);
        String updateShow = String.format(Locale.getDefault(), getString(R.string.forward_edit_colon_space_holder), update);
        tvUpdateAt.setText(updateShow);
        // imageList
        List<String> imageList = diary.getImageList();
        if (imageList != null && imageList.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            int spanCount = imageList.size() > 3 ? 3 : imageList.size();
            new RecyclerHelper(mActivity)
                    .initRecycler(rv)
                    .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                    .initAdapter(new ImgSquareShowAdapter(mActivity, spanCount))
                    .setAdapter()
                    .dataNew(imageList, 0);
        } else {
            rv.setVisibility(View.GONE);
        }
        // content
        tvContent.setText(diary.getContent());
    }

    private void showDeleteDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_diary)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteApi();
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi() {
        if (diary == null) return;
        MaterialDialog loading = getLoading(true);
        callDel = new RetrofitHelper().call(API.class).diaryDel(diary.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Diary> event = new RxEvent<>(ConsHelper.EVENT_DIARY_LIST_ITEM_DELETE, diary);
                RxBus.post(event);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });

    }

    private void goEditActivity() {
        if (diary == null) return;
        DiaryEditActivity.goActivity(mActivity, diary);
    }

}
