package com.jiangzg.lovenote_admin.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.SuggestCommentListActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Suggest;
import com.jiangzg.lovenote_admin.domain.SuggestInfo;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import java.util.List;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * 意见反馈适配器
 */
public class SuggestAdapter extends BaseQuickAdapter<Suggest, BaseViewHolder> {

    private BaseActivity mActivity;

    public SuggestAdapter(BaseActivity activity) {
        super(R.layout.list_item_suggest);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Suggest item) {
        // data
        String id = "id:" + item.getId();
        String uid = "uid:" + item.getUserId();
        String kind = "kind:" + SuggestInfo.getKindShow(item.getKind());
        String status = "status:" + SuggestInfo.getStatusShow(item.getStatus());
        String title = item.getTitle();
        String contentText = item.getContentText();
        String create = "创建于:" + DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String update = "更新于:" + DateUtils.getString(item.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        final long followCount = item.getFollowCount();
        String followShow = String.valueOf(followCount);
        long commentCount = item.getCommentCount();
        String commentShow = String.valueOf(commentCount);
        String statusShow = SuggestInfo.getStatusShow(item.getStatus());
        String official = item.isOfficial() ? "官方中" : "无官方";
        String top = item.isTop() ? "置顶中" : "无置顶";
        // view
        helper.setText(R.id.btnStatus, statusShow);
        helper.setText(R.id.btnOfficial, official);
        helper.setText(R.id.btnTop, top);
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, uid);
        helper.setText(R.id.tvKind, kind);
        helper.setText(R.id.tvStatus, status);
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvContent, contentText);
        helper.setText(R.id.tvCreateAt, create);
        helper.setText(R.id.tvUpdateAt, update);
        helper.setText(R.id.tvFollow, followShow);
        helper.setText(R.id.tvComment, commentShow);
        // listener
        helper.addOnClickListener(R.id.btnStatus);
        helper.addOnClickListener(R.id.btnOfficial);
        helper.addOnClickListener(R.id.btnTop);
    }

    public void goSuggestCommentList(int position) {
        Suggest item = getItem(position);
        SuggestCommentListActivity.goActivity(mActivity, 0, item.getId());
    }

    public void showDelDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_delete_this)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delSuggest(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    // 删除意见
    private void delSuggest(final int position) {
        Suggest item = getItem(position);
        MaterialDialog loading = mActivity.getLoading(true);
        Call<Result> call = new RetrofitHelper().call(API.class).setSuggestDel(item.getId());
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    public void showStatusSelectDialog(final int position) {
        final Suggest item = getItem(position);
        int statusIndex = SuggestInfo.getStatusIndex(item.getStatus());
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
                        item.setStatus(status.getStatus());
                        updateSuggest(position, item);
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    public void toggleOfficial(final int position) {
        final Suggest item = getItem(position);
        final boolean official = !item.isOfficial();
        DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content("修改为官方-" + official + "？")
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        item.setOfficial(official);
                        updateSuggest(position, item);
                    }
                })
                .show();
    }

    public void toggleTop(final int position) {
        final Suggest item = getItem(position);
        final boolean top = !item.isTop();
        DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content("修改为置顶-" + top + "？")
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        item.setTop(top);
                        updateSuggest(position, item);
                    }
                })
                .show();
    }

    private void updateSuggest(final int position, Suggest body) {
        if (body == null) return;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).setSuggestUpdate(body);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                Suggest suggest = data.getSuggest();
                setData(position, suggest);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
