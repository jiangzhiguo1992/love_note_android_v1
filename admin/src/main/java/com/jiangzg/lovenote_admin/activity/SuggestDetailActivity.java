package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.SuggestCommentAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Suggest;
import com.jiangzg.lovenote_admin.domain.SuggestComment;
import com.jiangzg.lovenote_admin.domain.SuggestInfo;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;
import com.jiangzg.lovenote_admin.view.FrescoView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import retrofit2.Call;

public class SuggestDetailActivity extends BaseActivity<SuggestDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.rv)
    RecyclerView rv;

    private Suggest suggest;
    private RecyclerHelper recyclerHelper;
    private int page;

    public static void goActivity(Activity from, Suggest suggest) {
        Intent intent = new Intent(from, SuggestDetailActivity.class);
        intent.putExtra("suggest", suggest);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_detail;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // init
        suggest = intent.getParcelableExtra("suggest_detail");
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new SuggestCommentAdapter(mActivity))
                .viewHeader(mActivity, R.layout.list_head_suggest_comment)
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getCommentData(true);
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        SuggestCommentAdapter commentAdapter = (SuggestCommentAdapter) adapter;
                        commentAdapter.goUserDetail(position);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        SuggestCommentAdapter commentAdapter = (SuggestCommentAdapter) adapter;
                        commentAdapter.showDeleteDialog(position);
                    }
                })
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        SuggestCommentAdapter commentAdapter = (SuggestCommentAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.tvTop:
                                commentAdapter.updateOfficial(position);
                                break;
                        }
                    }
                });
        // head
        initHead();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // refresh
        getCommentData(false);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    private void initHead() {
        // data
        if (suggest == null || recyclerHelper == null) return;
        boolean top = suggest.isTop();
        boolean official = suggest.isOfficial();
        boolean mine = suggest.isMine();
        String statusShow = SuggestInfo.getStatusShow(suggest.getStatus());
        String kindShow = SuggestInfo.getKindShow(suggest.getKind());
        String title = suggest.getTitle();
        String create = DateUtils.getString(suggest.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String createShow = String.format(Locale.getDefault(), getString(R.string.create_at_colon_space_holder), create);
        final String contentImgUrl = suggest.getContentImage();
        String contentText = suggest.getContentText();
        // view
        View head = recyclerHelper.getViewHead();
        TextView btnStatus = head.findViewById(R.id.btnStatus);
        TextView btnOfficial = head.findViewById(R.id.btnOfficial);
        TextView btnTop = head.findViewById(R.id.btnTop);
        TextView tvTitle = head.findViewById(R.id.tvTitle);
        TextView tvCreateAt = head.findViewById(R.id.tvCreateAt);
        TextView tvContent = head.findViewById(R.id.tvContent);
        FrescoView ivContent = head.findViewById(R.id.ivContent);
        ViewGroup.LayoutParams layoutParams = ivContent.getLayoutParams();
        ivContent.setWidthAndHeight(ScreenUtils.getScreenWidth(mActivity), layoutParams.height);
        // set
        btnStatus.setText(statusShow);
        btnOfficial.setText(official ? "official-ing" : "official-no");
        btnTop.setText(top ? "top-ing" : "top-no");
        tvTitle.setText(title);
        tvCreateAt.setText(createShow);
        tvContent.setText(contentText);
        if (StringUtils.isEmpty(contentImgUrl)) {
            ivContent.setVisibility(View.GONE);
            ivContent.setClickListener(null);
        } else {
            ivContent.setVisibility(View.VISIBLE);
            ivContent.setData(contentImgUrl);
        }
        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStatusSelectDialog();
            }
        });
        btnOfficial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggest2Official();
            }
        });
        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggest2Top();
            }
        });
    }

    private void getCommentData(final boolean more) {
        if (suggest == null) return;
        page = more ? page + 1 : 0;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).setSuggestCommentListGet(suggest.getId(), page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<SuggestComment> suggestCommentList = data.getSuggestCommentList();
                recyclerHelper.dataOk(suggestCommentList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

    private void showStatusSelectDialog() {
        if (suggest == null) return;
        int statusIndex = SuggestInfo.getStatusIndex(suggest.getStatus());

        final List<SuggestInfo.SuggestStatus> statusList = SuggestInfo.getInstance().getStatusList();
        CharSequence[] items = new CharSequence[statusList.size() - 1];
        for (int i = 1; i < statusList.size(); i++) {
            SuggestInfo.SuggestStatus s = statusList.get(i);
            // 第一个是全部，不要
            items[i - 1] = s.getShow();
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.please_select_classify)
                .items(items)
                .itemsCallbackSingleChoice(statusIndex - 1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        // 第一个忽略
                        SuggestInfo.SuggestStatus status = statusList.get(which + 1);
                        suggest2Status(status.getStatus());
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void suggest2Status(int status) {
        if (suggest == null) return;
        suggest.setStatus(status);
        updateSuggest(suggest);
    }

    private void suggest2Official() {
        if (suggest == null) return;
        final boolean official = !suggest.isOfficial();
        DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content("修改官方" + official + "？")
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (suggest == null) return;
                        suggest.setOfficial(official);
                        updateSuggest(suggest);
                    }
                })
                .show();
    }

    private void suggest2Top() {
        if (suggest == null) return;
        final boolean top = !suggest.isTop();
        DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content("修改置顶" + top + "？")
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (suggest == null) return;
                        suggest.setTop(top);
                        updateSuggest(suggest);
                    }
                })
                .show();
    }

    private void updateSuggest(Suggest body) {
        if (body == null) return;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).setSuggestUpdate(body);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                suggest = data.getSuggest();
                initHead();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
