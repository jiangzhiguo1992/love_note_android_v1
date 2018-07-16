package com.jiangzg.mianmian.adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.Suggest;
import com.jiangzg.mianmian.domain.SuggestComment;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/15.
 * 意见反馈评论适配器
 */
public class SuggestCommentAdapter extends BaseQuickAdapter<SuggestComment, BaseViewHolder> {

    private BaseActivity mActivity;
    private final String formatFloor;
    private final String formatOfficial;
    private final int colorGrey;
    private final int colorDark;
    private final int colorPrimary;

    public SuggestCommentAdapter(BaseActivity activity) {
        super(R.layout.list_item_suggest_comment);
        mActivity = activity;
        formatFloor = mActivity.getString(R.string.holder_floor);
        formatOfficial = mActivity.getString(R.string.official);

        colorGrey = ContextCompat.getColor(mActivity, R.color.font_grey);
        colorDark = ContextCompat.getColor(mActivity, ViewHelper.getColorDark(mActivity));
        colorPrimary = ContextCompat.getColor(mActivity, ViewHelper.getColorPrimary(mActivity));
    }

    @Override
    protected void convert(BaseViewHolder helper, SuggestComment item) {
        // data
        int layoutPosition = helper.getLayoutPosition();
        boolean official = item.isOfficial();
        boolean mine = item.isMine();
        String title = official ? formatOfficial : String.format(Locale.getDefault(), formatFloor, layoutPosition);
        String create = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(item.getCreateAt());
        String contentText = item.getContentText();
        // view
        helper.setText(R.id.tvTime, create);
        helper.setText(R.id.tvContent, contentText);
        TextView tvFloor = helper.getView(R.id.tvFloor);
        tvFloor.setText(title);
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
        SuggestComment item = getItem(position);
        if (!item.isMine()) {
            return;
        }
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
                // event
                RxEvent<Suggest> event = new RxEvent<>(ConsHelper.EVENT_SUGGEST_DETAIL_REFRESH, new Suggest());
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }


}
