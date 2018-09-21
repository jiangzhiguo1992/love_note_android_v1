package com.jiangzg.lovenote_admin.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.PostCommentListActivity;
import com.jiangzg.lovenote_admin.activity.UserDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.PostComment;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/15.
 * 意见反馈评论适配器
 */
public class PostCommentAdapter extends BaseQuickAdapter<PostComment, BaseViewHolder> {

    private BaseActivity mActivity;

    public PostCommentAdapter(BaseActivity activity) {
        super(R.layout.list_item_post_comment);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, PostComment item) {
        // data
        String official = item.isOfficial() ? mActivity.getString(R.string.official) : "";
        String id = "id:" + item.getId();
        String uid = "uid:" + item.getUserId();
        String cid = "cid:" + item.getCoupleId();
        String pid = "pid:" + item.getPostId();
        String tcid = "tcid:" + item.getToCommentId();
        String floor = "楼层:" + String.valueOf(item.getFloor());
        String create = "创建:" + DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String update = "更新:" + DateUtils.getString(item.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String contentText = item.getKind() == PostComment.KIND_JAB ? "【戳了一下】" : item.getContentText();
        String subCommentCount = String.valueOf(item.getSubCommentCount());
        String reportCount = String.valueOf(item.getReportCount());
        String pointCount = String.valueOf(item.getPointCount());
        // view
        helper.setText(R.id.tvOfficial, official);
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, uid);
        helper.setText(R.id.tvCid, cid);
        helper.setText(R.id.tvPid, pid);
        helper.setText(R.id.tvTcid, tcid);
        helper.setText(R.id.tvFloor, floor);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvUpdate, update);
        helper.setText(R.id.tvContent, contentText);
        helper.setText(R.id.tvReport, reportCount);
        helper.setText(R.id.tvComment, subCommentCount);
        helper.setText(R.id.tvPoint, pointCount);
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
        final PostComment item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).topicPostCommentDel(item.getId());
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
        PostComment item = getItem(position);
        if (item.getSubCommentCount() > 0) {
            PostCommentListActivity.goActivity(mActivity, 0, item.getPostId(), item.getId());
        } else {
            UserDetailActivity.goActivity(mActivity, item.getUserId());
        }
    }


}
