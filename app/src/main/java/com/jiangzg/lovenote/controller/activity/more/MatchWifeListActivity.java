package com.jiangzg.lovenote.controller.activity.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.adapter.more.MatchWifeAdapter;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.media.PickHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.MatchWork;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.io.File;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class MatchWifeListActivity extends BaseActivity<MatchWifeListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.llTop)
    LinearLayout llTop;
    @BindView(R.id.llOrder)
    LinearLayout llOrder;
    @BindView(R.id.tvOrder)
    TextView tvOrder;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;

    private long pid;
    private boolean showNew;
    private RecyclerHelper recyclerHelper;
    private int page = 0;
    private int orderIndex;

    public static void goActivity(Fragment from, long pid) {
        if (pid <= 0) return;
        Intent intent = new Intent(from.getActivity(), MatchWifeListActivity.class);
        intent.putExtra("pid", pid);
        intent.putExtra("showNew", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long pid) {
        if (pid <= 0) return;
        Intent intent = new Intent(from, MatchWifeListActivity.class);
        intent.putExtra("pid", pid);
        intent.putExtra("showNew", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_match_wife_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.nav_wife), true);
        // search
        orderIndex = 0;
        tvOrder.setText(ApiHelper.LIST_MATCH_ORDER_SHOW[orderIndex]);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL))
                .initRefresh(srl, true)
                .initAdapter(new MatchWifeAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .viewAnim()
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        MatchWifeAdapter wifeAdapter = (MatchWifeAdapter) adapter;
                        wifeAdapter.showDeleteDialog(position);
                    }
                })
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        MatchWifeAdapter wifeAdapter = (MatchWifeAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.ivWork: // 大图
                                wifeAdapter.goWifeDetail(position);
                                break;
                            case R.id.llCoin: // 金币
                                wifeAdapter.coinAdd(position);
                                break;
                            case R.id.llPoint: // 点赞
                                wifeAdapter.pointToggle(position, true);
                                break;
                            case R.id.ivMore: // 举报
                                wifeAdapter.showReportDialog(position);
                                break;
                        }
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        pid = intent.getLongExtra("pid", 0);
        showNew = intent.getBooleanExtra("showNew", false);
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == BaseActivity.REQUEST_PICTURE) {
            // 相册
            File pictureFile = PickHelper.getResultFile(mActivity, data);
            if (FileUtils.isFileEmpty(pictureFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            ossUpload(pictureFile);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_MORE_MATCH);
                return true;
            case R.id.menuMenu: // 往期
                MatchWifeActivity.goActivity(mActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llTop, R.id.llOrder, R.id.llAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llTop: // 置顶
                rv.smoothScrollToPosition(0);
                break;
            case R.id.llOrder: // 搜索
                showSearchDialog();
                break;
            case R.id.llAdd: // 添加
                if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
                    CouplePairActivity.goActivity(mActivity);
                    return;
                }
                PickHelper.selectImage(mActivity, 1, true);
                break;
        }
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        int orderType = ApiHelper.LIST_MATCH_ORDER_TYPE[orderIndex];
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).moreMatchWordListGet(pid, orderType, page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataOk(data.getShow(), data.getMatchWorkList(), more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
        pushApi(api);
    }

    private void showSearchDialog() {
        String[] newSelectList = ApiHelper.LIST_MATCH_ORDER_SHOW;
        if (!showNew) {
            newSelectList = Arrays.copyOf(newSelectList, newSelectList.length - 1);
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(newSelectList)
                .itemsCallbackSingleChoice(orderIndex, (dialog1, view, which, text) -> {
                    if (recyclerHelper == null) return true;
                    orderIndex = which;
                    tvOrder.setText(ApiHelper.LIST_MATCH_ORDER_SHOW[orderIndex]);
                    recyclerHelper.dataRefresh();
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void ossUpload(File file) {
        OssHelper.uploadMoreMatch(mActivity, file, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                addApi(ossPath);
            }

            @Override
            public void failure(File source, String errMsg) {
            }
        });
    }

    private void addApi(String ossPath) {
        MatchWork body = new MatchWork();
        body.setMatchPeriodId(pid);
        body.setContentImage(ossPath);
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).moreMatchWorkAdd(body);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
