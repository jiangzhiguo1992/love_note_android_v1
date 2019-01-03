package com.jiangzg.lovenote_admin.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.UserDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.SuggestComment;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/15.
 * 意见反馈评论适配器
 */
public class SuggestCommentAdapter extends BaseQuickAdapter<SuggestComment, BaseViewHolder> {

    private BaseActivity mActivity;

    public SuggestCommentAdapter(BaseActivity activity) {
        super(R.layout.list_item_suggest_comment);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, SuggestComment item) {
        // data
        String id = "id:" + item.getId();
        String uid = "uid:" + item.getUserId();
        String sid = "sid:" + item.getSuggestId();
        String official = item.isOfficial() ? mActivity.getString(R.string.official) : "";
        String create = DateUtils.getStr(item.getCreateAt() * 1000, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        String contentText = item.getContentText();
        // view
        helper.setText(R.id.tvOfficial, official);
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, uid);
        helper.setText(R.id.tvSid, sid);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvContent, contentText);
    }

    // 删除评论
    public void showDeleteDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delCommentApi(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delCommentApi(final int position) {
        final SuggestComment item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).setSuggestCommentDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
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

    public void goUserDetail(int position) {
        SuggestComment item = getItem(position);
        UserDetailActivity.goActivity(mActivity, item.getUserId());
    }


}
