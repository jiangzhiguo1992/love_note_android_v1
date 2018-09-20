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
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Post;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * Topic适配器
 */
public class PostAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {

    private BaseActivity mActivity;

    public PostAdapter(BaseActivity activity) {
        super(R.layout.list_item_post);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Post item) {
        // data
        String id = "id:" + item.getId();
        String uid = "uid:" + item.getUserId();
        String cid = "cid:" + item.getCoupleId();
        String kind = "kind:" + item.getKind();
        String subKind = "subKind:" + item.getSubKind();
        String create = "创建于:" + DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String update = "更新于:" + DateUtils.getString(item.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String title = item.getTitle();
        String contentText = item.getContentText();
        String longitude = "lon:" + item.getLongitude();
        String latitude = "lat:" + item.getLatitude();
        String address = item.getAddress();
        String reportCount = String.valueOf(item.getReportCount());
        String pointCount = String.valueOf(item.getPointCount());
        String collectCount = String.valueOf(item.getCollectCount());
        String commentCount = String.valueOf(item.getCommentCount());
        String official = item.isOfficial() ? "官方中" : "无官方";
        String top = item.isTop() ? "置顶中" : "无置顶";
        String well = item.isWell() ? "精华中" : "无精华";
        // view
        helper.setText(R.id.btnOfficial, official);
        helper.setText(R.id.btnTop, top);
        helper.setText(R.id.btnWell, well);
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, uid);
        helper.setText(R.id.tvCid, cid);
        helper.setText(R.id.tvKind, kind);
        helper.setText(R.id.tvSubKind, subKind);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvUpdate, update);
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvContent, contentText);
        helper.setText(R.id.tvLon, longitude);
        helper.setText(R.id.tvLat, latitude);
        helper.setText(R.id.tvAddress, address);
        helper.setText(R.id.tvReport, reportCount);
        helper.setText(R.id.tvPoint, pointCount);
        helper.setText(R.id.tvCollect, collectCount);
        helper.setText(R.id.tvComment, commentCount);
        // listener
        helper.addOnClickListener(R.id.btnOfficial);
        helper.addOnClickListener(R.id.btnTop);
        helper.addOnClickListener(R.id.btnWell);
    }

    public void goPostCommentList(int position) {
        Post item = getItem(position);
        PostCommentListActivity.goActivity(mActivity, 0, item.getId());
    }

    public void showDeleteDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_del_collect)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delCollect(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delCollect(final int position) {
        Post item = getItem(position);
        MaterialDialog loading = mActivity.getLoading(true);
        Call<Result> callCollect = new RetrofitHelper().call(API.class).topicPostDel(item.getId());
        RetrofitHelper.enqueue(callCollect, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    public void toggleOfficial(final int position) {
        final Post item = getItem(position);
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
                        updatePost(position, item);
                    }
                })
                .show();
    }

    public void toggleTop(final int position) {
        final Post item = getItem(position);
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
                        updatePost(position, item);
                    }
                })
                .show();
    }

    public void toggleWell(final int position) {
        final Post item = getItem(position);
        final boolean well = !item.isWell();
        DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content("修改为精华-" + well + "？")
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        item.setWell(well);
                        updatePost(position, item);
                    }
                })
                .show();
    }

    private void updatePost(final int position, Post body) {
        if (body == null) return;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).topicPostUpdate(body);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                Post post = data.getPost();
                setData(position, post);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
