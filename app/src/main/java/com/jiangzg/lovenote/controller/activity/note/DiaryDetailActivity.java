package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.common.ImgSquareShowAdapter;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Diary;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;

public class DiaryDetailActivity extends BaseActivity<DiaryDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.ivAvatar)
    FrescoAvatarView ivAvatar;
    @BindView(R.id.tvTextCount)
    TextView tvTextCount;
    @BindView(R.id.tvReadCount)
    TextView tvReadCount;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tvContent)
    TextView tvContent;

    private Diary diary;
    private RecyclerHelper recyclerHelper;
    private Observable<Diary> obDetailRefresh;
    private Call<Result> callGet;
    private Call<Result> callDel;

    public static void goActivity(Activity from, Diary diary) {
        Intent intent = new Intent(from, DiaryDetailActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_OBJ);
        intent.putExtra("diary", diary);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long did) {
        Intent intent = new Intent(from, DiaryDetailActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_ID);
        intent.putExtra("did", did);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_diary_detail;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.diary), true);
        srl.setEnabled(false);
        // init
        int from = intent.getIntExtra("from", ConsHelper.ACT_DETAIL_FROM_ID);
        if (from == ConsHelper.ACT_DETAIL_FROM_OBJ) {
            diary = intent.getParcelableExtra("diary");
            refreshView();
            // 没有详情页的，可以不加
            if (diary != null) {
                refreshData(diary.getId());
            }
        } else if (from == ConsHelper.ACT_DETAIL_FROM_ID) {
            long did = intent.getLongExtra("did", 0);
            refreshData(did);
        }
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obDetailRefresh = RxBus.register(ConsHelper.EVENT_DIARY_DETAIL_REFRESH, diary -> {
            if (diary == null) return;
            refreshData(diary.getId());
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callDel);
        RetrofitHelper.cancel(callGet);
        RxBus.unregister(ConsHelper.EVENT_DIARY_DETAIL_REFRESH, obDetailRefresh);
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.del_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuEdit: // 编辑
                if (diary == null) return true;
                DiaryEditActivity.goActivity(mActivity, diary);
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
        callGet = new RetrofitHelper().call(API.class).noteDiaryGet(did);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                diary = data.getDiary();
                refreshView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void refreshView() {
        if (diary == null) return;
        User user = SPHelper.getMe();
        // happen
        String happenAt = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(diary.getHappenAt());
        tvHappenAt.setText(happenAt);
        // avatar
        String avatar = UserHelper.getAvatar(user, diary.getUserId());
        ivAvatar.setData(avatar);
        // textCount
        String content = diary.getContentText();
        String textFormat = mActivity.getString(R.string.text_number_space_colon_holder);
        String textCount = String.format(Locale.getDefault(), textFormat, content == null ? 0 : content.length());
        tvTextCount.setText(textCount);
        // readCount
        String readFormat = mActivity.getString(R.string.read_space_colon_holder);
        String readCount = String.format(Locale.getDefault(), readFormat, diary.getReadCount());
        tvReadCount.setText(readCount);
        // imageList
        List<String> imageList = diary.getContentImageList();
        if (imageList != null && imageList.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            int spanCount = imageList.size() > 3 ? 3 : imageList.size();
            if (recyclerHelper == null) {
                recyclerHelper = new RecyclerHelper(rv)
                        .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                        .initAdapter(new ImgSquareShowAdapter(mActivity, spanCount))
                        .setAdapter();
            }
            recyclerHelper.dataNew(imageList, 0);
        } else {
            rv.setVisibility(View.GONE);
        }
        // content
        tvContent.setText(content);
    }

    private void showDeleteDialog() {
        if (diary == null || !diary.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_diary));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_diary)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> deleteApi())
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi() {
        if (diary == null) return;
        MaterialDialog loading = getLoading(true);
        callDel = new RetrofitHelper().call(API.class).noteDiaryDel(diary.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_DIARY_LIST_ITEM_DELETE, diary));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
