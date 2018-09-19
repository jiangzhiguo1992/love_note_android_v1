package com.jiangzg.lovenote_admin.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.SuggestDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Suggest;
import com.jiangzg.lovenote_admin.domain.SuggestInfo;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

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
        // view
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
    }

    public void goSuggestDetail(int position) {
        Suggest item = getItem(position);
        SuggestDetailActivity.goActivity(mActivity, item);
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

}
