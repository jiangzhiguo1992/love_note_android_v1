package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.EntryAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Entry;
import com.jiangzg.lovenote_admin.domain.FiledInfo;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.ApiHelper;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class EntryListActivity extends BaseActivity<EntryListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etUid)
    EditText etUid;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnEnd)
    Button btnEnd;
    @BindView(R.id.btnTotalCreate)
    Button btnTotalCreate;
    @BindView(R.id.btnGroupCreate)
    Button btnGroupCreate;
    @BindView(R.id.btnTotalUpdate)
    Button btnTotalUpdate;
    @BindView(R.id.btnGroupUpdate)
    Button btnGroupUpdate;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page;
    private int cFiledIndex, uFiledIndex;
    private long start, end;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), EntryListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long uid) {
        Intent intent = new Intent(from, EntryListActivity.class);
        intent.putExtra("uid", uid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_entry_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "entry_list", true);
        // uid
        long uid = intent.getLongExtra("uid", 0);
        if (uid > 0) {
            etUid.setText(String.valueOf(uid));
        }
        // time
        start = DateUtils.getCurrentLong() - TimeUnit.DAY;
        end = DateUtils.getCurrentLong();
        refreshDateView();
        // filed
        cFiledIndex = uFiledIndex = 0;
        btnGroupCreate.setText("分组(" + ApiHelper.LIST_ENTRY_FILED_SHOW[cFiledIndex] + ")");
        btnGroupUpdate.setText("分组(" + ApiHelper.LIST_ENTRY_FILED_SHOW[uFiledIndex] + ")");
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new EntryAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getListData(true);
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        EntryAdapter entryAdapter = (EntryAdapter) adapter;
                        entryAdapter.goUser(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        getListData(false);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.btnSearch, R.id.btnStart, R.id.btnEnd,
            R.id.btnTotalCreate, R.id.btnGroupCreate, R.id.btnTotalUpdate, R.id.btnGroupUpdate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSearch:
                getListData(false);
                break;
            case R.id.btnStart:
                showStartPicker();
                break;
            case R.id.btnEnd:
                showEndPicker();
                break;
            case R.id.btnTotalCreate:
                getAllCount(true);
                break;
            case R.id.btnGroupCreate:
                showFiledSelectDialog(true);
                break;
            case R.id.btnTotalUpdate:
                getAllCount(false);
                break;
            case R.id.btnGroupUpdate:
                showFiledSelectDialog(false);
                break;
        }
    }

    private void showStartPicker() {
        DialogHelper.showDateTimePicker(mActivity, start, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                start = time;
                refreshDateView();
            }
        });
    }

    private void showEndPicker() {
        DialogHelper.showDateTimePicker(mActivity, end, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                end = time;
                refreshDateView();
            }
        });
    }

    private void refreshDateView() {
        String startAt = "s: " + DateUtils.getStr(start, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        String endAt = "e: " + DateUtils.getStr(end, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        btnStart.setText(startAt);
        btnEnd.setText(endAt);
    }

    private void getListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        long uid = 0;
        String trim = etUid.getText().toString().trim();
        if (StringUtils.isNumber(trim)) {
            uid = Long.valueOf(trim);
        }
        Call<Result> call = new RetrofitHelper().call(API.class).entryListGet(uid, page);
        MaterialDialog loading = null;
        if (!more) {
            loading = getLoading(true);
        }
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Entry> entryList = data.getEntryList();
                recyclerHelper.dataOk(entryList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

    private void getAllCount(final boolean create) {
        Call<Result> call = new RetrofitHelper().call(API.class).entryTotalGet(create, start / 1000, end / 1000);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (create) {
                    btnTotalCreate.setText("数量(" + data.getTotal() + ")");
                } else {
                    btnTotalUpdate.setText("数量(" + data.getTotal() + ")");
                }
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (create) {
                    btnTotalCreate.setText("数量(fail)");
                } else {
                    btnTotalUpdate.setText("数量(fail)");
                }
            }
        });
    }

    private void showFiledSelectDialog(final boolean create) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(ApiHelper.LIST_ENTRY_FILED_SHOW)
                .itemsCallbackSingleChoice(create ? cFiledIndex : uFiledIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (create) {
                            cFiledIndex = which;
                            btnGroupCreate.setText("分组(" + ApiHelper.LIST_ENTRY_FILED_SHOW[cFiledIndex] + ")");
                        } else {
                            uFiledIndex = which;
                            btnGroupUpdate.setText("分组(" + ApiHelper.LIST_ENTRY_FILED_SHOW[uFiledIndex] + ")");
                        }
                        DialogUtils.dismiss(dialog);
                        getFiledGroupData(create);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void getFiledGroupData(boolean create) {
        String filed = ApiHelper.LIST_ENTRY_FILED_SHOW[create ? cFiledIndex : uFiledIndex];
        Call<Result> call = new RetrofitHelper().call(API.class).entryGroupGet(filed, create, start / 1000, end / 1000);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                List<FiledInfo> infoList = data.getInfoList();
                DialogHelper.showFiledInfoDialog(mActivity, infoList);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
