package com.jiangzg.lovenote_admin.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Broadcast;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * broadcast适配器
 */
public class BroadcastAdapter extends BaseQuickAdapter<Broadcast, BaseViewHolder> {

    private BaseActivity mActivity;

    public BroadcastAdapter(BaseActivity activity) {
        super(R.layout.list_item_broadcast);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Broadcast item) {
        // data
        String id = "id:" + item.getId();
        String create = "create:" + DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String update = "update:" + DateUtils.getString(item.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String start = "start:" + DateUtils.getString(item.getStartAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String end = "end:" + (item.getEndAt() == 0 ? "永久" : DateUtils.getString(item.getEndAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M));
        String type = "(" + Broadcast.getTypeShow(item.getContentType()) + ")";
        String isEnd = String.valueOf(item.isEnd());
        String title = item.getTitle();
        String cover = "cover:" + item.getCover();
        String content = item.getContentText().replace("\\n", "\n");
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvUpdate, update);
        helper.setText(R.id.tvStart, start);
        helper.setText(R.id.tvEnd, end);
        helper.setText(R.id.tvType, type);
        helper.setText(R.id.tvIsEnd, isEnd);
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvCover, cover);
        helper.setText(R.id.tvContent, content);
    }

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
                        deleteApi(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi(final int position) {
        final Broadcast item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).broadcastDel(item.getId());
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

}
