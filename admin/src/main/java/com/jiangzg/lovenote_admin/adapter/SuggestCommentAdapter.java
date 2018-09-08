package com.jiangzg.lovenote_admin.adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.SuggestComment;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/15.
 * 意见反馈评论适配器
 */
public class SuggestCommentAdapter extends BaseQuickAdapter<SuggestComment, BaseViewHolder> {

    private BaseActivity mActivity;
    private final int colorGrey;
    private final int colorDark;
    private final int colorPrimary;

    public SuggestCommentAdapter(BaseActivity activity) {
        super(R.layout.list_item_suggest_comment);
        mActivity = activity;
        colorGrey = ContextCompat.getColor(mActivity, R.color.font_grey);
        colorDark = ContextCompat.getColor(mActivity, ViewHelper.getColorDark(mActivity));
        colorPrimary = ContextCompat.getColor(mActivity, ViewHelper.getColorPrimary(mActivity));
    }

    @Override
    protected void convert(BaseViewHolder helper, SuggestComment item) {
        // data
        boolean official = item.isOfficial();
        boolean mine = item.isMine();
        String floor = official ? mActivity.getString(R.string.official) : "";
        String create = DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String top = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_space_space_holder), floor, create);
        String contentText = item.getContentText();
        // view
        helper.setText(R.id.tvId, "id：" + item.getId());
        helper.setText(R.id.tvUid, "uid：" + item.getUserId());
        helper.setText(R.id.tvContent, contentText);
        TextView tvFloor = helper.getView(R.id.tvTop);
        tvFloor.setText(top);
        if (official) {
            tvFloor.setTextColor(colorDark);
        } else if (mine) {
            tvFloor.setTextColor(colorPrimary);
        } else {
            tvFloor.setTextColor(colorGrey);
        }
    }

    // 删除评论
    public void showDeleteDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_del_comment)
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


}
