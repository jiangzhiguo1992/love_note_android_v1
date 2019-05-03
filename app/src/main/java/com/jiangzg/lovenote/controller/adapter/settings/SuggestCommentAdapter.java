package com.jiangzg.lovenote.controller.adapter.settings;

import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.ShowHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
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

    private BaseActivity mActivity;
    private final int colorGrey;
    private final int colorPrimary;

    public SuggestCommentAdapter(BaseActivity activity) {
        super(R.layout.list_item_suggest_comment);
        mActivity = activity;
        // color
        colorGrey = ContextCompat.getColor(mActivity, R.color.font_grey);
        colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
    }

    @Override
    protected void convert(BaseViewHolder helper, SuggestComment item) {
        // data
        boolean official = item.isOfficial();
        String admin = official ? mActivity.getString(R.string.administrators) : "";
        String create = ShowHelper.getBetweenTimeGoneShow(DateUtils.getCurrentLong() - TimeHelper.getJavaTimeByGo(item.getCreateAt()));
        String top = StringUtils.isEmpty(admin) ? create : String.format(Locale.getDefault(), mActivity.getString(R.string.holder_space_space_holder), admin, create);
        String contentText = item.getContentText();
        // view
        helper.setText(R.id.tvContent, contentText);
        helper.setText(R.id.tvTop, top);
        helper.setTextColor(R.id.tvTop, official ? colorPrimary : colorGrey);
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
                .content(R.string.confirm_del_this_comment)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> delCommentApi(position))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delCommentApi(final int position) {
        final SuggestComment item = getItem(position);
        Call<Result> api = new RetrofitHelper().call(API.class).setSuggestCommentDel(item.getId());
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                remove(position);
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SUGGEST_DETAIL_REFRESH, new Suggest()));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

}
