package com.jiangzg.mianmian.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.AwardRule;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;

import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * 梦境适配器
 */
public class AwardRuleAdapter extends BaseQuickAdapter<AwardRule, BaseViewHolder> {

    private BaseActivity mActivity;
    private final Couple couple;

    public AwardRuleAdapter(BaseActivity activity) {
        super(R.layout.list_item_award_rule);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, AwardRule item) {
        String score = String.valueOf(item.getScore());
        if (item.getScore() > 0) {
            score = "+" + score;
        }
        String useCount = String.valueOf(item.getUseCount());
        String title = item.getTitle();
        String creator = Couple.getName(couple, item.getUserId());
        String creatorShow = String.format(Locale.getDefault(), mActivity.getString(R.string.creator_colon_space_holder), creator);
        String createAt = TimeHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(item.getCreateAt());
        String createShow = String.format(Locale.getDefault(), mActivity.getString(R.string.create_at_colon_space_holder), createAt);
        // view
        helper.setText(R.id.tvScore, score);
        helper.setText(R.id.tvUseCount, useCount);
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvCreator, creatorShow);
        helper.setText(R.id.tvCreateAt, createShow);
    }

    public void showDeleteDialog(final int position) {
        AwardRule item = getItem(position);
        if (!item.isMine()) return;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_rule)
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

    private void deleteApi(int position) {
        final AwardRule item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).awardRuleDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<AwardRule> event = new RxEvent<>(ConsHelper.EVENT_AWARD_RULE_LIST_ITEM_DELETE, item);
                RxBus.post(event);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    public void selectAwardRule(int position) {
        mActivity.finish(); // 必须先关闭
        AwardRule item = getItem(position);
        RxEvent<AwardRule> event = new RxEvent<>(ConsHelper.EVENT_AWARD_RULE_SELECT, item);
        RxBus.post(event);
    }

}
