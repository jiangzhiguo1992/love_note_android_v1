package com.jiangzg.lovenote.controller.adapter.note;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.AwardRule;
import com.jiangzg.lovenote.model.entity.Couple;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/13.
 * 奖励规则适配器
 */
public class AwardRuleAdapter extends BaseQuickAdapter<AwardRule, BaseViewHolder> {

    private final Couple couple;
    private BaseActivity mActivity;

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
        String creator = UserHelper.getName(couple, item.getUserId());
        String createAt = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        // view
        helper.setText(R.id.tvScore, score);
        helper.setText(R.id.tvUseCount, useCount);
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvCreator, creator);
        helper.setText(R.id.tvCreateAt, createAt);
    }

    public void showDeleteDialog(final int position) {
        AwardRule item = getItem(position);
        if (!item.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_note));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_note)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> deleteApi(position))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi(int position) {
        final AwardRule item = getItem(position);
        Call<Result> api = new RetrofitHelper().call(API.class).noteAwardRuleDel(item.getId());
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_AWARD_RULE_LIST_ITEM_DELETE, item));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

    public void selectAwardRule(int position) {
        mActivity.finish(); // 必须先关闭
        AwardRule item = getItem(position);
        RxBus.post(new RxBus.Event<>(RxBus.EVENT_AWARD_RULE_SELECT, item));
    }

}
