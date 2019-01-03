package com.jiangzg.lovenote.controller.adapter.settings;

import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Suggest;
import com.jiangzg.lovenote.model.entity.SuggestComment;

import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/15.
 * 意见反馈评论适配器
 */
public class SuggestCommentAdapter extends BaseQuickAdapter<SuggestComment, BaseViewHolder> {

    private final int colorGrey;
    private final int colorDark;
    private final int colorPrimary;
    private BaseActivity mActivity;

    public SuggestCommentAdapter(BaseActivity activity) {
        super(R.layout.list_item_suggest_comment);
        mActivity = activity;

        colorGrey = ContextCompat.getColor(mActivity, R.color.font_grey);
        colorDark = ContextCompat.getColor(mActivity, ViewUtils.getColorDark(mActivity));
        colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
    }

    @Override
    protected void convert(BaseViewHolder helper, SuggestComment item) {
        // data
        boolean official = item.isOfficial();
        boolean mine = item.isMine();
        String floor = official ? mActivity.getString(R.string.administrators) : "";
        String create = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(item.getCreateAt());
        String top = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_space_space_holder), floor, create);
        String contentText = item.getContentText();
        // view
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
                .onPositive((dialog1, which) -> delCommentApi(position))
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
                RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_SUGGEST_DETAIL_REFRESH, new Suggest()));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }


}
