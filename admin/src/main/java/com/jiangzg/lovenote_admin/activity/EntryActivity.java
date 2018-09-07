package com.jiangzg.lovenote_admin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.EntryAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Entry;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.ApiHelper;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class EntryActivity extends BaseActivity<EntryActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etUid)
    EditText etUid;
    @BindView(R.id.btnListCreate)
    Button btnListCreate;
    @BindView(R.id.btnCount)
    Button btnCount;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.btnFiledCreate)
    Button btnFiledCreate;
    @BindView(R.id.btnFiled)
    Button btnFiled;
    @BindView(R.id.btnFiledSearch)
    Button btnFiledSearch;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page;
    private int filedIndex;
    private long listCreate, filedCreate;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), EntryActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_entry;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "entry", true);
        // uid
        long uid = intent.getLongExtra("uid", 0);
        if (uid > 0) {
            etUid.setText(String.valueOf(uid));
        }
        onInputChange();
        // time
        listCreate = filedCreate = DateUtils.getCurrentLong() - ConstantUtils.DAY;
        refreshDateView();
        // filed
        filedIndex = 0;
        btnFiled.setText(ApiHelper.LIST_ENTRY_FILEDS_SHOW[filedIndex]);
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
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnTextChanged({R.id.etUid})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnClick({R.id.btnListCreate, R.id.btnCount, R.id.btnSearch,
            R.id.btnFiledCreate, R.id.btnFiled, R.id.btnFiledSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnListCreate:
                showListCreatePicker();
                break;
            case R.id.btnCount:
                getAllCount();
                break;
            case R.id.btnSearch:
                getListData(false);
                break;
            case R.id.btnFiledCreate:
                showFiledCreatePicker();
                break;
            case R.id.btnFiled:
                showFiledSelectDialog();
                break;
            case R.id.btnFiledSearch:
                getFiledCountData();
                break;
        }
    }

    private void onInputChange() {
        String input = etUid.getText().toString().trim();
        if (StringUtils.isEmpty(input)) {
            btnCount.setEnabled(true);
            return;
        }
        if (StringUtils.isNumber(input)) {
            Long uid = Long.valueOf(input);
            if (uid <= 0) {
                btnCount.setEnabled(true);
                return;
            }
        }
        btnCount.setEnabled(false);
    }

    private void showListCreatePicker() {
        DialogHelper.showDatePicker(mActivity, listCreate, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                listCreate = time;
                refreshDateView();
            }
        });
    }

    private void showFiledCreatePicker() {
        DialogHelper.showDatePicker(mActivity, filedCreate, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                filedCreate = time;
                refreshDateView();
            }
        });
    }

    private void refreshDateView() {
        String listCreateAt = DateUtils.getString(listCreate, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String filedCreateAt = DateUtils.getString(filedCreate, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        btnListCreate.setText("c: " + listCreateAt);
        btnFiledCreate.setText("c: " + filedCreateAt);
    }

    private void getListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        long uid = 0;
        String trim = etUid.getText().toString().trim();
        if (StringUtils.isNumber(trim)) {
            uid = Long.valueOf(trim);
        }
        Call<Result> call;
        if (uid > 0) {
            call = new RetrofitHelper().call(API.class).entryUserListGet(uid, listCreate / 1000, page);
        } else {
            call = new RetrofitHelper().call(API.class).entryListGet(listCreate / 1000, page);
        }
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

    private void getAllCount() {
        Call<Result> call = new RetrofitHelper().call(API.class).entryCountGet(listCreate / 1000);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                btnCount.setText("数量(" + data.getTotal() + ")");
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                btnCount.setText("数量(fail)");
            }
        });
    }

    private void showFiledSelectDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(ApiHelper.LIST_ENTRY_FILEDS_SHOW)
                .itemsCallbackSingleChoice(filedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        filedIndex = which;
                        btnFiled.setText(ApiHelper.LIST_ENTRY_FILEDS_SHOW[filedIndex]);
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void getFiledCountData() {
        String filed = btnFiled.getText().toString().trim();
        Call<Result> call = new RetrofitHelper().call(API.class).entryCountGet(filedCreate / 1000, filed);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                Map<String, Long> countList = data.getCountList();
                showFiledResultDialog(countList);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void showFiledResultDialog(Map<String, Long> countList) {
        StringBuilder builder = new StringBuilder();
        if (countList == null || countList.size() <= 0) {
            builder.append("没有信息");
        } else {
            for (Map.Entry entry : countList.entrySet()) {
                builder.append(entry.getKey())
                        .append(" == (")
                        .append(entry.getValue())
                        .append(")")
                        .append("\n");
            }
        }
        String show = builder.toString();
        DialogHelper.getBuild(mActivity).content(show).show();
    }
}
